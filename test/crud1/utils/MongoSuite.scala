package crud1.utils

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, FunSuite}
import com.github.simplyscala.MongoEmbedDatabase
import scala.concurrent.Await
import crud1.models.User
import crud1.models.UserJsonFormat.userFormat
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

trait MongoSuite extends FunSuite with Matchers with BeforeAndAfterAll with BeforeAndAfter
with MongoEmbedDatabase with DatabaseUtils

trait DatabaseUtils {
  import crud1.models.UserJsonFormat._
  import crud1.models.User
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._
  import play.modules.reactivemongo.json.collection.JSONCollection

  def drop(collections: JSONCollection*) = collections.foreach { coll => Await.ready(coll.drop(), 2 seconds) }

  def initData(users: User*)(implicit users_coll: JSONCollection) =
    users.foreach { user => Await.ready(users_coll.save(user), 2 seconds) }
}