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
package com.elogiclab.guardbee.core.authprovider

import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.test.WithApplication
import com.elogiclab.guardbee.core.Password
import org.mindrot.jbcrypt.BCrypt
import com.elogiclab.guardbee.core.User
import org.joda.time.DateTime


/**
 * @author Marco Sarti
 *
 */
object LocalAccountAuthenticatorSpec extends Specification {
  
  "LocalAccountAuthenticator" should {
    "Should obtain credentials fron request" in new WithApplication {
      val request = FakeRequest("POST","/").withFormUrlEncodedBody(("username", "username"), ("password", "password"))
      val plugin = new LocalAccountAuthenticatorPlugin(app)
      val token = plugin.obtainCredentials(request)
      token.isRight must beTrue
      token.right.get.username must equalTo("username")
      token.right.get.password must equalTo("password")
    }
  
    "should authenticate" in new WithApplication {
      
      val plugin = new LocalAccountAuthenticatorPlugin(app) {
		  override def obtainPassword(username: String) = Some(Password("bcrypt", BCrypt.hashpw("password", BCrypt.gensalt(10)), None))
		  override def getByUsername(u: String) = Some(new User {
		      def username: String = u
			  def fullName: String = "fullname"
			  def email: String = "test@example.org"
			  
			  def enabled: Boolean = true
			  def expirationDate: Option[DateTime] = None
		  })
		  override def matchPassword(candidate: String, pwd: Password) = BCrypt.checkpw(candidate, pwd.password)
      }
      
      
      val token = LocalAccountAuthenticationToken("username", "password", None)
      
      val auth = plugin.authenticate(token)
      
      auth.isRight should beTrue
      
    }
  
  
  }
  
  
  

}