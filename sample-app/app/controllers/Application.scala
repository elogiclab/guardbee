package controllers

import play.api.mvc.{Action, Controller}
import com.elogiclab.guardbee.core.SecuredController
import com.elogiclab.guardbee.core.authz._

object Application extends SecuredController {
  def index = Action { implicit request =>
    Ok(views.html.index("Hello Play Framework"))
  }
  
  def secured = Authorized(IsAuthenticated) { auth => implicit request =>
    Ok(views.html.secured(auth)(request))
  }
  
}