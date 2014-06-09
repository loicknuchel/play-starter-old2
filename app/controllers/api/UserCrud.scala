package controllers.api

import models.BasicUser
import models.BasicUserJsonFormat._
import dao.BasicUserDao
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.JsObject
import play.modules.reactivemongo.MongoController
import reactivemongo.bson.BSONObjectID

object UserCrud extends Controller with MongoController {
  implicit val DB = db

  def create = Action.async(parse.json) { request =>
    val id = BSONObjectID.generate.stringify
    val user = request.body.as[JsObject] ++ Json.obj("id" -> id)
    user.validate[BasicUser].map {
      BasicUserDao.create(_)
        .map { lastError => Created(Json.obj("id" -> id, "msg" -> "User Created")) }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def findAll = Action.async { BasicUserDao.findAll().map { users => Ok(Json.toJson(users)) } }

  def find(id: String) = Action.async {
    BasicUserDao.find(id).map { mayBeUser =>
      mayBeUser
        .map { user => Ok(Json.toJson(user)) }
        .getOrElse(NotFound(Json.obj("msg" -> s"User with ID $id not found")))
    }
  }

  def update(id: String) = Action.async(parse.json) { request =>
    val user = request.body.as[JsObject]
    user.validate[BasicUser].map {
      BasicUserDao.update(id, _)
        .map { _ => Ok(Json.obj("msg" -> s"User Updated")) }
        .recover { case _ => InternalServerError }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def delete(id: String) = Action.async {
    BasicUserDao.delete(id)
      .map { _ => Ok(Json.obj("msg" -> s"User Deleted")) }
      .recover { case _ => InternalServerError }
  }
}