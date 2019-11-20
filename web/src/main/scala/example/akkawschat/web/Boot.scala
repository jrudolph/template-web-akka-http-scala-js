package example.akkawschat.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import scala.concurrent.{ Await, Promise }
import scala.util.{ Failure, Success }
import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem()
  import system.dispatcher

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")

  val shutdownSignal = Promise[Unit]()
  val service = new Webservice(shutdownSignal.future)

  val binding = Http().bindAndHandle(service.route, interface, port)
  binding.onComplete {
    case Success(binding) =>
      val localAddress = binding.localAddress
      println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")

      Runtime.getRuntime.addShutdownHook(new Thread {
        override def run(): Unit = {
          shutdownSignal.trySuccess(())
          Await.ready(binding.terminate(10.seconds), 15.seconds)
        }
      })
    case Failure(e) =>
      println(s"Binding failed with ${e.getMessage}")
      system.terminate()
  }
}
