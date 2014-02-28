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

import play.api.Application

/**
 * @author Marco Sarti
 *
 */
class GoogleConfiguration(application: Application) {
  
  val applicationKey = "guardbee"
  
  //Google OAuth2 config
  lazy val GoogleOauth2AuthURL = application.configuration.getString(applicationKey + ".google.OAuth2AuthURL").getOrElse("https://accounts.google.com/o/oauth2/auth")
  lazy val GoogleOauth2TokenURL = application.configuration.getString(applicationKey + ".google.OAuth2TokenURL").getOrElse("https://accounts.google.com/o/oauth2/token")
  lazy val GoogleOauth2RedirectURL = application.configuration.getString(applicationKey + ".google.OAuth2RedirectURL").getOrElse("http://localhost:9000/oauthcallback/google")
  lazy val GoogleOauth2Scope = application.configuration.getString(applicationKey + ".google.OAuth2Scope")
  .getOrElse("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email")
  lazy val GoogleOauth2ClientID = application.configuration.getString(applicationKey + ".google.OAuth2ClientID").getOrElse("unknown")
  lazy val GoogleOauth2Secret = application.configuration.getString(applicationKey + ".google.OAuth2Secret").getOrElse("unknown")
  
  lazy val GoogleCreateProfile = application.configuration.getBoolean(applicationKey + ".google.createProfile").getOrElse(false)
  
  lazy val GoogleDefaultProfileRoles:Seq[String] = {
    import scala.collection.JavaConverters._
    application.configuration.getStringList(applicationKey + ".google.defaultProfileRoles").map { l =>
      l.asScala.toSeq
    }.getOrElse(Seq("ROLE_USER"))
  }
}