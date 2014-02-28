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

import play.api.Application
import play.api.Plugin
import play.api.Play
import play.api.Logger
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import scala.reflect.ClassTag

trait GuardbeeService

/**
 * @author Marco Sarti
 *
 */
class GuardbeeServicePlugin(app: Application) extends Plugin with GuardbeeService {
  val logger = Logger("guardbee")
  
  override def onStart = {
    logger.info("Starting GuardbeeService plugin")
  }

  object Configuration extends Configuration(app)

  private lazy val sessionManager = app.plugin[SessionManager].getOrElse {
    val plugin = new DefaultSessionManagerPlugin(app)
    logger.info("Using the default SessionManager");
    plugin
  }

  def getAuthentication(request: RequestHeader) = {
    sessionManager.getAuthentication(request).filterNot(a => a.isExpired)
  }

  def saveAuthentication(authentication: Authentication) = sessionManager.saveAuthentication(authentication.touch)

  def removeAuthentication(result: => Result) = sessionManager.removeAuthentication(result)

  lazy val Authenticators: Map[String, Authenticator] = {
    app.plugins.filter(p => classOf[Authenticator].isAssignableFrom(p.getClass)).map(p => (p.asInstanceOf[Authenticator].ProviderId, p.asInstanceOf[Authenticator])).toMap
  }

  def UserService[T <: User] = app.plugin[UserService[T]].getOrElse(sys.error("Could not load UserService plugin"))
  
  lazy val TemplateManager = app.plugin[TemplateManager].getOrElse {
    val plugin = new DefaultTemplateManagerPlugin(app)
    logger.info("Using the default TemplateManager");
    plugin
  }
  
  lazy val PasswordProvider = app.plugin[PasswordProvider].getOrElse {
    val plugin = new BcryptPasswordProvider(app)
    logger.info("Using the default BcryptPasswordProvider");
    plugin
  }
}

object GuardbeeService extends GuardbeeService {

  private def plugin = (for {
    app <- Play.maybeApplication
    plugin <- app.plugin[GuardbeeServicePlugin]
  } yield plugin).getOrElse(sys.error("Could not load GuardbeeService plugin"))

  def Configuration = plugin.Configuration

  def getAuthentication(request: RequestHeader) = plugin.getAuthentication(request)

  def saveAuthentication(authentication: Authentication) = plugin.saveAuthentication(authentication)

  def removeAuthentication(result: => Result) = plugin.removeAuthentication(result)

  def UserService[T <: User] = plugin.UserService[T]
  
  def TemplateManager: TemplateManager = plugin.TemplateManager
  
  def Authenticators: Map[String, Authenticator] = plugin.Authenticators
  
  def PasswordProvider = plugin.PasswordProvider

}