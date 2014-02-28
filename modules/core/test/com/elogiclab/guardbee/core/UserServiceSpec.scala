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

import org.specs2.mutable.Specification
import play.api.Application
import play.api.test.WithApplication
import play.api.test.FakeApplication
import org.joda.time.DateTime

  case class TestUser(
  username: String,
  fullName: String,
  email: String,
  enabled: Boolean,
  expirationDate: Option[DateTime]
  ) extends User
  
  case class OtherTestUser(
  username: String,
  fullName: String,
  email: String,
  enabled: Boolean,
  expirationDate: Option[DateTime]
  ) extends User


class TestUserService(app:Application) extends UserService[TestUser](app) {
  val testUser = TestUser("username","fullname", "mail@example.com", false, None)
  
  def getByUsername(username: String): Option[TestUser] = Some(testUser)
  
  def getByEmail(email: String): Option[TestUser] = Some(testUser)
  def obtainPassword(username: String): Option[Password] = Some(Password("bcrypt", password="pwd", None))
  def createUser(user: User, roles: Seq[String]): Either[Errors, TestUser] = Right(testUser)
  def updatePassword(username: String, password: Password): Either[Errors, Unit] = Right(Unit)
  def unlockUser(username: String): Either[Errors, TestUser] = Right(testUser)
  def lockUser(username: String): Either[Errors, TestUser] = Right(testUser)
  def getGrantedRoles(username:String): Seq[String] = ???
}



/**
 * @author Marco Sarti
 *
 */
object UserServiceSpec extends Specification {
  "UserService" should {
    "cast correctly to the valid User class" in 
    new WithApplication(app = FakeApplication(additionalPlugins = Seq("com.elogiclab.guardbee.core.GuardbeeServicePlugin", "com.elogiclab.guardbee.core.TestUserService"))) {
      def assign1():TestUser = GuardbeeService.UserService[TestUser].getByUsername("").get
      def assign2():User = GuardbeeService.UserService[User].getByUsername("").get
      def assign3():OtherTestUser = GuardbeeService.UserService[OtherTestUser].getByUsername("").get
      
      
      assign1 must not( throwA[ClassCastException] )
      assign2 must not( throwA[ClassCastException] )
      assign3 must throwA[ClassCastException]
      
      def use1() = GuardbeeService.UserService[User].createUser(new User {
		  val username: String = ""
		  val fullName: String = ""
		  def email: String = ""
		  
		  val enabled: Boolean = true
		  val expirationDate = None
      }, Nil)
      
      use1 must not( throwA[ClassCastException] )
      
    }
    
    
  }
  
  
  
  

}