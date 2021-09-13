import io.github.edadma.libyaml._

import scala.annotation.tailrec

object Main extends App {

  val parser = new Parser
  val event  = new Event

  parser.setInputString("a: b")

  @tailrec
  def eventLoop(): Unit = {
    parser.parse(event)
    println(s"${event.getType.value}")

    if (event.getType == EventType.STREAM_END)
      event.destroy()
    else {
      event.delete()
      eventLoop()
    }
  }

  eventLoop()
  parser.destroy()

}
