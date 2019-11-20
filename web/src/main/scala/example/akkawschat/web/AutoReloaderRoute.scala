package example.akkawschat.web

import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import akka.stream.scaladsl.{ Flow, Sink, Source }

import scala.concurrent.Future

object AutoReloaderRoute {
  def apply(shutdownSignal: Future[Unit]): Route = {
    val txtSource =
      Source.single("Waiting for termination...") ++
        Source.future(shutdownSignal)
        .map(_ => "Now terminating")

    val flow =
      Flow.fromSinkAndSourceCoupled[Message, Message](Sink.ignore, txtSource.map(TextMessage(_)))
    handleWebSocketMessages(flow)
  }
}
