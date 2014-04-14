package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.duration._
import scala.concurrent.Future
import play.api.libs.concurrent._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.DefaultBSONHandlers._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
  
object Users2 extends Controller {
  val collection = ReactiveMongoPlugin.db.collection[JSONCollection]("users")

  val objectIdFormat = OFormat[String](
    (__ \ "$oid").read[String],
    OWrites[String]{ s => Json.obj( "$oid" -> s ) }
  )

  /** Full Person validator */
  val validatePerson: Reads[JsObject] = (
    (__ \ 'name).json.pickBranch and
    (__ \ 'bio).json.pickBranch
  ).reduce

  /** Person validator for restricted update */
  val emptyObj = __.json.put(Json.obj())
  val validatePerson4RestrictedUpdate: Reads[JsObject] = (
    ((__ \ 'name).json.pickBranch or emptyObj) and
    ((__ \ 'bio).json.pickBranch or emptyObj)
  ).reduce

  /** Writes an ID in Json Extended Notation */
  val toObjectId = OWrites[String]{ s => Json.obj("_id" -> Json.obj("$oid" -> s)) }
  val fromObjectId = (__ \ '_id).json.copyFrom( (__ \ '_id \ '$oid).json.pick )

  /** Generates a new ID and adds it to your JSON using Json extended notation for BSON */
  val generateId = (__ \ '_id \ '$oid).json.put( JsString(BSONObjectID.generate.stringify) )

  /** Updates Json by adding both ID and date */
  val addMongoId: Reads[JsObject] = __.json.update( generateId )

  /** Converts JSON into Mongo update selector by just copying whole object in $set field */
  val toMongoUpdate = (__ \ '$set).json.copyFrom( __.json.pick )

  val outputPerson = 
    (__ \ '_id).json.prune

  /** no need to always use Json combinators or transformers, sometimes stay simple */
  def resOK(data: JsValue) = Json.obj("res" -> "OK") ++ Json.obj("data" -> data)
  def resKO(error: JsValue) = Json.obj("res" -> "KO") ++ Json.obj("error" -> error)
  
  def all = Action.async {
    val cursor = collection.find(BSONDocument(), BSONDocument()).cursor[JsValue]
    val futureList = cursor.collect[List]()
    futureList.map { results => Ok(Json.toJson(results)) }
  }
  
  def create = Action(parse.json){ request =>
    request.body.transform(validatePerson andThen addMongoId).map{ jsobj => 
      Async{
        collection.insert(jsobj).map{ p => 
          Ok( resOK(jsobj.transform(fromObjectId).get) )
        }.recover{ case e => 
          InternalServerError( resKO(JsString("exception %s".format(e.getMessage))) )
        }
      }
    }.recoverTotal{ err => 
      BadRequest( resKO(JsError.toFlatJson(err)) )
    }
  }

  def show(id: String) = Action{ 
    Async {
      collection.find(BSONDocument("_id" -> id)).cursor[JsValue].headOption.map{ 
        case None => NotFound(Json.obj("res" -> "KO", "error" -> s"person with ID $id not found"))
        case Some(p) => 
          p.transform(outputPerson).map{ jsonp =>
            Ok( resOK(Json.obj("person" -> jsonp)) )    
          }.recoverTotal{ e =>
            BadRequest( resKO(JsError.toFlatJson(e)) )    
          }
      }
    }
  }

  def updateFull(id: String) = Action(parse.json){ request =>
    request.body.transform(validatePerson).flatMap{ jsobj =>
      jsobj.transform(toMongoUpdate).map{ updateSelector =>
        Async{
          collection.update(
            toObjectId.writes(id),
            updateSelector
          ).map{ lastError => 
            if(lastError.ok)
              Ok( resOK(Json.obj("msg" -> s"person $id updated")) )
            else     
              InternalServerError( resKO(JsString("error %s".format(lastError.stringify))) )
          }
        }
      }
    }.recoverTotal{ e =>
      BadRequest( resKO(JsError.toFlatJson(e)) )
    }
  }

  def update(id: String) = Action(parse.json){ request =>
    request.body.transform(validatePerson4RestrictedUpdate).flatMap{ jsobj =>
      jsobj.transform(toMongoUpdate).map{ updateSelector =>
        Async{
          collection.update(
            toObjectId.writes(id),
            updateSelector
          ).map{ lastError => 
            if(lastError.ok)
              Ok( resOK(Json.obj("msg" -> s"person $id updated")) )
            else     
              InternalServerError( resKO(JsString("error %s".format(lastError.stringify))) )
          }
        }
      }
    }.recoverTotal{ e =>
      BadRequest( resKO(JsError.toFlatJson(e)) )
    }
  }

  def delete(id: String) = Action{ 
    Async {
      collection.remove[JsValue](toObjectId.writes(id)).map{ lastError =>
        if(lastError.ok)
          Ok( resOK(Json.obj("msg" -> s"person $id deleted")) )
        else     
          InternalServerError( resKO(JsString("error %s".format(lastError.stringify))) )
      }
    }
  }
}