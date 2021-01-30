import java.time.LocalDateTime

import akka.actor.Actor
import javafx.application.Platform

class Meeting(userName: String) extends Actor {
  def getLocalTime:String ={
    val hour = LocalDateTime.now().getHour
    val minute = LocalDateTime.now().getMinute
    val localDateTime = s"[$hour:$minute]"
    return localDateTime
  }



  override def receive: Receive = {



    case CommonChatMsg (from, message) =>

      val localTime = getLocalTime

      Platform.runLater(new Runnable() {
        override def run(): Unit = {
          CommonWindow.textArea.appendText(s"$localTime $from : $message \n")
        }
      })



    case PrivateChatMsg(from,to, message)=>

      val localTime = getLocalTime

      Platform.runLater(new Runnable {
        override def run(): Unit =
          CommonWindow.textArea.appendText(s"$localTime[Privat msg] $from : $message \n")
      })



    case SelfPrivateChatMsg(from, to, message) =>

      val localTime = getLocalTime

      Platform.runLater(new Runnable() {
        override def run(): Unit = {
          CommonWindow.textArea.appendText(s"$localTime[Privat msg] $from : $message \n")
        }
      })
  }
}
