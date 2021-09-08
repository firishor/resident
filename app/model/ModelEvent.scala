package model

import org.bson.types.ObjectId
import play.api.libs.json.{JsValue, Reads}

case class ModelEvent(id: Option[ObjectId], eventType: String, timestamp: Long, value: Double)

object ModelEvent {
  implicit val jsonFormat: Reads[ModelEvent] = (json: JsValue) => {
    val validateEventType = (json \ "type").validate[String]
    val validateTimestamp = (json \ "timestamp").validate[Long]
    val validateValue = (json \ "value").validate[Double]
    for {
      eventType <- validateEventType
      timestamp <- validateTimestamp
      value <- validateValue
    } yield ModelEvent(None, eventType, timestamp, value)
  }
}
