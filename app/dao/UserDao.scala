package dao

import scala.concurrent.Future
import models.User
import reactivemongo.api.DB
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.UserJsonFormat._

object UserDao {
  private val USER_COLECTION_NAME = "users"

  def all()(implicit db: DB): Future[Set[User]] =
      db.collection[JSONCollection](USER_COLECTION_NAME).find(Json.obj()).cursor[User].collect[Set]()
}