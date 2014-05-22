package chatsse.dao

import scala.concurrent.Future
import chatsse.models.Message
import reactivemongo.api.DB
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import chatsse.models.MessageJsonFormat._
import reactivemongo.core.commands.LastError
import play.api.libs.json.Json.toJsFieldJsValueWrapper

object MessageDao {
  private val MESSAGE_COLECTION_NAME = "messages"

  def all()(implicit db: DB): Future[Set[Message]] = collection().find(Json.obj()).cursor[Message].collect[Set]()

  def insert(message: Message)(implicit db: DB): Future[LastError] = collection().insert(message)

  def find(id: String)(implicit db: DB): Future[Option[Message]] = collection().find(Json.obj("id" -> id)).one[Message]

  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))

  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](MESSAGE_COLECTION_NAME)
}