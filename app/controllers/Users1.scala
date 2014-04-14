package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.User
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import dao.UserDao
import models.UserJsonFormat._

object Users1 extends Controller with MongoController {
  implicit val DB = db

  def all = Action.async { UserDao.all().map { users => Ok(Json.toJson(users)) } }

  def create = Action.async(parse.json) { request =>
    request.body.validate[User]
      .map { UserDao.insert(_).map { lastError => Created("User Created") } }
      .getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def show(id: String) = Action.async {
    UserDao.find(id).map { mayBeUser =>
      mayBeUser
        .map { user => Ok(Json.toJson(user)) }
        .getOrElse(NotFound(s"user with id $id not found"))
    }
  }

  def update(id: String) = TODO

  def delete(id: String) = Action.async {
    UserDao.delete(id)
      .map { _ => Ok(s"User Deleted") }
      .recover { case _ => InternalServerError }
  }
}