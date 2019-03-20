package com.example

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.example.StatelessDatadonK.NiceHuman.{OfferBadBeverage, OfferGoodBeverage}

import scala.io.StdIn


object StatelessDatadonK extends App {
  /* Akka demonstration - stateless replies
     Example: A nice human offers DatadonK-Actor some beverage.
     Inspired by Changing behavior demonstration in Akka Essentials Course by Daniel Ciocirlan.
  */

  object DatadonK {
    case class Reply(withMsg: String)
  }

  class DatadonK extends Actor {
    import DatadonK._, NiceHuman._

    override def receive: Receive = happyDatadonK

    def happyDatadonK: Receive = {
      case Beverege("Espresso") =>
      case Beverege("Red Bull") => context.become(sadDatadonK)
      case Inquire(message)     => sender() ! Reply("Ja, Danke")
    }

    def sadDatadonK: Receive = {
      case Beverege("Espresso") => context.become(happyDatadonK)
      case Beverege("Red Bull") =>
      case Inquire(message)     => sender() ! Reply("SC|-|EI$$ RedBull")
    }
  }


  object NiceHuman {
    case class OfferGoodBeverage(to: ActorRef)
    case class OfferBadBeverage(to: ActorRef)
    case class Beverege(beverege: String)
    case class Inquire(message: String)
    val espresso = "Espresso"
    val redbull = "Red Bull"
  }

  class NiceHuman extends Actor {
    import NiceHuman._, DatadonK._

    val proformaQ = "Magst Du das Getränk?"

    override def receive: Receive = {
      case OfferGoodBeverage(ref) =>
        println(s"[$self] bietet einen $espresso an")
        ref ! Beverege(espresso)
        println(s"[$self] $proformaQ")
        ref ! Inquire(proformaQ)
      case OfferBadBeverage(ref) =>
        println(s"[$self] bietet ein $redbull an.")
        ref ! Beverege(redbull)
        println(s"[$self] $proformaQ")
        ref ! Inquire(proformaQ)
      case Reply(msg) => println(s"[${sender()}] hat mit '$msg' geantwortet.")
    }
  }

  val system = ActorSystem("StatelessDatadonK")

  val niceHuman = system.actorOf(Props[NiceHuman], "niceHuman")
  val datadonk23 = system.actorOf(Props[DatadonK], "datadonk23")

  niceHuman ! OfferGoodBeverage(datadonk23)
  Thread.sleep(10)
  niceHuman ! OfferBadBeverage(datadonk23)

  Thread.sleep(10)
  println(">>> Press ENTER to exit <<<")
  try StdIn.readLine()
  finally system.terminate()

}
