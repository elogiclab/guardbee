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
package com.elogiclab.guardbee.providers.oauth2.controller

import play.api.mvc.Controller
import play.api.Play.current
import play.api.mvc.Action
import com.elogiclab.guardbee.core.SecuredController
import com.elogiclab.guardbee.core.authz._
import com.elogiclab.guardbee.providers.oauth2.model.ClientApplication
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import com.elogiclab.guardbee.providers.oauth2.OAuth2Service._

class AuthorizationEndpoint extends SecuredController {
  
  implicit def scopeFormat: Formatter[Set[String]] = new Formatter[Set[String]] {
    def bind(key: String, data: Map[String, String]) = ???
    def unbind(key: String, value: Set[String]) = ???
  }
  
  
  val auth_form = Form(
      tuple(
          "client_id" -> of[ClientApplication],
          "response_type" -> text.verifying("guardbee.error.oauth2.invalid_response_type", {v => v == "token" || v == "code"}),
          "redirect_url" -> text,
          "scope" -> of[Set[String]],
          "state" -> optional(text))
          .verifying("guardbee.error.oauth2.invalid_scope", {v => v._1.scope.toSeq.containsSlice(v._4.toSeq)})
          .verifying("guardbee.error.oauth2.invalid_redirect_url", {v => v._1.redirectUrls.contains(v._3)})
  )
  
  def RedirectToError( client_id: String, redirect_uri: String ) = {
    ClientApplicationService.getClientApplication(client_id).fold({errors => ""}, {app => ""})
  }
  
  def auth() = Authorized(IsAuthenticated) { auth => implicit request =>
    auth_form.bindFromRequest.fold(
    errors => Redirect("", 2),
    value => ???
    )
    Ok
  }

}