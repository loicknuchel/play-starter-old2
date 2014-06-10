package dao

import models.UserProfile
import models.UserProfileFormat._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.JsObject
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.DB
import reactivemongo.core.commands._
import reactivemongo.bson._

object UserProfileDao {
  private val COLLECTION_NAME = "user-profiles"
  private def collection()(implicit db: DB): JSONCollection = db.collection[JSONCollection](COLLECTION_NAME)
  private def generateId(profile: UserProfile): JsObject = Json.toJson(profile).as[JsObject].deepMerge(Json.obj("id" -> BSONObjectID.generate.stringify))
  private def toUpdateFormat(profile: UserProfile): JsObject =
    Json.obj("$set" -> profile).transform(
      (__ \ '$set \ 'id).json.prune andThen
        (__ \ '$set \ 'created).json.prune).get

  def find(id: String)(implicit db: DB): Future[Option[UserProfile]] = collection().find(Json.obj("id" -> id)).one[UserProfile]
  def find(filter: JsObject)(implicit db: DB): Future[Set[UserProfile]] = collection().find(filter).cursor[UserProfile].collect[Set]()
  def findAll()(implicit db: DB): Future[Set[UserProfile]] = collection().find(Json.obj()).cursor[UserProfile].collect[Set]()

  def insert(profile: UserProfile)(implicit db: DB): Future[LastError] = collection().insert(profile)
  def create(profile: UserProfile)(implicit db: DB): Future[LastError] = collection().insert(generateId(profile))
  def update(id: String, profile: UserProfile)(implicit db: DB): Future[LastError] = collection().update(Json.obj("id" -> id), toUpdateFormat(profile))
  def delete(id: String)(implicit db: DB): Future[LastError] = collection().remove(Json.obj("id" -> id))
  def insertAll(categories: List[UserProfile])(implicit db: DB): Future[List[LastError]] = Future.sequence(categories.map(profile => collection().insert(profile)))
  def deleteAll()(implicit db: DB): Future[LastError] = collection().remove(Json.obj())
}