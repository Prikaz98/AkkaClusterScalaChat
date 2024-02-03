package cluster

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import model.ChatModel

case class Demon(name: String, port: String, model: ChatModel) {

  private val mainConf: Config = port match {
    case "2551" =>
      ConfigFactory.load("app.conf")
    case "2552" =>
      ConfigFactory.load("app1.conf")
    case "2553" =>
      ConfigFactory.load("app2.conf")
    case other =>
      throw new IllegalArgumentException(
        s"couldn't find fitted config by port $other"
      )
  }

  private val system: ActorSystem = ActorSystem("system", mainConf)
  val myPath = s"akka://system@127.0.0.1:$port/user/meetingManager"
  val meetingManager: ActorRef = system.actorOf(
    Props(classOf[MeetingManager], model, myPath, name),
    "meetingManager"
  )
  system.actorOf(Props(classOf[ChatCluster], myPath, meetingManager), "cluster")
}
