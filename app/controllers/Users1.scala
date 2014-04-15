package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.User
import models.UserNoId
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import dao.UserDao
import models.UserJsonFormat._
import models.UserNoIdJsonFormat._
import reactivemongo.bson.BSONObjectID
import models.UserNoId

object Users1 extends Controller with MongoController {
  implicit val DB = db

  def all = Action.async { UserDao.all().map { users => Ok(Json.toJson(users)) } }

  def create = Action.async(parse.json) { request =>
    val id = BSONObjectID.generate.stringify
    request.body.validate[UserNoId]
      .map { userNoId => User(id, userNoId.name, userNoId.bio) }
      .map { UserDao.insert(_).map { lastError => Created(Json.obj("id" -> id, "msg" -> "User Created")) } }
      .getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def show(id: String) = Action.async {
    UserDao.find(id).map { mayBeUser =>
      mayBeUser
        .map { user => Ok(Json.toJson(user)) }
        .getOrElse(NotFound(Json.obj("msg" -> s"User with ID $id not found")))
    }
  }

  def delete(id: String) = Action.async {
    UserDao.delete(id)
      .map { _ => Ok(Json.obj("msg" -> s"User Deleted")) }
      .recover { case _ => InternalServerError }
  }

  def update(id: String) = TODO
}