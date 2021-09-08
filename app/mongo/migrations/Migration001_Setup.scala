package mongo.migrations

import com.github.cloudyrock.mongock.{ChangeLog, ChangeSet}
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.ValidationOptions
import com.mongodb.client.model.Filters._
import com.mongodb.client.model.Indexes._
import com.mongodb.client.model._
import model.ModelEvent
import org.bson.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.mongodb.scala.model.{CreateCollectionOptions, IndexOptions}

import java.io.File

@ChangeLog(order = "001")
class Migration001_Setup {

  @ChangeSet(order = "001", id = "20210907-001", author = "Adrian Carp")
  def setupCollection(db: MongoDatabase) = {

    db.createCollection("eventLogs", new CreateCollectionOptions().validationOptions(new ValidationOptions()
      .validator(
        jsonSchema(
          BsonDocument(Seq(
            "bsonType" -> BsonString("object"),
            "required" -> BsonArray.fromIterable(
              Array(
                "type",
                "timestamp",
                "value"
              ).map(BsonString(_))
            ),
            "properties" -> BsonDocument(Seq(
              "type" -> BsonDocument(Seq(
                "bsonType" -> BsonString("string"),
                "description" -> BsonString("event type")
              )),
              "timestamp" -> BsonDocument(Seq(
                "bsonType" -> BsonString("long"),
                "description" -> BsonString("timestamp")
              )),
              "value" -> BsonDocument(Seq(
                "bsonType" -> BsonString("double"),
                "description" -> BsonString("value")
              ))
            ))
          ))
        )
      )
      .validationAction(ValidationAction.ERROR)
    )
      .collation(
        Collation.builder()
          .locale("en")
          .collationStrength(CollationStrength.SECONDARY)
          .caseLevel(false)
          .numericOrdering(true)
          .build()
      )
    )
  }

  @ChangeSet(order = "002", id = "20210907-002", author = "Adrian Carp")
  def addIndex(db: MongoDatabase) = {
    val collection = db.getCollection("eventLogs")

    collection.createIndex(
      compoundIndex(
        ascending("timestamp"),
        ascending("type"),
        ascending("value")
      ),
      IndexOptions().name("timestampTypeValueIndex").unique(false)
    )
  }

//  @ChangeSet(order = "003", id = "20210907-003", author = "Adrian Carp")
//  def populateEventLogsCollection(db: MongoDatabase) = {
//
//    val collection = db.getCollection("eventLogs")
//
//    val source: scala.io.Source = scala.io.Source.fromFile(new File("resources/resident-samples.log"))
//    source.getLines().foreach { line =>
//      line.split(",").toList match {
//        case timestamp :: eventType :: value :: Nil =>
//          val modelEvent = ModelEvent(None, eventType, timestamp.toLong, value.toDouble)
//          val doc = buildDocument(modelEvent)
//          collection.insertOne(doc)
//        case _ =>
//      }
//    }
//
//    def buildDocument(event: ModelEvent): Document = {
//      val res = new Document()
//      res.put("type", event.eventType)
//      res.put("timestamp", event.timestamp)
//      res.put("value", event.value)
//      res
//    }
//  }
}