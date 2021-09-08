package dto

import play.api.libs.json.{Format, Json}

case class DtoAverage(eventType: String, value: Double, processedCount: Int)

object DtoAverage {
  implicit val jsonFormat: Format[DtoAverage] = Json.format[DtoAverage]
}