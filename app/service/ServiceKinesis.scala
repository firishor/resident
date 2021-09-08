package service

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.kinesis.scaladsl.KinesisSource
import akka.stream.alpakka.kinesis.{ShardIterator, ShardSettings}
import akka.stream.scaladsl.{Sink, Source}
import config.ConfigKinesis
import model.{Accumulator, ModelEvent}
import play.api.libs.json.Json
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.Record

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

@Singleton
class ServiceKinesis @Inject()()(implicit
                                 val actorSystem: ActorSystem,
                                 materializer: Materializer,
                                 ec: ExecutionContext,
                                 amazonKinesisAsync: KinesisAsyncClient,
                                 configKinesis: ConfigKinesis) {

  private val settings =
    ShardSettings(streamName = configKinesis.streamName, "shardId-000000000000")
      .withRefreshInterval(configKinesis.refreshInterval.second)
      .withLimit(configKinesis.limit)
      .withShardIterator(ShardIterator.TrimHorizon)

  def average(eventType: String, from: Long, to: Long): Future[Accumulator] = {
    val source: Source[Record, NotUsed] = KinesisSource.basic(settings, amazonKinesisAsync)
    val sink: Sink[Accumulator, Future[Accumulator]] = Sink.head

    source
      .grouped(configKinesis.insertBatchNumber)
      .fold(Accumulator(0, 0)) { (accumulator, records) =>
          val events = records.flatMap(recordToEvent)
          val filteredEvents = events.filter { modelEvent =>
              modelEvent.eventType == eventType &&
                modelEvent.timestamp >= from &&
                modelEvent.timestamp <= to
            }

        accumulator.copy(
          value = accumulator.value + filteredEvents.map(_.value).sum,
          processedCount = accumulator.processedCount + filteredEvents.length)
      }
      .runWith(sink)
  }

  private def recordToEvent(record: Record): Option[ModelEvent] = {
    val bytes = record.data().asByteArray()
    val str = new String(bytes)
    Json.parse(str).validate[ModelEvent].asOpt
  }

}
