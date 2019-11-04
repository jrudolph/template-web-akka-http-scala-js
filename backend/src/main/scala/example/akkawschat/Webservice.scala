package example.akkawschat

import akka.http.scaladsl.server.Directives

class Webservice extends Directives {
  def route =
    get {
      path("example") { complete(s"Hello ${sys.env("test")}") } ~
        pathSingleSlash {
          getFromResource("web/index.html")
        } ~
        // Scala-JS puts them in the root of the resource directory per default,
        // so that's where we pick them up
        path("frontend-launcher.js")(getFromResource("frontend-launcher.js")) ~
        path("frontend-fastopt.js")(getFromResource("frontend-fastopt.js"))
    } ~ getFromResourceDirectory("web")
}
