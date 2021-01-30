import Main.{meetingManager, myPath}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory

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
  var name = getName
  var path = getPath
  var num = getNum

  val config = ConfigFactory.load("app.conf")
  val config1 = ConfigFactory.load("app1.conf")
  val config2 = ConfigFactory.load("app2.conf")
  var mainConf=config

  if (num == 2 ) mainConf = config1
  if (num == 3 ) mainConf = config2

  val system = ActorSystem("system",mainConf)
  val cluster = system.actorOf(Props[Cluster],"cluster")
  val meetingManager = system.actorOf(Props[MeetingManager], "meetingManager")
  val myPath = s"akka://system@127.0.0.1:$path/user/meetingManager"




  def setName(name: String) = {
    this.name = name
  }
  def getName():String= {
  return name
  }
  def setPathAndNum(path:String, num: Int) ={
    this.path = path
    this.num = num
  }
  def getPath():String={
  return path
  }
  def getNum():Int={
    return num
  }

}
