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
package com.elogiclab.guardbee.core.authz

import org.specs2.mutable.Specification
import com.elogiclab.guardbee.core.Authentication
import org.joda.time.DateTime
import play.api.test.WithApplication
import play.api.test.FakeApplication

/**
 * @author Marco Sarti
 *
 */
object AuthzSpec extends Specification {
  
  object auth extends Authentication("username", "provider", None) {
    override val granted_roles = Seq("ROLE_USER", "ROLE_GUEST")
  }
  val expiredAuth = Authentication("username", "provider", None, DateTime.now.minusDays(10))
  
  def TRUE(aut: Authentication) = true
  def FALSE(aut: Authentication) = false
  
  "authz" should {
    "pass if authentication is valid" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      IsAuthenticated(auth) must beTrue
    }
    "fail if authentication is expired" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      IsAuthenticated(expiredAuth) must beFalse
    }
    "AllOf must pass if all elements return true" in {
      AllOf(TRUE, TRUE)(auth) must beTrue
    }
    "AllOf must fail if one or more element return false" in {
      AllOf(TRUE, FALSE, TRUE)(auth) must beFalse
      AllOf(FALSE, TRUE, FALSE)(auth) must beFalse
      AllOf()(auth) must beFalse
    }
    "AtLeastOneOf must pass if one or more element return true" in {
      AtLeastOneOf(TRUE, FALSE, TRUE)(auth) must beTrue
      AtLeastOneOf(FALSE, TRUE, FALSE)(auth) must beTrue
    }
    "AtLeastOneOf must fail if no element return true" in {
      AtLeastOneOf(FALSE, FALSE, FALSE)(auth) must beFalse
      AtLeastOneOf()(auth) must beFalse
    }
    "MajorityOf must pass if the majority of element return true" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      MajorityOf(TRUE, FALSE, TRUE)(auth) must beTrue
      MajorityOf(FALSE, TRUE, TRUE, TRUE)(auth) must beTrue
    }
    "MajorityOf must fail if the majority of element does not return true" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      MajorityOf(FALSE, FALSE, FALSE)(auth) must beFalse
      MajorityOf(FALSE, FALSE, TRUE)(auth) must beFalse
      MajorityOf(FALSE, FALSE, TRUE, TRUE)(auth) must beFalse
      MajorityOf(FALSE, FALSE, FALSE, TRUE)(auth) must beFalse
      MajorityOf()(auth) must beFalse
    }
    "HasRole must pass if role is ROLE_USER" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      HasRole("ROLE_USER")(auth) must beTrue
    }
    
    "HasRole must fail if role is ROLE_ADMIN" in new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      HasRole("ROLE_ADMIN")(auth) must beFalse
    }
    
  }

}