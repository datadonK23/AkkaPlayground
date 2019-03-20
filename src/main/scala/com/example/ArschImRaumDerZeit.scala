package com.example

import akka.actor.{Actor, ActorSystem, Props}
import scala.io.StdIn


// Companion object
object ArschImRaumDerZeit {
  def props: Props = Props(new ArschImRaumDerZeit)
}

// Actor
class ArschImRaumDerZeit extends Actor {
  override def receive: Receive = {
    case _: String => println(s"$self: Fick Dich!")
  }
}

object AkkaExperiment extends App {
  val system = ActorSystem("wizoSystem")

  val arschImRaumDerZeit1 = system.actorOf(ArschImRaumDerZeit.props,
    "AiRdZ1")
  val command = StdIn.readLine(s"Tell $arschImRaumDerZeit1 something:")
  arschImRaumDerZeit1 ! command

  Thread.sleep(10)
  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()
}