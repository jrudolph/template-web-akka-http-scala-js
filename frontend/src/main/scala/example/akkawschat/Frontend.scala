package example.akkawschat

object Frontend {
  def main(args: Array[String]): Unit = {
    AutoReloader("/ws-watchdog", 200)
  }
}