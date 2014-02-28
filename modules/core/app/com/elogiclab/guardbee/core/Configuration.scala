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

/**
 * @author Marco Sarti
 *
 */
class Configuration(application: Application) {
  implicit val app = application

  val applicationKey = "guardbee"

  lazy val SessionTimeout = application.configuration.getInt(applicationKey + ".session.timeout").getOrElse(60)

  lazy val CookieName = application.configuration.getString(applicationKey + ".cookie.name").getOrElse("GUARDBEE_SESSION")
  lazy val CookiePath = application.configuration.getString(applicationKey + ".cookie.path").getOrElse("/")
  lazy val CookieSecure = application.configuration.getBoolean(applicationKey + ".cookie.secure").getOrElse(false)
  lazy val CookieExpiration = application.configuration.getInt(applicationKey + ".cookie.expiration").getOrElse(3600)

  lazy val ApplicationName = application.configuration.getString("application.name").getOrElse("Guardbee")

  lazy val BcryptLogRounds = application.configuration.getInt(applicationKey + ".bcrypt.log_round").getOrElse(10)

  lazy val DefaultProfileRoles: Seq[String] = {
    import scala.collection.JavaConverters._
    application.configuration.getStringList(applicationKey + ".google.defaultProfileRoles").map { l =>
      l.asScala.toSeq
    }.getOrElse(Seq("ROLE_USER"))
  }

}