package crud.dao

import models.UserFormat._
import dao.UserDao
import crud.utils.MongoSuite
import com.github.simplyscala.MongodProps
import reactivemongo.api.{ MongoDriver, DB }
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Await
import scala.concurrent.duration._
import models.User
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import models.UserProfile


class UserDaoTest extends MongoSuite {
  var mongoProps: MongodProps = null
  override def beforeAll() { mongoProps = mongoStart(27018) }
  override def afterAll() { mongoStop(mongoProps) }

  before { drop(usersCollForTest) }

  implicit val connection: DB = {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost:27018"))
    connection("test")
  }

  implicit val usersCollForTest = connection[JSONCollection]("users")

  test("should find all user") {
    // Given
    val expectedUser = User("id", "name", UserProfile("id", "profile"), "mail", "password")

    initData(expectedUser)

    // When
    val result = Await.result(UserDao.findAll(), 2 seconds)

    // Then
    result should have size 1
    result shouldBe Set(expectedUser)
  }

  test("should insert user") {
    // Given
    val expectedUser = User("id", "name", UserProfile("id", "profile"), "mail", "password")

    // When
    Await.ready(UserDao.create(expectedUser), 2 seconds)

    // Then
    val result = Await.result(usersCollForTest.find(Json.obj()).cursor[User].collect[Set](), 2 seconds)
    result shouldBe Set(expectedUser)
  }

  test("should find User by its id") {
    // Given
    val expectedUser = User("expected_id", "expectedUSer", UserProfile("id", "profile"), "mail", "password")
    val unexpectedUser = User("unexpected_id", "unexpectedUSer", UserProfile("id", "profile"), "mail", "password")

    initData(expectedUser, unexpectedUser)

    // When
    val result = Await.result(UserDao.find(expectedUser.id), 2 seconds)

    // Then
    result shouldBe Option(expectedUser)
  }

  test("should delete User from its id") {
    // Given
    val expectedUser = User("expected_id", "expectedUSer", UserProfile("id", "profile"), "mail", "password")
    val unexpectedUser = User("unexpected_id", "unexpectedUSer", UserProfile("id", "profile"), "mail", "password")

    initData(expectedUser, unexpectedUser)

    // When
    Await.ready(UserDao.delete(unexpectedUser.id), 2 seconds)

    // Then
    val result = Await.result(usersCollForTest.find(Json.obj()).cursor[User].collect[Set](), 2 seconds)
    result shouldBe Set(expectedUser)
  }
}