package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.User
import models.User.UserBSONReader
import models.User.userFormat
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentIdentity
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONObjectIDIdentity
import reactivemongo.bson.Producer.nameValue2Producer

object Users1 extends Controller with MongoController {
  def collection = db.collection[BSONCollection]("users")

  def all = Action.async {
    val cursor = collection.find(BSONDocument(), BSONDocument()).cursor[User] // get all the fields of all the users
    val futureList = cursor.collect[List]()
    futureList.map { results => Ok(Json.toJson(results)) } // convert it to a JSON and return it
  }

  def create = Action.async(parse.json) { request =>
    request.body.validate[User].map {
      user =>
        collection.insert(user).map {
          lastError => Created(s"User Created")
        }
    }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def show(id: String) = Action.async {
    val futureUser = collection.find(BSONDocument("_id" -> new BSONObjectID(id))).one[User]
    futureUser.map { user => Ok(Json.toJson(user)) }
  }
  def update(id: String) = TODO
  def delete(id: String) = Action.async {
    collection.remove(BSONDocument("_id" -> new BSONObjectID(id))).map(_ => Ok(s"User Deleted")).recover { case _ => InternalServerError }
  }
}