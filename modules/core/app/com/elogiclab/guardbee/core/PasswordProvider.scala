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
import org.mindrot.jbcrypt.BCrypt
import com.elogiclab.guardbee.core.GuardbeeService._
import play.api.Logger

/**
 * @author Marco Sarti
 *
 */
trait PasswordProvider {
  
  def ProviderId: String

  def hash(plainpassword: String): Password

  def matches(candidate: String, password: Password): Boolean

}

class BcryptPasswordProvider(application: Application) extends PasswordProvider with Plugin {
  
  val ProviderId = "bcrypt"
  val logger = Logger("guardbee")

  def hash(plainpassword: String): Password = {
    Password(ProviderId, BCrypt.hashpw(plainpassword, BCrypt.gensalt(Configuration.BcryptLogRounds)), None)
  }

  def matches(candidate: String, password: Password): Boolean = {
        logger.debug("Password checking ")
    password.provider match {
      case p if p == ProviderId => {
        val result = BCrypt.checkpw(candidate, password.password)
        logger.debug("Password check result: "+result)
        result
      }
      case _ => {
        logger.debug(password.provider+" not managed by this provider")
        false
      }
    }
  }

}