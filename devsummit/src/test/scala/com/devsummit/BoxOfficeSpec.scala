package com.devsummit

import akka.testkit.{ImplicitSender, TestKit}
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import org.scalatest.{WordSpecLike, MustMatchers}

class BoxOfficeSpec extends TestKit(ActorSystem("testTickets"))
                       with WordSpecLike
                       with MustMatchers
                       with ImplicitSender
                       with StopSystemAfterAll {
  "The TicketMaster" must {

    "Create an event and get tickets from the correct Ticket Seller" in {
      import TicketProtocol._

      val boxOffice = system.actorOf(Props[BoxOffice])
      boxOffice ! Event("Akka", 10)
      expectMsg(EventCreated)

      boxOffice ! TicketRequest("Akka")
      expectMsg(Ticket("Akka", 1))

      boxOffice ! TicketRequest("DavidBowie")
      expectMsg(SoldOut)

    }

    "Create a child actor when an event is created and send it a Tickets message" in {
      import TicketProtocol._

      val boxOffice = system.actorOf(Props(new BoxOffice with TestActorCreateTicketSellers {
                                         def testActorRef = testActor
                                    }))
      boxOffice ! Event("Akka", 3)
      expectMsg(Tickets(List(Ticket("Akka",1),Ticket("Akka",2),Ticket("Akka",3))))
      expectMsg(EventCreated)

    }

  }
}

trait TestActorCreateTicketSellers extends CreateTicketSellers { self:Actor =>
  def testActorRef:ActorRef
  override def createTicketSeller(name: String): ActorRef = testActorRef
}

// think of a way to test that one actor is blocking?
//
