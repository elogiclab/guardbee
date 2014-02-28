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

import play.api.Application
import com.elogiclab.guardbee.core.Authenticator
import play.api.Plugin
import play.api.mvc.Request
import com.elogiclab.guardbee.core.Errors
import com.elogiclab.guardbee.core.Authentication
import play.api.data.Form
import play.api.data.Forms._
import com.elogiclab.guardbee.core.Msg
import play.api.Logger
import com.elogiclab.guardbee.core.User
import com.elogiclab.guardbee.core.Authentication
import org.joda.time.DateTime
import com.elogiclab.guardbee.core.Password

case class LocalAccountAuthenticationToken(username: String, password: String, remember_me: Option[Boolean])

/**
 * @author Marco Sarti
 *
 */
class LocalAccountAuthenticatorPlugin(app: Application) extends Authenticator with Plugin {
  val ProviderId = "local"
  val logger = Logger("guardbee")

  type AuthenticationToken = LocalAccountAuthenticationToken

  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "remember-me" -> optional(boolean))(LocalAccountAuthenticationToken.apply)(LocalAccountAuthenticationToken.unapply))

  def obtainCredentials[A](request: Request[A]): Either[Errors, LocalAccountAuthenticationToken] = {
    form.bindFromRequest()(request).fold({ err =>
      logger.debug("Errors obtaining credentials from request: " + err.errors.map(f => f.key + "->" + f.message).mkString)
      val errors = err.errors.map { e =>
        Msg(e.message, e.args)
      }
      Left(Errors(errors))
    }, { success =>
      Right(success)
    })

  }
  
  import com.elogiclab.guardbee.core.GuardbeeService._
  def obtainPassword(username: String) = UserService[User].obtainPassword(username)
  def getByUsername(username: String) = UserService[User].getByUsername(username)
  def matchPassword(candidate: String, pwd: Password) = PasswordProvider.matches(candidate, pwd)

  def authenticate(authToken: LocalAccountAuthenticationToken): Either[Errors, Authentication] = {
    logger.debug("User " + authToken.username + " attempt to authenticate")

    val principal = for (
      pwd <- obtainPassword(authToken.username);
      candidate <- Some(authToken.password) if matchPassword(candidate, pwd);
      user <- getByUsername(authToken.username) if user.isValid
    ) yield user
    
    principal map { u =>
      Right(Authentication(u.username, ProviderId, None, DateTime.now, authToken.remember_me.getOrElse(false)))
    } getOrElse(Left(Errors.AuthenticationError))
  }

}