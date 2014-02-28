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
package services

import play.api.Application
import com.elogiclab.guardbee.core._
import play.api.db.DB
import anorm._
import play.api.Play.current
import java.util.Date
import anorm.SqlParser._
import org.joda.time.DateTime
import java.sql.Connection
import play.api.Logger

/**
 * @author Marco Sarti
 *
 */
class UserServicePlugin(app: Application) extends UserService[BasicUser](app) {
  val logger = Logger("guardbee")
  
  val simple = get[String]("users.username") ~
    get[Option[String]]("users.password") ~
    get[String]("users.fullName") ~
    get[String]("users.email") ~
    get[Boolean]("users.enabled") ~
    get[Option[Date]]("users.expirationDate") map {
      case username ~ password ~ fullName ~ email ~ enabled ~ expirationDate =>
        BasicUser(username, password, fullName, email, enabled, expirationDate.map(new DateTime(_)))
    }

  def createUser(user: User, roles: Seq[String]): Either[Errors, BasicUser] = {
    logger.debug("Creating user '"+user.username+"', roles "+roles.mkString(","))
    
    val result = BasicUser(user.username, None, user.fullName, user.email, user.enabled, user.expirationDate)
    DB.withTransaction { implicit connection =>
      SQL("""insert into users (username, password, fullName, email, enabled, expirationDate) 
	        values ({username}, {password}, {fullName}, {email}, {enabled}, {expirationDate}) """).on(
        'username -> result.username,
        'password -> result.password,
        'fullName -> result.fullName,
        'email -> result.email,
        'enabled -> result.enabled,
        'expirationDate -> result.expirationDate.map(_.toDate)).executeUpdate()
      roles.map { role =>
        SQL("insert into user_roles( username, role ) values ({username}, {role}) ").on(
          'username -> result.username,
          'role -> role
        ).executeUpdate()
      }
    
    }
    Right(result)
  }
  def getByEmail(email: String): Option[BasicUser] = {
    DB.withConnection { implicit connection =>
      SQL(
        """select * from users where email = {email}""").on('email -> email)
        .as(simple singleOpt)
    }
  }
  def getByUsername(username: String): Option[BasicUser] = {
    DB.withConnection { implicit connection =>
      SQL(
        """select * from users where username = {username}""").on('username -> username)
        .as(simple singleOpt)
    }
  }

  def updateUser(user: BasicUser)(implicit connection: Connection): Either[Errors, BasicUser] = {
    SQL("""update users set password = {password}, fullName = {fullName}, email = {email}, enabled = {enabled}, expirationDate = {expirationDate} 
          where username = {username} """).on(
      'username -> user.username,
      'password -> user.password,
      'fullName -> user.fullName,
      'email -> user.email,
      'enabled -> user.enabled,
      'expirationDate -> user.expirationDate.map(_.toDate)).executeUpdate()
    Right(user)
  }

  def lockUser(username: String): Either[Errors, BasicUser] = {
    DB.withTransaction { implicit connection =>
      getByUsername(username).map { user =>
        updateUser(user.copy(enabled = false))
      }.getOrElse(Left(Errors(Seq(Msg("guardbee.error.user_notfound")))))
    }
  }
  def obtainPassword(username: String): Option[Password] = {
    getByUsername(username).map { user =>
      logger.debug("Password ok for user "+user.password)
      user.password.map(Password("bcrypt", _, None))
    }.getOrElse {
      logger.debug("Password not found for user "+username)
      None
    }
  }
  def unlockUser(username: String): Either[Errors, BasicUser] = {
    DB.withTransaction { implicit connection =>
      getByUsername(username).map { user =>
        updateUser(user.copy(enabled = true))
      }.getOrElse(Left(Errors(Seq(Msg("guardbee.error.user_notfound")))))
    }
  }
  def updatePassword(username: String, password: Password): Either[Errors, Unit] = {
    DB.withTransaction { implicit connection =>
      getByUsername(username).map { user =>
        updateUser(user.copy(password = Some(password.password)))
        Right()
      }.getOrElse(Left(Errors(Seq(Msg("guardbee.error.user_notfound")))))
    }
  }
  
  def getGrantedRoles(username: String): Seq[String] = Nil
  
  
}