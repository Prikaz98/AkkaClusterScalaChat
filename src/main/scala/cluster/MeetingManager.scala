package cluster

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import javafx.application.Platform
import model.ChatModel

import scala.collection.mutable.HashMap

class MeetingManager(model: ChatModel, myPath: String, name: String)
    extends Actor
    with ActorLogging {

  private val addressNick = new HashMap[String, String]
  private val meetings = new HashMap[String, ActorRef]
  private val meeting = this.context.system.actorOf(Props(new Meeting(model)))
  addressNick += (myPath -> name)
  meetings += (name -> meeting)

  override def receive: Receive = {

    case RequestNameSession(from) =>
      this.context.system.actorSelection(from) ! SendRequestNameSession(from)
    case SendRequestNameSession(from) =>
      sender() ! RemoteLogin(from, name, meeting)
    case RemoteLogin(from, username, session) =>
      log.info(s"$username login")
      var boolUserEntered: Boolean = false
      for (key <- meetings.keySet if key == username) boolUserEntered = true
      if (boolUserEntered == false) {
        addressNick += (from -> username)
        meetings += (username -> session)
        Platform.runLater(() => model.users.add(username))
        boolUserEntered = false
      }

    case RemoteLogout(from) =>
      meetings -= addressNick(from)
      val nickName = addressNick(from)
      addressNick -= from
      Platform.runLater(() => model.users.remove(nickName))

    case chatmsg @ CommonChatMsg(from, massage) =>
      for (key <- meetings.keySet) {
        meetings(key) ! chatmsg
      }

    case chatmsg @ PrivateChatMsg(from, to, message) =>
      meetings(to) ! chatmsg

    case chatmsg @ SelfPrivateChatMsg(from, to, message) =>
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
