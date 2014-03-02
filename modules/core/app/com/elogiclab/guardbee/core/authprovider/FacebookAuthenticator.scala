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
package com.elogiclab.guardbee.core.authprovider

import scala.util.Either
import com.elogiclab.guardbee.core.Authenticator
import play.api.mvc.RequestHeader
import play.api.Application
import play.api.Plugin
import com.elogiclab.guardbee.core.Errors
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Request
import play.api.Logger
import com.elogiclab.guardbee.core.Msg
import com.elogiclab.guardbee.core.Authentication
import play.api.libs.ws.WS
import play.api.libs.ws.Response
import com.elogiclab.guardbee.core.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Await
import com.elogiclab.guardbee.core.Authentication
import com.elogiclab.guardbee.core.AuthenticationToken

case class FacebookAuthenticationToken(code: String, remember_me: Option[Boolean])

/**
 * @author Marco Sarti
 *
 */
class FacebookAuthenticatorPlugin(app: Application) extends Authenticator with Plugin {
  val logger = Logger("guardbee")

  val ProviderId: String = "facebook"

  case class FacebookReponse(status: Int, access_token: String, expires: Int)

  def parseResponse(response: Response) = {
    val Pattern = "access_token=([A-Za-z0-9]*)&expires=([0-9]*)$".r
    response.status match {
      case 200 => {
        response.body match {
          case Pattern(access_token, expires) => Right(FacebookReponse(response.status, access_token, expires.toInt))
          case _ => Left(Errors(Seq(Msg("guardbee.error"))))
        }
      }
      case _ => Left(Errors(Seq(Msg("guardbee.error"))))
    }
  }
  def retrieveUserinfo(access_token: String): Either[Errors, User] = {
    import scala.concurrent.duration._
    val future = WS.url("https://graph.facebook.com/me").withQueryString(("access_token", access_token)).get.map { response =>
      response.status match {
        case 200 => {
          Right(new User {
            val username = (response.json \ "email").as[String]
            val email = (response.json \ "email").as[String]
            val enabled = true
            val expirationDate = None
            val fullName = (response.json \ "name").as[String]
          })
        }
        case _ => Left(Errors(Seq(Msg("guardbee.error"))))
      }
    }
    Await.result(future, Duration.Inf)
  }

  def loginToFacebook(code: String): Either[Errors, User] = {
    import scala.concurrent.duration._
    val future = WS.url("https://graph.facebook.com/oauth/access_token")
      .withQueryString(("client_id", "client_id"), ("redirect_uri", "http://localhost:9000/callback/facebook"),
        ("code", code), ("client_secret", "secret")).get.map { response =>
          for (
            fbresponse <- parseResponse(response).right;
            user <- retrieveUserinfo(fbresponse.access_token).right
          ) yield user
        }
    Await.result(future, Duration.Inf)
  }

  def authenticate(authToken: FacebookAuthenticationToken): Either[Errors, Authentication] = {
    loginToFacebook(authToken.code).fold({ errors =>
      Left(errors)
    }, { user =>
      Right(Authentication(username = user.username, provider = ProviderId, scope = None, remember_me = authToken.remember_me.getOrElse(false)))
    })
  }

  def performAuthentication(authToken: AuthenticationToken): Either[Errors, Authentication] = {
    ???
  }

}