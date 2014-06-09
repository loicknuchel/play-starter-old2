package controllers

import models.UserProfile
import dao.UserProfileDao
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import play.api.libs.json._
import play.api.data.Form
import play.modules.reactivemongo.MongoController


object UserProfileCrud extends Controller with MongoController {
  implicit val DB = db
  
  val pageTitle = "User profiles admin"
  val userProfileForm: Form[UserProfile] = Form(UserProfile.mapForm)

  def options(profiles: Set[UserProfile]): Seq[(String, String)] = profiles.map(profile => (profile.name, profile.name)).toSeq
    
  def index = Action.async {
    UserProfileDao.findAll().map { profiles =>
      Ok(views.html.admin.userprofile.list(pageTitle, profiles.toList))
    }
  }

  def showCreationForm = Action.async {
	  Future.successful(Ok(views.html.admin.userprofile.edit(pageTitle, None, userProfileForm)))
  }

  def create = Action.async { implicit request =>
    userProfileForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admin.userprofile.edit(pageTitle, None, formWithErrors))),
      profile => UserProfileDao.create(profile).map { lastError => Redirect(routes.UserProfileCrud.index()) })
  }

  def showEditForm(id: String) = Action.async {
    val futureResults = for {
      mayBeProfile <- UserProfileDao.find(id)
    } yield mayBeProfile

    futureResults.map { mayBeProfile =>
      mayBeProfile.map { profile =>
        Ok(views.html.admin.userprofile.edit(pageTitle, Some(id), userProfileForm.fill(profile)))
      }.getOrElse(Redirect(routes.UserProfileCrud.index()))
    }
  }

  def update(id: String) = Action.async { implicit request =>
    userProfileForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admin.userprofile.edit(pageTitle, Some(id), formWithErrors))),
      profile => {
        UserProfileDao.update(id, profile)
          .map { _ => Redirect(routes.UserProfileCrud.index()) }
          .recover { case _ => InternalServerError }
      })
  }

  def delete(id: String) = Action.async {
    println("remove(" + id + ")");
    UserProfileDao.delete(id)
      .map { _ => Redirect(routes.UserProfileCrud.index()) }
      .recover { case _ => InternalServerError }
  }
}