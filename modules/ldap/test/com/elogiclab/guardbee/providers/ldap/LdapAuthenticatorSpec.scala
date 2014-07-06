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
package com.elogiclab.guardbee.providers.ldap

/**
 * @author Marco Sarti
 *
 */
import org.specs2.mutable.{Tags, Specification}
import play.api.test.WithApplication
import com.unboundid.ldap.sdk.LDAPConnection
import play.api.Logger
import play.api.test.FakeApplication
import play.api.test.FakeRequest
import com.elogiclab.guardbee.core.UsernamePasswordAuthenticationToken

/**
 * @author Marco Sarti
 *
 */
object LdapAuthenticatorSpec extends Specification with Tags {
  val logger = Logger("guardbee")


  "LdapAuthenticator" should {

    tag("ldap")
    "findDN must find DN" in new WithApplication(app = new FakeApplication(
      additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"),
      additionalConfiguration = Map("logger.guardbee-ldap" -> "DEBUG"))) {
      val clazz = new LdapAuthenticatorPlugin(app)
      clazz.connect.fold({ error =>
        logger.error(error.print)
        false must beTrue
      }, { connection =>
        val result1 = clazz.findDN(connection, "msarti")
        result1 must beRight
        result1.right.get.userDN must equalTo("uid=msarti,ou=People,dc=elogiclab,dc=com")

        val result2 = clazz.findDN(connection, "nouser")
        result2 must beLeft
      })

    }

    tag("ldap")
    "bindUser must authenticate" in new WithApplication(app = new FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
      val clazz = new LdapAuthenticatorPlugin(app)
      clazz.connect.fold({ error =>
        logger.error(error.print)
        false must beTrue
      }, { connection =>
        val result1 = clazz.bindUser(connection, "uid=msarti,ou=People,dc=elogiclab,dc=com", "123456")
        result1 must beRight
        val result2 = clazz.bindUser(connection, "uid=msarti,ou=People,dc=elogiclab,dc=com", "baspwd")
        result2 must beLeft
        val result3 = clazz.bindUser(connection, "uid=nouser,ou=People,dc=elogiclab,dc=com", "baspwd")
        result3 must beLeft
      })

    }

  }


  tag("ldap")
  "Should authenticate" in new WithApplication(app = new FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
    val plugin = new LdapAuthenticatorPlugin(app) {
      override def validateAccount(user: LdapUser) = Right(user)
    }
    val result = plugin.authenticate(UsernamePasswordAuthenticationToken("msarti", "123456", None))
    
    result must beRight
    
  }


  tag("ldap")
  "Should NOT authenticate if account is invalid" in new WithApplication(app = new FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
    val plugin = new LdapAuthenticatorPlugin(app) {
      override def validateAccount(user: LdapUser) = Left(LdapInvalidAccountError)
    }
    val result = plugin.authenticate(UsernamePasswordAuthenticationToken("msarti", "123456", None))
    
    result must beLeft
    
  }



  tag("ldap")
  "Should NOT authenticate with bad credentials" in new WithApplication(app = new FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin"))) {
    val plugin = new LdapAuthenticatorPlugin(app)
    val result = plugin.authenticate(UsernamePasswordAuthenticationToken("msarti", "bad", None))
    
    result must beLeft
    
  }


  tag("ldap")
  "Should NOT authenticate with blank password" in new WithApplication {
    val plugin = new LdapAuthenticatorPlugin(app)
    val result = plugin.authenticate(UsernamePasswordAuthenticationToken("msarti", "", None))
    
    result must beLeft
    
  }

  tag("ldap")
  "Should NOT authenticate if user does not exist" in new WithApplication {
    val plugin = new LdapAuthenticatorPlugin(app)
    val result = plugin.authenticate(UsernamePasswordAuthenticationToken("notexists", "dummy", None))
    
    result must beLeft
    
  }

}