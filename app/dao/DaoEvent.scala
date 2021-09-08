package dao

import model.ModelEvent
import org.mongodb.scala.{Completed, MongoDatabase}
import org.mongodb.scala.bson.collection.immutable.Document

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DaoEvent @Inject()(implicit mongoDatabase: MongoDatabase, ec: ExecutionContext) {

  def buildDocument(event: ModelEvent): Document = {
    Document("type" -> event.eventType, "timestamp" -> event.timestamp, "value" -> event.value)
  }

  def insertMany(event: Seq[ModelEvent]): Future[Completed] = {
    mongoDatabase
      .getCollection("eventLogs")
      .insertMany(event.map(buildDocument))
      .toFuture()
  }

}
