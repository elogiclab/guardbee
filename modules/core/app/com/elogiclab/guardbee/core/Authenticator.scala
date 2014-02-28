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

import play.api.mvc.RequestHeader
import play.api.mvc.Request
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data.Forms._
import play.api.data.Form

/**
 * @author Marco Sarti
 *
 */
trait Authenticator {

  def ProviderId: String

  type AuthenticationToken

  def obtainDestinationUrl[A](request: Request[A]): Option[String] = {
		Form(
			single("dest" -> optional(text))
		).bindFromRequest()(request).fold({ errors =>
		  None
		}, { value => 
		  value
		})
  }

  def obtainCredentials[A](request: Request[A]): Either[Errors, AuthenticationToken]

  def authenticate(authToken: AuthenticationToken): Either[Errors, Authentication]

  def performAuthentication[A](request: Request[A]): Either[Errors, Authentication] = {
    for (
      auth_token <- obtainCredentials(request).right;
      authentication <- authenticate(auth_token).right
    ) yield authentication
  }

}