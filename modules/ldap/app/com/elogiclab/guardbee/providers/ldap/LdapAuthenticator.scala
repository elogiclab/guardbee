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

import com.elogiclab.guardbee.core.Authenticator
import play.api.Application
import play.api.Plugin
import play.api.Logger
import com.unboundid.ldap.sdk.Filter
import com.unboundid.ldap.sdk.SearchRequest
import com.unboundid.ldap.sdk.SearchScope
import com.unboundid.ldap.sdk.LDAPConnection
import com.elogiclab.guardbee.core.Errors
import com.elogiclab.guardbee.core.ErrorCodes._
import com.elogiclab.guardbee.core.Authentication
import play.api.mvc.Request
import com.elogiclab.guardbee.core.User
import play.api.data.Form
import play.api.data.Forms._
import com.elogiclab.guardbee.core.Msg
import org.joda.time.DateTime
import com.elogiclab.guardbee.core.UsernamePasswordAuthenticationToken
import com.elogiclab.guardbee.core.UsernamePasswordAuthenticator


trait LdapUser extends User {
  def userDN: String
}

/**
 * @author Marco Sarti
 *
 */
class LdapAuthenticatorPlugin(app: Application) extends LdapConfiguration(app) with UsernamePasswordAuthenticator with Plugin {
  val ProviderId = "ldap"

  def connect: Either[Errors, LDAPConnection] = {
    try {
      logger.debug("Connecting to " + LDAPHost + ":" + LDAPPort)
      Right(new LDAPConnection(LDAPHost, LDAPPort))
    } catch {
      case e: Throwable => {
        logger.error("Error connecting to LDAP server: " + e.getMessage())
        Left(Errors("guardbee.error.internal", ERROR_LDAP_UNAVAILABLE))
      }
    }
  }

  def findDN(connection: LDAPConnection, userid: String): Either[Errors, LdapUser] = {
    try {
      connection.bind(LDAPBindUser, LDAPBindPassword)
      logger.error("Connected")
      val strFilter = LDAPUserFilter.format(userid)
      logger.error(strFilter)

      val filter = Filter.create(strFilter)
      val searchRequest = new SearchRequest(LDAPSearchBase, SearchScope.SUB, filter)
      val searchResult = connection.search(searchRequest)

      logger.error("results: " + searchResult.getEntryCount)

      searchResult match {
        case found if found.getEntryCount > 0 => {

          val entry = found.getSearchEntries().get(0)

          logger.error("entry: " + entry.getDN())

          (Option(entry.getAttribute(LDAPUsernameAttr)), Option(entry.getAttribute(LDAPemailAttr))) match {
            case (Some(ldap_username), Some(ldap_email_attr)) => {
              Right(new LdapUser {
                val username = ldap_username.getValue()
                val fullName = ldap_username.getValue()
                val email = ldap_email_attr.getValue()
                val enabled = true
                val expirationDate = None
                val userDN = entry.getDN()
              })
            }
            case (None,_) => {
              logger.error("Error creating user for " + userid + "; missing attribute " + entry.getAttribute(LDAPUsernameAttr))
              Left(Errors("guardbee.error.ldap.missingAttribute", LDAPUsernameAttr))
            }
            case (_,None) => {
              logger.error("Error creating user for " + userid + "; missing attribute " + entry.getAttribute(LDAPemailAttr))
              Left(Errors("guardbee.error.ldap.missingAttribute", LDAPemailAttr))
            }

          }
        }
        case _ => Left(Errors("guardbee.error.authenticationFailed"))
      }

    } catch {
      case e: Throwable => {
        logger.error("Error retrieving user " + userid + ": " + e.getMessage())
        Left(Errors("guardbee.error.authenticationFailed"))
      }
    }
  }

  def bindUser(connection: LDAPConnection, userDN: String, password: String) = {

    try {
      val result = connection.bind(userDN, password)
      Right(Unit)
    } catch {
      case e: Throwable => {
        logger.error("User " + userDN + " authentication failed: " + e.getMessage())
        Left(Errors("guardbee.error.authenticationFailed"))
      }
    }
  }


	def createAuthentication(u: LdapUser, remember_me:Option[Boolean]): Either[Errors, Authentication] = {
	  Right(Authentication(u.username, ProviderId, None, DateTime.now, remember_me.getOrElse(false)))
	}
  
  
  def authenticate(authToken: UsernamePasswordAuthenticationToken): Either[Errors, Authentication] = {
    logger.debug(authToken.username + " attempts LDAP authentication")
    
    
    val result = connect.fold({ errors =>
      Left(errors)
    }, { connection =>
      logger.debug("Connected to LDAP")
      val auth = for(
          user <- findDN(connection, authToken.username).right;
          result <- bindUser(connection, user.userDN, authToken.password).right;
          authentication <- createAuthentication(user, authToken.remember_me).right
      ) yield authentication
      connection.close
      auth
    })
    
    result
  }

}