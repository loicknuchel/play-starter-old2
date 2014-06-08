package controllers

import play.api._
import play.api.mvc._

/*
 * Single Page Application :
 * Only serve the index.html with angular app
 */
object ApplicationSpa extends Controller {

  def index = Action {
    Ok(views.html.indexSpa())
  }

}