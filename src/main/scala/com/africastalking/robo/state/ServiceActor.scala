package com.africastalking.robo
package state

import scala.concurrent.{ ExecutionContext, Future }

import akka.actor.{ Actor, FSM, Terminated }

import org.slf4j.LoggerFactory

import com.africastalking.robo.free.DBService
import com.africastalking.robo.state.ServiceMessage._
import com.africastalking.robo.persistence.Service
import com.africastalking.robo.utils.XMLResponses

class ServiceActor(
     implicit databaseService: Service[DBService.Action, Future]) extends Actor with FSM[CallState, CallData]{

  import ServiceProtocol._

  implicit def executionContext: ExecutionContext = context.system.dispatcher

  private val logger = LoggerFactory.getLogger(classOf[ServiceActor])
  // implicit val ResolveTimeout: FiniteDuration = 10 seconds

  /** Trying to extend/pimp FSM.State to add a andThen function **/
  implicit class FSMOps[S, D](fsm: FSM.State[S, D]) {
    def andThen[G](f: () => FSM.State[S, D]): FSM.State[S, D] = {
      ??? //fsm.using()
    }
  }

  /** Model to save to a database don't thik we need this here anymore **/
  //val voiceSession = VoiceSessionHops("XXXX", "YYYY", "ZZZZ", "PPPP", "QQQQ", "RRRR", "SSSS", "TTTT")
  //databaseService.run(DBService.saveVoiceSession(voiceSession))
  // .onComplete(_ ⇒ logger.debug("Completed Database insertion"))

  startWith(WaitingForRequest, NoData)

  /** Stage Waiting **/
  when(WaitingForRequest){
    case Event(req: VoiceCall, NoData) if req.isActive == 1 ⇒
      logger.info("Received my First Voice Call Request")
      sender ! XMLResponses.welcome // zero state xml sent
      goto(HandlingStageZeroDigits) using VoiceData(req.sessionId, req.isActive, req.callNumber)
  }

  /** Stage Zero **/
  when(HandlingStageZeroDigits){
    /** User selects English from the zero stage xml **/
    case Event(ZeroStageDigits(_, isActive, _,dtmfdigits), req: VoiceData)
      if dtmfdigits.isDefined  && dtmfdigits.get == "1" && isActive == 1 ⇒
      logger.info("Received request for English menu")
      sender ! XMLResponses.handle_eng // stage one xml sent
      goto(HandlingStageOneDigits) using StageZeroData(req, language = dtmfdigits)
    /** User selects Hausa from the zero stage xml **/
    case Event(ZeroStageDigits(_, isActive, _,dtmfdigits), req: VoiceData)
      if dtmfdigits.isDefined  && dtmfdigits.get == "2" && isActive == 1 ⇒
      logger.info("Received request for Hausa menu")
      sender ! XMLResponses.handle_hausa // stage one xml sent
      goto(HandlingStageOneDigits) using StageZeroData(req, language = dtmfdigits)

    /** Repeat the menu selected **/
    case Event(ZeroStageDigits(_,isActive, _,dtmfdigits), req: VoiceData)
      if dtmfdigits.isDefined && dtmfdigits.get == "0" && isActive == 1 ⇒
      logger.info("Received request to repeat welcome message")
      sender ! XMLResponses.welcome
      stay using req

    /** Try again if the user enters bad input and warn them **/
    case Event(ZeroStageDigits(_,isActive, _, dtmfdigits), req: VoiceData)
      if dtmfdigits.isDefined && isActive == 1 ⇒ sender() ! XMLResponses.welcome
      logger.info("User enters random digits, repeat welcome message")
      stay using req
  }

  /** stage One **/
  when(HandlingStageOneDigits) {
    /** User select prayer from zero stage **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "1" && isActive == 1 ⇒
      data.language match {
        case English ⇒ // English
          sender ! XMLResponses.eng_pray // stage two xml sent
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
        case Hausa ⇒ // Hausa
          sender ! XMLResponses.hausa_pray // stage two xml sent here
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
      }

    /** User select names from zero stage **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "2" && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender ! XMLResponses.eng_names
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
        case Hausa ⇒
          sender ! XMLResponses.hausa_names
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
      }

    /** User select food-tip from zero stage **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "3" && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender ! XMLResponses.eng_foodtips
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
        case Hausa ⇒
          sender ! XMLResponses.hausa_foodtips
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
      }

    /** User select health from zero stage **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "4" && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender() ! XMLResponses.eng_health
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
        case Hausa ⇒
          sender() ! XMLResponses.hausa_health
          goto(HandlingStageTwoDigits) using StageOneData(data, dtmfdigits)
      }

    /** This handles for the previous XMLResponses.handle_eng, or XMLResponses.handle_hausa **/
    /** Repeat only the names menu which is selecting 2 */
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "0" && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender ! XMLResponses.handle_eng
          stay using data
        case Hausa ⇒
          sender ! XMLResponses.handle_hausa
          stay using data
      }

    /** Go back to the previous menu **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && dtmfdigits.get == "#" && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender ! XMLResponses.welcome
          goto(HandlingStageZeroDigits) using
            VoiceData(data.voiceData.sessionId, data.voiceData.isActive, data.voiceData.callerNumber)
        case Hausa ⇒
          sender ! XMLResponses.welcome
          goto(HandlingStageZeroDigits) using
            VoiceData(data.voiceData.sessionId, data.voiceData.isActive, data.voiceData.callerNumber)
      }

    /** If the user enters a random digits repeat the menu selected **/
    case Event(FirstStageDigits(_, isActive, _, dtmfdigits), data: StageZeroData)
      if dtmfdigits.isDefined && isActive == 1 ⇒
      data.language match {
        case English ⇒
          sender ! XMLResponses.handle_eng
          stay using data
        case Hausa ⇒
          sender ! XMLResponses.handle_hausa
          stay using data
      }
  }

  when(HandlingStageTwoDigits) {
    /** User selected typePNFH option for each **/
    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "1" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒
          data.wantToHear match {
            case Prayer ⇒
              sender() ! XMLResponses.prayer_eng_forgive
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒ /** It will actually not get here becasuse the XML sent does not have GetDigts**/
              // sender ! XMLResponses.eng_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 1 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              sender ! XMLResponses.food_eng_suhoor
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_eng_important
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }

        case Hausa ⇒ // language Hausa =>
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_hausa_forgive
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.hausa_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 1 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              sender ! XMLResponses.food_eng_suhoor
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_hausa_meals
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }
      }

    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "2" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English⇒ // language English
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_eng_victory
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.eng_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 2 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              sender ! XMLResponses.food_eng_iftar
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Health ⇒ // health
              sender ! XMLResponses.health_eng_water
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }

        case Hausa ⇒ // language Hausa =>
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_eng_reward
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.hausa_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 2 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              sender ! XMLResponses.food_hausa_iftar
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_hausa_drink
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }
      }

    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "3" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒ // language English
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_eng_reward
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.eng_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 3 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              // sender ! XMLResponses.eng_foodtips (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 3 on food-tips
              // stay using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_eng_time
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }

        case Hausa ⇒ // language Hausa =>
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_hausa_reward
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.hausa_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 3 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              // sender ! XMLResponses.hausa_foodtips (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 3 on food-tips
              // stay using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_hausa_discipline
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }
      }

    case Event(SecondStageDigits(_, isActive, _,dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "4" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒ // language English
          data.wantToHear match  {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_eng_reward
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.eng_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 4 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              // sender ! XMLResponses.eng_foodtips (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 4 on food-tips
              // stay using StageTwoData(data, dtmfdigits)
            case Health ⇒ // health
              sender ! XMLResponses.health_eng_sleep
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }

        case Hausa ⇒ // language Hausa =>
          data.wantToHear match {
            case Prayer ⇒ // prayer
              sender ! XMLResponses.prayer_hausa_reward
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
            case Names ⇒
              // sender ! XMLResponses.hausa_names (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 4 on prayer
              // stay using StageTwoData(data, dtmfdigits)
            case Food ⇒
              // sender ! XMLResponses.hausa_foodtips (maybe we should repeat)
              context.parent ! Terminated
              stop // just stop if user enters wrong dtmf we don't have options for 4 on food-tips
              // stay using StageTwoData(data, dtmfdigits)
            case Health ⇒
              sender ! XMLResponses.health_hausa_rest
              goto(HandlingStageThreeDigits) using StageTwoData(data, dtmfdigits)
          }
      }

    /** Repeat the menu for each the former xml sent **/
    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "0" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒
          data.wantToHear match  {
            case Prayer ⇒
              sender ! XMLResponses.eng_pray
              stay using data
            case Names ⇒
              sender ! XMLResponses.eng_names
              stay using data
            case Food ⇒
              sender ! XMLResponses.eng_foodtips
              stay using data
            case Health ⇒
              sender ! XMLResponses.eng_health
              stay using data
          }

        case Hausa ⇒
          data.wantToHear match {
            case Prayer ⇒
              sender ! XMLResponses.hausa_pray
              stay using data
            case Names ⇒
              sender ! XMLResponses.hausa_names
              stay using data
            case Food ⇒
              sender ! XMLResponses.hausa_foodtips
              stay using data
            case Health ⇒
              sender ! XMLResponses.hausa_health
              stay using data
          }
      }

    /** Going to the previous menu **/
    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData)
      if dtmfdigits.isDefined && dtmfdigits.get == "#" && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒
          sender ! XMLResponses.handle_eng
          goto(HandlingStageOneDigits) using StageZeroData(data.stageZeroData.voiceData, language = Some("1"))

        case Hausa ⇒ // language Hausa
          sender ! XMLResponses.handle_hausa
          goto(HandlingStageOneDigits) using StageZeroData(data.stageZeroData.voiceData, language = Some("0"))
      }

    /** Stop when the user enters a random digits **/
    /** It has to be here so that we don't  match it with # key this is the last matching case**/
    case Event(SecondStageDigits(_, isActive, _, dtmfdigits), data: StageOneData) if dtmfdigits.isDefined && isActive == 1 ⇒
      data.stageZeroData.language match {
        case English ⇒
          data.wantToHear match  {
            case Prayer ⇒
              context.parent ! Terminated
              stop
            case Names ⇒
              context.parent ! Terminated
              stop
            case Food ⇒
              context.parent ! Terminated
              stop
            case Health ⇒
              context.parent ! Terminated
              stop
          }

        case Hausa ⇒
          data.wantToHear match {
            case Prayer ⇒
              context.parent ! Terminated
              stop
            case Names ⇒
              context.parent ! Terminated
              stop
            case Food ⇒
              context.parent ! Terminated
              stop
            case Health =>
              context.parent ! Terminated
              stop
          }
      }

  }

  /** Handles only 0, # and and .isDefined to repeat **/
  when(HandlingStageThreeDigits)(FSM.NullFunction)


  when(EndingCall){
    case Event(EndingCall, _) ⇒
      context.parent ! Terminated
      stop()
  }

  whenUnhandled {
    case Event(EndingCall, _) ⇒
      logger.info("I GOT THE MESSAGE TO END THE CALL")
      context.parent ! Terminated
      stop

    case Event(StateTimeout , _) ⇒
      logger.error("Received state timeout in process to validate an order create request")
      context.parent ! Terminated
      stop

    case Event(other, _) ⇒
      logger.error("Received unexpected message of {} in state {}", other, stateName)
      context.parent ! Terminated
      stop
  }

  onTransition {

    case WaitingForRequest -> HandlingStageZeroDigits ⇒ logger.debug("From WaitingForRequest to HandlingStageZeroDigits")
    // case Active -> _ => cancelTimer("timeout")
    case _ -> WaitingForRequest ⇒ logger.info("I waiting for your request ")
  }

}

