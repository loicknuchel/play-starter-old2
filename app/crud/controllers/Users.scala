package crud.controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import crud.models.User
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import crud.dao.UserDao
import crud.models.UserJsonFormat._
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import crud.dao.UserDao
import play.api.libs.json.JsObject

object Users extends Controller with MongoController {
  implicit val DB = db

  def create = Action.async(parse.json) { request =>
    val id = BSONObjectID.generate.stringify
    val user = request.body.as[JsObject] ++ Json.obj("id" -> id)
    user.validate[User].map {
      UserDao.create(_)
        .map { lastError => Created(Json.obj("id" -> id, "msg" -> "User Created")) }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findAll = Action.async { UserDao.findAll().map { users => Ok(Json.toJson(users)) } }

  def find(id: String) = Action.async {
    UserDao.find(id).map { mayBeUser =>
      mayBeUser
        .map { user => Ok(Json.toJson(user)) }
        .getOrElse(NotFound(Json.obj("msg" -> s"User with ID $id not found")))
    }
  }

  def update(id: String) = Action.async(parse.json) { request =>
    val user = request.body.as[JsObject]
    user.validate[User].map {
      UserDao.update(id, _)
        .map { _ => Ok(Json.obj("msg" -> s"User Updated")) }
        .recover { case _ => InternalServerError }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def delete(id: String) = Action.async {
    UserDao.delete(id)
      .map { _ => Ok(Json.obj("msg" -> s"User Deleted")) }
      .recover { case _ => InternalServerError }
  }
}