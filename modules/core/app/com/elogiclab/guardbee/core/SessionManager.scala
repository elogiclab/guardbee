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

import play.api.mvc.Result
import play.api.mvc.RequestHeader
import play.api.Application
import play.api.Plugin
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime
import play.api.libs.Crypto
import play.api.mvc.Cookie
import play.api.Logger

/**
 * @author Marco Sarti
 *
 */
trait SessionManager {

  def saveAuthentication(authentication: Authentication): Cookie

  def getAuthentication(request: RequestHeader): Option[Authentication]

  def removeAuthentication(result: => Result): Result
  

}

class DefaultSessionManagerPlugin(app: Application) extends Plugin with SessionManager {
  
  val logger = Logger("guardbee")
  
  val df = org.joda.time.format.ISODateTimeFormat.dateTime

  implicit val authenticationReads = (
    (__ \ "username").read[String] and
    (__ \ "provider").read[String] and
    (__ \ "scope").read[Option[Seq[String]]] and
    (__ \ "lastAccess").read[String].fmap[DateTime](dt => df.parseDateTime(dt))  and
    (__ \ "remember_me").read[Boolean])(Authentication.apply _)

  implicit val authenticationWriters = (
    (__ \ "username").write[String] and
    (__ \ "provider").write[String] and
    (__ \ "scope").write[Option[Seq[String]]] and
    (__ \ "lastAccess").write[String].contramap[DateTime](dt => df.print(dt)) and
    (__ \ "remember_me").write[Boolean])(unlift(Authentication.unapply))

  private def serializeAndSign(auth: Authentication): String = {
    val value = Json.stringify(Json.toJson(auth))
    Crypto.sign(value) + "-" + value
  }

  private def verifyAndDeserialize(value: String): Option[Authentication] = {
    val json = if (value.length > 40) value.substring(41) else ""
    val sign = if (value.length > 40) value.substring(0, 40) else ""
    val expected = Crypto.sign(json)
    expected == sign match {
      case true => {
        logger.debug("Cookie signature verified")
        val jsvalue = Json.parse(json)
        jsvalue.validate[Authentication] match {
          case auth: JsSuccess[Authentication] => Some(auth.get)
          case _ => {
        	logger.warn("Cookie object not validated!")
            None
          }
        }
      }
      case _ => {
       	logger.warn("Cookie signature NOT verified")
        None
      }
    }
  }

  def saveAuthentication(authentication: Authentication): Cookie = {

    import GuardbeeService.Configuration
    val maxAge = authentication.remember_me match {
      case true => Some(Configuration.CookieExpiration)
      case false => None
    }

    val cookie = Cookie(
      name = Configuration.CookieName,
      path = Configuration.CookiePath,
      maxAge = maxAge,
      value = serializeAndSign(authentication),
      httpOnly = true,
      secure = Configuration.CookieSecure)

    cookie
  }

  def getAuthentication(request: RequestHeader): Option[Authentication] = {
    import GuardbeeService.Configuration
    request.cookies.get(Configuration.CookieName).map{ cookie =>
      val authOpt = verifyAndDeserialize(cookie.value)
      logger.debug(authOpt.map {auth => "Authentication found - lastAccess: "+auth.lastAccess+", isExpired: "+auth.isExpired}.getOrElse("Authentication invalid"))
      authOpt
    }.getOrElse {
      logger.debug("Authentication not present")
      None
    }
  }

  def removeAuthentication(result: => Result): Result = {
    import GuardbeeService.Configuration
    val cookie = Cookie(
      name = Configuration.CookieName,
      path = Configuration.CookiePath,
      maxAge = Some(-1),
      value = "",
      httpOnly = true,
      secure = Configuration.CookieSecure)  
      result.withCookies(cookie)
  }

}