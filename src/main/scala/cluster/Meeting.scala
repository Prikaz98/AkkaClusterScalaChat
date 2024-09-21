package cluster

import akka.actor.Actor
import javafx.application.Platform
import model.ChatModel

import java.time.LocalDateTime

class Meeting(model: ChatModel) extends Actor {

  private def now: String = {
    val now = LocalDateTime.now()
    val hour = now.getHour
    val minute = now.getMinute
    s"[$hour:$minute]"
  }

  override def receive: Receive = {
    case CommonChatMsg(from, message) =>
      Platform.runLater(() =>
        model.newMessage.set(s"$now $from : $message \n")
      )

    case PrivateChatMsg(from, to, message) =>
      val localTime = now
      Platform.runLater(() =>
        model.newMessage.set(s"$now[Privat msg] $from : $message \n")
      )

    case SelfPrivateChatMsg(from, _, message) =>
      val localTime = now
      Platform.runLater(() =>
        model.newMessage.set(s"$now[Privat msg] $from : $message \n")
      )
  }
}
