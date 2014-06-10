package dao

import models.User
import models.UserFormat._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.JsObject
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import reactivemongo.core.commands._
import reactivemongo.bson._

object UserDao {
  private val COLLECTION_NAME = "users"
  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](COLLECTION_NAME)
  private def generateId(user: User): JsObject = Json.toJson(user).as[JsObject].deepMerge(Json.obj("id" -> BSONObjectID.generate.stringify))
  private def toUpdateFormat(user: User): JsObject =
    Json.obj("$set" -> user).transform(
      (__ \ '$set \ 'id).json.prune andThen
        (__ \ '$set \ 'created).json.prune).get

  def find(id: String)(implicit db: DB): Future[Option[User]] = collection().find(Json.obj("id" -> id)).one[User]
  def find(filter: JsObject)(implicit db: DB): Future[Set[User]] = collection().find(filter).cursor[User].collect[Set]()
  def findAll()(implicit db: DB): Future[Set[User]] = collection().find(Json.obj()).cursor[User].collect[Set]()

  def insert(user: User)(implicit db: DB): Future[LastError] = collection().insert(user)
  def create(user: User)(implicit db: DB): Future[LastError] = collection().insert(generateId(user))
  def update(id: String, user: User)(implicit db: DB): Future[LastError] = collection().update(Json.obj("id" -> id), toUpdateFormat(user))
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))
  def insertAll(categories: List[User])(implicit db: DB): Future[List[LastError]] = Future.sequence(categories.map(user => collection().insert(user)))
  def deleteAll()(implicit db: DB): Future[LastError] = collection().remove(Json.obj())
}