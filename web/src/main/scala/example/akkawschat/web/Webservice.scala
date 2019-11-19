package example.akkawschat.web

import java.util.Random

import akka.http.scaladsl.marshalling.{ Marshaller, ToEntityMarshaller }
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.server.Directives
import play.twirl.api.Html

class Webservice extends Directives {
  implicit val twirlHtmlMarshaller: ToEntityMarshaller[Html] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`text/html`)(_.toString)

  val random = new Random()

  def route =
    concat(
      get {
        concat(
          pathSingleSlash {
            complete(html.page(Html("Homepage")))
          },
          // Scala-JS puts them in the root of the resource directory per default,
          // so that's where we pick them up
          path("frontend-launcher.js")(getFromResource("frontend-launcher.js")),
          path("frontend-fastopt.js")(getFromResource("frontend-fastopt.js")))
      },
      getFromResourceDirectory("web"))
}
