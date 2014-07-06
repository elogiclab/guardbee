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
package com.elogiclab.guardbee.providers.oauth2

import play.api.Application
import play.api.Plugin
import play.api.Logger
import play.api.Play

trait OAuth2Service {

}

object OAuth2Service {

  private def plugin = (for {
    app <- Play.maybeApplication
    plugin <- app.plugin[OAuth2ServicePlugin]
  } yield plugin).getOrElse(sys.error("Could not load OAuth2ServicePlugin plugin"))
  
  def ClientApplicationService = plugin.ClientApplicationService
  
  

}

class OAuth2ServicePlugin(app: Application) extends Plugin with OAuth2Service {
  val logger = Logger("guardbee")

  override def onStart = {
    logger.info("Starting OAuth2ServicePlugin plugin")
  }
  
  lazy val ClientApplicationService = app.plugin[ClientApplicationService].getOrElse {
    sys.error("No ClientApplicationService is defined. Please activate a ClientApplicationService plugin ")
  }

} 