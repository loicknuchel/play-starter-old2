package controllers

import models.User
import models.UserForm
import models.UserProfile
import dao.UserDao
import dao.UserProfileDao
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc._
import play.api.libs.json._
import play.api.data.Form
import play.modules.reactivemongo.MongoController

object UserCrud extends Controller with MongoController {
  implicit val DB = db

  val pageTitle = "Users admin"
  val userForm: Form[UserForm] = Form(UserForm.mapForm)

  def options(users: Set[UserProfile]): Seq[(String, String)] = users.map(user => (user.id, user.name)).toSeq

  def index = Action.async {
    UserDao.findAll().map { users =>
      Ok(views.html.admin.user.list(pageTitle, users.toList))
    }
  }

  def showCreationForm = Action.async {
    UserProfileDao.findAll().map { profiles =>
      Ok(views.html.admin.user.edit(pageTitle, None, userForm, options(profiles)))
    }
  }

  def create = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => UserProfileDao.findAll().map { profiles =>
        BadRequest(views.html.admin.user.edit(pageTitle, None, formWithErrors, options(profiles)))
      },
      user => {
        UserProfileDao.find(user.profileId) flatMap {
          case None => UserProfileDao.findAll().map { profiles =>
            BadRequest(views.html.admin.user.edit(pageTitle, None, userForm.fill(user).withGlobalError("Unable to find profile "+user.profileId), options(profiles)))
          }
          case Some(profile) => UserDao.create(user.withProfile(profile)).map { lastError => Redirect(routes.UserCrud.index()) }
        }
      })
  }

  def showEditForm(id: String) = Action.async {
    val futureResults = for {
      mayBeUser <- UserDao.find(id)
      profiles <- UserProfileDao.findAll()
    } yield (mayBeUser, profiles)

    futureResults.map {
      case (mayBeUser, profiles) =>
        mayBeUser.map { user =>
          Ok(views.html.admin.user.edit(pageTitle, Some(id), userForm.fill(user.toForm), options(profiles)))
        }.getOrElse(Redirect(routes.UserCrud.index()))
    }
  }

  def update(id: String) = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => UserProfileDao.findAll().map { profiles =>
        BadRequest(views.html.admin.user.edit(pageTitle, None, formWithErrors, options(profiles)))
      },
      user => {
        UserProfileDao.find(user.profileId) flatMap {
          case None => UserProfileDao.findAll().map { profiles =>
            BadRequest(views.html.admin.user.edit(pageTitle, None, userForm.fill(user).withGlobalError("Unable to find profile "+user.profileId), options(profiles)))
          }
          case Some(profile) => UserDao.update(id, user.withProfile(profile))
            .map { _ => Redirect(routes.UserCrud.index()) }
            .recover { case _ => InternalServerError }
        }
      })
  }

  def delete(id: String) = Action.async {
    println("remove(" + id + ")");
    UserDao.delete(id)
      .map { _ => Redirect(routes.UserCrud.index()) }
      .recover { case _ => InternalServerError }
  }
}