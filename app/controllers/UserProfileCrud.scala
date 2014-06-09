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
  
  val pageTitle = "Aliment categories admin"
  val categoryForm: Form[UserProfile] = Form(UserProfile.mapForm)

  def options(categories: Set[UserProfile]): Seq[(String, String)] = categories.map(category => (category.name, category.name)).toSeq
    
  def index = Action.async {
    UserProfileDao.findAll().map { categories =>
      Ok(views.html.admin.userprofile.list(pageTitle, categories.toList))
    }
  }

  def showCreationForm = Action.async {
	  Future.successful(Ok(views.html.admin.userprofile.edit(pageTitle, None, categoryForm)))
  }

  def create = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admin.userprofile.edit(pageTitle, None, formWithErrors))),
      category => UserProfileDao.create(category).map { lastError => Redirect(routes.UserProfileCrud.index()) })
  }

  def showEditForm(id: String) = Action.async {
    val futureResults = for {
      mayBeCategory <- UserProfileDao.find(id)
    } yield mayBeCategory

    futureResults.map { mayBeCategory =>
      mayBeCategory.map { category =>
        Ok(views.html.admin.userprofile.edit(pageTitle, Some(id), categoryForm.fill(category)))
      }.getOrElse(Redirect(routes.UserProfileCrud.index()))
    }
  }

  def update(id: String) = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.admin.userprofile.edit(pageTitle, Some(id), formWithErrors))),
      category => {
        UserProfileDao.update(id, category)
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