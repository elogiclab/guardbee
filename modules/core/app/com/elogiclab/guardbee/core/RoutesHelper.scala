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

import play.api.Play
import play.api.mvc.Call

/**
 * @author Marco Sarti
 *
 */
object RoutesHelper {

  lazy val loginLogoutControllerClazz = Play.current.classloader.loadClass("com.elogiclab.guardbee.core.ReverseLoginLogoutController")

  lazy val loginLogoutControllerMethods = loginLogoutControllerClazz.newInstance().asInstanceOf[{
    def loginPage(dest:String): Call
    def logout(): Call
    def login(providerId: String, dest:Option[String]): Call
    def loginWith(providerId: String, dest: String): Call
    def loginCallback(provider:String, code:String)
  }]

  def loginPage(dest:String) = loginLogoutControllerMethods.loginPage(dest)
  def logout() = loginLogoutControllerMethods.logout()
  def login(providerId: String, dest:Option[String]) = loginLogoutControllerMethods.login(providerId, dest)
  def loginWith(providerId: String, dest: String) = loginLogoutControllerMethods.loginWith(providerId, dest)
  def loginCallback(provider:String, code:String) = loginLogoutControllerMethods.loginCallback(provider, code)
  

  lazy val assetClazz = Play.current.classloader.loadClass("controllers.ReverseAssets")
  lazy val assetMethods = assetClazz.newInstance().asInstanceOf[{
    def at(file: String): Call
  }]
  def assetAt(file: String) = assetMethods.at(file)

  lazy val webJarAssetClazz = Play.current.classloader.loadClass("controllers.ReverseWebJarAssets")
  lazy val webJarAssetMethods = webJarAssetClazz.newInstance().asInstanceOf[{
    def at(file: String): Call
  }]
  def webJarAt(file: String) = webJarAssetMethods.at(file)

}