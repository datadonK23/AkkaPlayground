package com.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.StdIn

// Supervising actor
object SupervisingActor {
  def props: Props = Props(new SupervisingActor)
}

class SupervisingActor extends Actor {
  val child = context.actorOf(SupervisedActor.props, "supervised-actor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
  }
}

// Supervised actor -> will fail
object SupervisedActor {
  def props: Props = Props(new SupervisedActor)
}

class SupervisedActor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("supervised actor started")
  override def postStop(): Unit = log.info("supervised actor stopped")

  override def receive: Receive = {
    case "fail" =>
      log.info("supervised actor fails now")
      throw new Exception("I failed :(")
  }
}


// Main app
object FailingActor extends App {
  val system = ActorSystem("FailingActor")

  val supervisingActor = system.actorOf(SupervisingActor.props, "supervising-actor")
  supervisingActor ! "failChild"

  Thread.sleep(100)
  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}