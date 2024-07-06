package cluster

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

class ChatCluster(myPath: String, meetingManager: ActorRef)
    extends Actor
    with ActorLogging {

  private val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info(s"Listener node is up: $member")
      if (actorPath(member) != myPath) {
        meetingManager ! RequestNameSession(actorPath(member))
      }

    case UnreachableMember(member) =>
      log.info(s"Listener node is unreachable: $member")
      meetingManager ! RemoteLogout(actorPath(member))

    case MemberRemoved(member, _) =>
      log.info(s"Listener node is removed: $member")
      meetingManager ! RemoteLogout(actorPath(member))

    case ev: MemberEvent =>
      log.info(s"Listener event: $ev")
  }

  def actorPath(member: Member): String = s"${member.address.toString}/user/meetingManager"

}
