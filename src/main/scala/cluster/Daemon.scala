package cluster

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import model.ChatModel

case class Demon(name: String, port: String, model: ChatModel) {
  System.setProperty("USER_CLUSTER_PORT", port)

  private val mainConf: Config = ConfigFactory.load("app.conf")

  private val system: ActorSystem = ActorSystem("system", mainConf)
  val myPath = s"akka://system@127.0.0.1:$port/user/meetingManager"
  val meetingManager: ActorRef = system.actorOf(Props(classOf[MeetingManager], model, myPath, name),"meetingManager")
  system.actorOf(Props(classOf[ChatCluster], myPath, meetingManager), "cluster")
}
