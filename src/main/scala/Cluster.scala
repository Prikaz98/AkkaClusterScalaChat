import Main.{myPath,meetingManager}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import javafx.application.Application

class Cluster extends Actor with ActorLogging{

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)


  override def receive: Receive =  {
    case MemberUp(member) =>
      log.info(s"Listener node is up: $member")
      if (member.address + "/user/meetingManager" != myPath) {
        var actorPath = member.address + "/user/meetingManager"
        meetingManager ! RequestNameSession(actorPath)
      }

    case UnreachableMember(member) =>
      log.info(s"Listener node is unreachable: $member")
      var actorPath = member.address + "/user/meetingManager"
      meetingManager ! RemoteLogout(actorPath)

    case MemberRemoved(member, prevStatus) =>
      log.info(s"Listener node is removed: $member")

    case ev: MemberEvent =>
      log.info(s"Listener event: $ev")
  }

}
object Main extends App{
  val system = ActorSystem("system")
  val cluster = system.actorOf(Props[Cluster],"cluster")
  val meetingManager = system.actorOf(Props[MeetingManager], "meetingManager")
  val myPath = "akka://system@127.0.0.1:2552/user/meetingManager"
  val name= "Vasya"
  Application.launch(classOf[ClientWindowStart], args: _*)
}
