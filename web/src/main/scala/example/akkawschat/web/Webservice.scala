package example.akkawschat.web

import java.util.Random

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.{ Directives, Route }
import play.twirl.api.Html

import scala.concurrent.Future

class Webservice(shutdownSignal: Future[Unit]) extends Directives {
  implicit val twirlHtmlMarshaller: ToEntityMarshaller[Html] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/html`)(_.toString)

  val random = new Random()

  lazy val route: Route =
    concat(
      get {
        concat(
          pathSingleSlash {
            complete(html.page(Html("Homepage")))
          },
          path("ws-watchdog") { AutoReloaderRoute(shutdownSignal) },
          // Scala-JS puts them in the root of the resource directory per default,
          // so that's where we pick them up
          path("frontend-fastopt.js")(getFromResource("frontend-fastopt.js")),
          path("frontend-fastopt.js.map")(getFromResource("frontend-fastopt.js.map")),
        )
      },
      getFromResourceDirectory("web"))
}
