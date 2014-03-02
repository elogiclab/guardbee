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
package com.elogiclab.guardbee.providers.google

import com.elogiclab.guardbee.core.Authenticator
import play.api.Application
import play.api.Plugin
import play.api.mvc.Request
import com.elogiclab.guardbee.core.Errors
import com.elogiclab.guardbee.core.Authentication
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WS
import com.elogiclab.guardbee.core.OAuth2Client
import com.elogiclab.guardbee.core.RedirectURL
import play.api.data.validation.Constraint
import play.api.data.validation.Valid
import play.api.data.validation.Invalid
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.elogiclab.guardbee.core.OAuth2AccessToken
import org.joda.time.DateTime
import com.elogiclab.guardbee.core.User
import com.elogiclab.guardbee.core.Authentication
import com.elogiclab.guardbee.core.GuardbeeService
import com.elogiclab.guardbee.core.OAuth2AuthenticationToken
import com.elogiclab.guardbee.core.OAuth2Authenticator

case class GoogleAccessToken()

/**
 * @author Marco Sarti
 *
 */
class GoogleAuthenticatorPlugin(app: Application) extends GoogleConfiguration(app) with OAuth2Authenticator with Plugin with OAuth2Client {
  val ProviderId: String = "google"

  val TokenContstraint = Constraint[OAuth2AuthenticationToken] { t: OAuth2AuthenticationToken =>
    t.code.map { c =>
      Valid
    }.getOrElse(Invalid("guardbee.googleauth.error.unauthorized", t.error.getOrElse("Unknown")))
  }


  def authenticate(authToken: OAuth2AuthenticationToken): Either[Errors, Authentication] = {
    def getAuthentication(user: User): Either[Errors, Authentication] = Right(Authentication(user.username, ProviderId, None))
    
    def verifyAccount(user: User) : Either[Errors, User] = {
      GuardbeeService.UserService[User].getByEmail(user.email).filter(_.isValid).map { u =>
        Right(u)
      }.getOrElse {
        logger.info("Account "+user.email+" not found. Cannot authorize this Google account, please register user before attempt to login")
        Left(Errors("guardbee.error.accountNotFound"))
      }
    }
    

    authToken.code.map { c =>
      for (
        access_token <- getAccessToken(c).right;
        profile <- getProfile(access_token.access_token).right;
        user <- verifyAccount(profile).right;
        authentication <- getAuthentication(user).right
      ) yield authentication
    }.getOrElse(Left(Errors("guardbee.googleauth.error.unauthorized", "invalid_code")))
  }

  def getAuthURL[A](implicit request: Request[A]) = {
    
    val state = Form(
    		single("dest" -> optional(text))
    ).bindFromRequest()(request).fold({ e =>
      None
    }, { value =>
      value
    }).getOrElse("/")
    
    RedirectURL(GoogleOauth2AuthURL,
    Map(
      "client_id" -> Seq(GoogleOauth2ClientID),
      "redirect_uri" -> Seq(GoogleOauth2RedirectURL),
      "response_type" -> Seq("code"),
      "scope" -> Seq(GoogleOauth2Scope),
      "state" -> Seq(state)))
  }

  def getAccessToken(code: String): Either[Errors, OAuth2AccessToken] = {
    val future = WS.url(GoogleOauth2TokenURL)
      .post(Map(
        "client_id" -> Seq(GoogleOauth2ClientID),
        "client_secret" -> Seq(GoogleOauth2Secret),
        "code" -> Seq(code),
        "redirect_uri" -> Seq(GoogleOauth2RedirectURL),
        "grant_type" -> Seq("authorization_code"))).map { response =>
        response.status match {
          case 200 => {
            Right(OAuth2AccessToken(
              (response.json \ "access_token").as[String],
              (response.json \ "token_type").as[String],
              DateTime.now.plusMinutes((response.json \ "expires_in").as[Int])))
          }
          case _ => {
            Left(Errors("guardbee.googleauth.error.unauthorized", (response.json \ "error").as[String]))
          }
        }
      }
    Await.result(future, Duration.Inf)
  }

  def getProfile(access_token: String): Either[Errors, User] = {
    val future = WS.url("https://www.googleapis.com/oauth2/v1/userinfo").withQueryString(("alt", "json"), ("access_token", access_token))
      .get.map { response =>
        response.status match {
          case 200 => {
            Right(new User {
              val username = (response.json \ "email").as[String]
              val fullName = (response.json \ "name").as[String]
              val email = (response.json \ "email").as[String]
              val enabled = true
              val expirationDate = None
            })
          }
          case _ =>
            val e = (response.json \ "error" \ "message").as[String]
            Left(Errors("guardbee.googleauth.error.unauthorized", e))
        }
      }
    Await.result(future, Duration.Inf)
  }

}