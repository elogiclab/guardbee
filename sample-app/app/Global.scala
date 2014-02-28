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
/**
 * @author Marco Sarti
 *
 */
import play.api._
import com.elogiclab.guardbee.core.GuardbeeService
import com.elogiclab.guardbee.core.User
import org.joda.time.DateTime
import com.elogiclab.guardbee.core.GuardbeeService

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    
    GuardbeeService.UserService[User].getByUsername("username").orElse {
    
	    GuardbeeService.UserService[User].createUser(new User {
	      val username = "username"
	      val fullName = "fullname"
	      val email = "username@example.org"
	      val enabled = true
	      val expirationDate = Some(DateTime.now.plusMonths(2))
	    }, GuardbeeService.Configuration.DefaultProfileRoles)
	    GuardbeeService.UserService[User].updatePassword("username", GuardbeeService.PasswordProvider.hash("password"))
	    None
    }
    
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
    
}
