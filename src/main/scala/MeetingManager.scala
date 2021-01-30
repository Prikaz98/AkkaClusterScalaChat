import CommonWindow.users
import Main.{myPath, name, system}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import javafx.application.Platform

import scala.collection.mutable.HashMap

class MeetingManager extends Actor with ActorLogging{
  val addressNick = new HashMap[String, String]
  val meetings = new HashMap[String, ActorRef]
  val meeting = system.actorOf(Props(new Meeting(name)))
  addressNick += (myPath -> name)
  meetings += (name -> meeting)


  override def receive: Receive = {

    case RequestNameSession(from) =>
      system.actorSelection(from) ! SendRequestNameSession(from)
    case SendRequestNameSession(from) =>
    sender() ! RemoteLogin(from, name, meeting)
    case RemoteLogin(from, username, session) =>
      println(s"$username login")
      var boolUserEntered: Boolean = false
      for (key <- meetings.keySet if key == username) boolUserEntered = true
      if (boolUserEntered == false) {
        addressNick += (from -> username)
        meetings += (username -> session)
        Platform.runLater(new Runnable {
          override def run(): Unit = users.add(username)
        })
      boolUserEntered = false
  }
    case RemoteLogout(from) =>
      meetings -= (addressNick(from))
      addressNick -= (from)
      val nickName = addressNick(from)
      Platform.runLater(new Runnable {
        override def run(): Unit = users.remove(nickName)
      })
    case chatmsg@CommonChatMsg(from, massage)=>
      for(key <- meetings.keySet){
        meetings(key) ! chatmsg
      }
    case chatmsg@PrivateChatMsg(from, to, message) =>
      meetings(to) ! chatmsg
    case chatmsg@SelfPrivateChatMsg(from, to, message) =>
      meetings(from) ! chatmsg
  }
}


case class RequestNameSession(from: String)
case class SendRequestNameSession(from: String)
case class RemoteLogout(from: String)
case class RemoteLogin(from: String, name: String, session: ActorRef)
case class CommonChatMsg(from: String, message: String)
case class PrivateChatMsg(from: String, to: String, message: String)
case class SelfPrivateChatMsg(from: String, to: String, message: String)