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
package com.elogiclab.guardbee.providers.oauth2.model

import org.joda.time.DateTime
import play.api.data.format.Formatter
import play.api.data.FormError
import play.api.data.format.Formats
import com.elogiclab.guardbee.providers.oauth2.OAuth2Service
import com.elogiclab.guardbee.core.Errors

case class ClientApplication(
  clientId: String,
  secret: String,
  redirectUrls: Set[String],
  scope: Set[String],
  owner: String,
  enabled: Boolean,
  implicitFlowEnabled: Boolean,
  creationTime: DateTime,
  expirationTime: Option[DateTime])

object ClientApplication {

  implicit def clientIdFormat: Formatter[ClientApplication] = new Formatter[ClientApplication] {

    def toClientApplication(client_id: String): Option[ClientApplication] = {
      OAuth2Service.ClientApplicationService.getClientApplication(client_id).fold({ errors =>
        None
      }, { value =>
        Some(value)
      })
    }

    private def parsing[T](parse: String => Option[T], errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
      Formats.stringFormat.bind(key, data).right.flatMap { s =>
        parse(s).map(Right(_)).getOrElse(Left(Seq(FormError(key, errMsg, errArgs))))
      }
    }

    def bind(key: String, data: Map[String, String]) = {
      parsing(toClientApplication, "error.invalid_client_id", Nil)(key, data)
    }
    def unbind(key: String, value: ClientApplication) = Map(key -> value.clientId)
  }

}

