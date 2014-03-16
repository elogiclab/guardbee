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

import play.api.Play
import play.api.Application
import com.elogiclab.guardbee.core.Configuration

/**
 * @author Marco Sarti
 *
 */
class LdapConfiguration(application: Application) extends Configuration(application) {
  
  
    //LDAP
  lazy val LDAPHost = application.configuration.getString(applicationKey + ".ldap.host").getOrElse("localhost")
  lazy val LDAPPort = application.configuration.getInt(applicationKey + ".ldap.port").getOrElse(389)
  lazy val LDAPBindUser = application.configuration.getString(applicationKey + ".ldap.bindUser").getOrElse("cn=admin,dc=elogiclab,dc=com")
  lazy val LDAPBindPassword = application.configuration.getString(applicationKey + ".ldap.bindPassword").getOrElse("password")
  lazy val LDAPUserFilter = application.configuration.getString(applicationKey + ".ldap.userFilter").getOrElse("(&(objectClass=inetOrgPerson)(cn=%s))")
  lazy val LDAPemailAttr = application.configuration.getString(applicationKey + ".ldap.emailAttr").getOrElse("mail")
  lazy val LDAPUsernameAttr = application.configuration.getString(applicationKey + ".ldap.usernameAttr").getOrElse("uid")
  lazy val LDAPSearchBase = application.configuration.getString(applicationKey + ".ldap.searchBase").getOrElse("dc=elogiclab,dc=com")
  lazy val LDAPRequiresExistingAccount = application.configuration.getBoolean(applicationKey + ".ldap.requiresExistingAccount").getOrElse(false)

}

