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

import scala.concurrent.Future
import scala.collection.immutable.Map
import play.api.i18n.Lang
import play.api.libs.iteratee.Enumeratee
import scala.Option
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Security
import play.api.mvc.RequestHeader
import play.api.mvc.Results
import play.api.mvc.Action
import play.api.mvc.Cookie
import play.api.http.MimeTypes

/**
 * @author Marco Sarti
 *
 */
trait SecuredController extends Controller {

  def getAuthentication(request: RequestHeader) = GuardbeeService.getAuthentication(request).filter(a => !a.isExpired)
  def onUnauthorized(mimeType: String)(request: RequestHeader) = {
    Results.Redirect(RoutesHelper.loginPage(request.path))
  }
  def onForbidden(mimeType: String)(request: RequestHeader) = Results.Redirect("/")
  def saveAuthentication(authentication: Authentication): Cookie = GuardbeeService.saveAuthentication(authentication)

  def Authorized(authz: Authentication => Boolean, mimeType: String = MimeTypes.HTML)(f: => Authentication => Request[AnyContent] => Result) =
    Security.Authenticated[Authentication](getAuthentication, onUnauthorized(mimeType)) { authentication =>
      authz(authentication) match {
        case true => Action(request => f(authentication)(request).withCookies(saveAuthentication(authentication.touch)))
        case false => Action(request => onForbidden(mimeType)(request))
      }
    }

}