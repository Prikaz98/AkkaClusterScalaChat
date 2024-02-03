package cluster

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class ChatCluster(myPath : String, meetingManager : ActorRef) extends Actor with ActorLogging {

  private val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info(s"Listener node is up: $member")
      if (member.address + "/user/meetingManager" != myPath) {
        val actorPath = member.address + "/user/meetingManager"
        meetingManager ! RequestNameSession(actorPath)
      }

    case UnreachableMember(member) =>
      log.info(s"Listener node is unreachable: $member")
      val actorPath = member.address + "/user/meetingManager"
      meetingManager ! RemoteLogout(actorPath)

    case MemberRemoved(member, prevStatus) =>
      log.info(s"Listener node is removed: $member")

    case ev: MemberEvent =>
      log.info(s"Listener event: $ev")
  }

}
