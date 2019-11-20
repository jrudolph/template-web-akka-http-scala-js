package example.akkawschat

import org.scalajs.dom

object AutoReloader {
  def apply(path: String, reconnectionIntervalMillis: Long): Unit = {
    val uri = relativeWsUri(path)
    def watchdog(): Unit = {
      val socket = new dom.WebSocket(uri)
      socket.onopen = { (e: dom.Event) =>
        println(s"AutoReloader connected to [$uri], reconnectionIntervalMillis: $reconnectionIntervalMillis ms")

        socket.onclose = { (e: dom.Event) =>
          println(s"Socket was closed, reloading in $reconnectionIntervalMillis ms")
          dom.window.setTimeout(tryReconnection _, reconnectionIntervalMillis)
        }
      }
      socket.onerror = { (e: dom.Event) =>
        println(s"AutoReloader couldn't connect to watchdog, now disabled.")
      }

    }

    def tryReconnection(): Unit = {
      val socket = new dom.WebSocket(relativeWsUri(path))
      socket.onopen = { (e: dom.raw.Event) =>
        dom.window.location.reload(true)
        socket.close()
      }
      socket.onerror = { (e: dom.raw.Event) =>
        println(s"Got error $e, retrying in $reconnectionIntervalMillis ms")
        dom.window.setTimeout(tryReconnection _, reconnectionIntervalMillis)
      }
    }
    watchdog()
  }

  private def relativeWsUri(path: String): String = {
    val loc = dom.window.location
    val protocol = if (loc.protocol == "https:") "wss" else "ws"
    s"$protocol://${loc.host}$path"
  }
}
