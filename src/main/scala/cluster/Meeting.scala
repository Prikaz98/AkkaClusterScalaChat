package cluster

import akka.actor.Actor
import javafx.application.Platform
import model.ChatModel

import java.time.LocalDateTime

class Meeting(model: ChatModel) extends Actor {

  private def getLocalTime: String = {
    val hour = LocalDateTime.now().getHour
    val minute = LocalDateTime.now().getMinute
    val localDateTime = s"[$hour:$minute]"
    localDateTime
  }

  override def receive: Receive = {
    case CommonChatMsg(from, message) =>
      val localTime = getLocalTime
      Platform.runLater(() =>
        model.newMessage.set(s"$localTime $from : $message \n")
      )

    case PrivateChatMsg(from, to, message) =>
      val localTime = getLocalTime
      Platform.runLater(() =>
        model.newMessage.set(s"$localTime[Privat msg] $from : $message \n")
      )

    case SelfPrivateChatMsg(from, _, message) =>
      val localTime = getLocalTime
      Platform.runLater(() =>
        model.newMessage.set(s"$localTime[Privat msg] $from : $message \n")
      )
  }
}
