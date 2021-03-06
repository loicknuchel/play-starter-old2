package dao

import models.Message
import models.MessageJsonFormat._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.JsObject
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import reactivemongo.core.commands.LastError

object MessageDao {
  private val MESSAGE_COLECTION_NAME = "messages"

  def all()(implicit db: DB): Future[Set[Message]] = collection().find(Json.obj()).cursor[Message].collect[Set]()
  def find(filter: JsObject)(implicit db: DB): Future[Set[Message]] = collection().find(filter).cursor[Message].collect[Set]()
  def findOne(id: String)(implicit db: DB): Future[Option[Message]] = collection().find(Json.obj("id" -> id)).one[Message]

  def insert(message: Message)(implicit db: DB): Future[LastError] = collection().insert(message)
  
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))

  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](MESSAGE_COLECTION_NAME)
}