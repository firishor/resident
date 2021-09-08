package service

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import dao.DaoEvent
import model.{Accumulator, ModelEvent}
import play.api.Logger

import java.io.File
import java.nio.file.Paths
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Service @Inject()()(implicit materializer: Materializer) {
  def average(eventType: String, from: Long, to: Long): Future[Accumulator] = {
    val source = FileIO.fromPath(Paths.get("resources/resident-samples.log"))
    val sink: Sink[Accumulator, Future[Accumulator]] = Sink.head

    source
      .via(bytesStringToString)
      .via(stringToModelEvents)
      .fold(Accumulator(0, 0)) {
        (accumulator, modelEvents) =>
          val filteredData = modelEvents.filter { modelEvent =>
            modelEvent.eventType == eventType &&
              modelEvent.timestamp >= from &&
              modelEvent.timestamp <= to
          }

          accumulator.copy(
            value = accumulator.value + filteredData.map(_.value).sum,
            processedCount = accumulator.processedCount + filteredData.length)
      }
      .runWith(sink)
  }

  private def bytesStringToString: Flow[ByteString, String, NotUsed] = {
    Framing
      .delimiter(ByteString(System.lineSeparator()), maximumFrameLength = 512, allowTruncation = true)
      .map(_.utf8String)
  }

  private def stringToModelEvents: Flow[String, List[ModelEvent], NotUsed] = {
    Flow[String]
      .map { content =>
        content.split("\n").toList
          .flatMap { line =>
            line.split(",").toList match {
              case timestamp :: eventType :: value :: Nil =>
                Some(ModelEvent(None, eventType , timestamp.toLong , value.toDouble))
              case _ =>
                None
            }
          }
      }
  }

}
