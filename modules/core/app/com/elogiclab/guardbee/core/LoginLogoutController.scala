/**
 * Copyright (c) 2014 Marco Sarti <marco.sarti at gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.elogiclab.guardbee.core

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext
import scala.util.matching.Regex
import play.api.libs.ws.Response
import scala.concurrent._
import play.api.mvc.Result
import play.api.Logger
import play.api.i18n.Messages
/**
 * @author Marco Sarti
 *
 */
object LoginLogoutController extends Controller {

  val logger = Logger("guardbee")

  val LoginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "remember-me" -> optional(boolean))(UsernamePasswordAuthenticationToken.apply)(UsernamePasswordAuthenticationToken.unapply))

  val OAuth2Form = Form(
    mapping(
      "code" -> optional(text),
      "error" -> optional(text),
      "state" -> optional(text))(OAuth2AuthenticationToken.apply)(OAuth2AuthenticationToken.unapply))

  def loginPage(dest: Option[String]) = Action { implicit request =>
    Ok(GuardbeeService.TemplateManager.loginPage(LoginForm, dest.getOrElse("/")))
  }

  def login(provider: String, dest: Option[String]) = Action { implicit request =>
    LoginForm.bindFromRequest()(request).fold({ failed =>
      val errors = Errors.FormErrors(failed.errors)
      logger.debug("Failed to bind request: " + errors.print)
      Redirect(RoutesHelper.loginPage(dest.orElse(Some("/")))).flashing("error" -> errors.printHTML)
    }, { token =>
      GuardbeeService.Authenticators.get(provider).map { authenticator: Authenticator =>
        logger.debug("Attempt to authenticate: provider '" + provider + "'")
        authenticator.performAuthentication(token).fold({ errors =>
          logger.debug("Authentication failed: " + errors.print)
          Redirect(RoutesHelper.loginPage(dest.orElse(Some("/")))).flashing("error" -> errors.printHTML)
        }, { authentication =>
          logger.info("User '" + authentication.username + "' authenticated")
          Redirect(dest.getOrElse("/")).withCookies(GuardbeeService.saveAuthentication(authentication))
        })
      }.getOrElse {
        logger.error("The provider "+provider+" is not supported")
        InternalServerError
      }
    })
  }

  def loginWith(provider: String, dest: String) = Action { implicit request =>
    logger.debug("dest = " + dest)

    GuardbeeService.Authenticators.get(provider).map { authenticator: Authenticator =>
      authenticator match {
        case oauth2: OAuth2Client => {
          val redirect = oauth2.getAuthURL
          logger.debug("queryString = " + redirect.queryString)
          Redirect(redirect.url, redirect.queryString)
        }
        case _ => InternalServerError
      }
    }.getOrElse(InternalServerError)
  }

  def oauthCallback(provider: String, state: Option[String]) = Action { implicit request =>
    OAuth2Form.bindFromRequest()(request).fold({ failed =>
      val errors = Errors.FormErrors(failed.errors)
      logger.debug("Error binding request: " + errors.print)
      Redirect(RoutesHelper.loginPage(state.orElse(Some("/")))).flashing("error" -> errors.printHTML)
    }, { token =>
      GuardbeeService.Authenticators.get(provider).map { authenticator: Authenticator =>
        logger.debug("Attempt to authenticate: provider '" + provider + "'")
        authenticator.performAuthentication(token).fold({ errors =>
          logger.debug("Authentication failed: " + errors.print)
          Redirect(RoutesHelper.loginPage(state.orElse(Some("/")))).flashing("error" -> errors.printHTML)
        }, { authentication =>
          logger.info("User '" + authentication.username + "' authenticated")
          Redirect(state.getOrElse("/")).withCookies(GuardbeeService.saveAuthentication(authentication))
        })
      }.getOrElse(InternalServerError)
    })
  }

  def logout() = Action { request =>
    GuardbeeService.removeAuthentication(Redirect("/"))
  }
}