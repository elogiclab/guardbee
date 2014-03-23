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
import org.joda.time.DateTime

/**
 * @author Marco Sarti
 *
 */

trait RegistrationToken {
  self =>
  
    
  def email: String
  def token: String
  def expirationTime: DateTime
  
  
  def isExpired = DateTime.now.isAfter(self.expirationTime)
  
  
}

class RegistrationServiceConfig(app: Application) extends Configuration(app) {
  
}





abstract class RegistrationService(app: Application) extends RegistrationServiceConfig(app) with Plugin {
  
  
  def findTokenByEmail(email: String): Option[RegistrationToken]
  def consumeToken(token: String): Option[RegistrationToken]
  def saveToken(token: RegistrationToken): RegistrationToken
  
  
  
  
  
  

}