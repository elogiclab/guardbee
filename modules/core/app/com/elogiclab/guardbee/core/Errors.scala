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

import play.api.mvc.Request
import play.api.i18n.Messages
import play.api.data.FormError


case class Msg(message: String, args: Any*)


/**
 * @author Marco Sarti
 *
 */
case class Errors(messages: Seq[Msg]) {
  
  def add(message: Msg) = {
    Errors(message +: messages)
  }
  
  def +(message: Msg) = add(message)
  
  def printHTML[A](implicit request: Request[A]) = {
    "<ul><li>"+messages.map( { msg => Messages(msg.message, msg.args:_*)} ).mkString("</li><li>")+"</li></ul>"
  }
  
  def print = {
    messages.map( { msg => Messages(msg.message, msg.args:_*)} ).mkString(" - ")
  }
  
}

trait ErrorResults {
  
  val AuthenticationError = Errors(Seq(Msg("guardbee.error.authentication_error")))
  def InternalError(error_code: ErrorCodes.ErrorCode) = Errors(Seq(Msg("guardbee.error.internal", error_code)))
  val AuthenticationFailedError = Errors(Seq(Msg("guardbee.error.authenticationFailed")))
}


object Errors {
  
  val AuthenticationError = Errors(Seq(Msg("guardbee.error.authentication_error")))
  
  def FormErrors(formErrors: Seq[FormError]) = {
    Errors(formErrors.map { e => Msg(e.message, e.args:_*)})
  }
  
  def apply(message: String, args: Any*):Errors = Errors(Seq(Msg(message, args:_*))) 
  
  
}





object ErrorCodes extends Enumeration {
  type ErrorCode = Value
  
  val ERROR_LDAP_UNAVAILABLE = Value
  
}
