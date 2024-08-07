/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import config.annotations.LeadTrusteeIndividual
import controllers.actions._
import controllers.actions.register.TrusteeNameRequest
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import forms.NinoFormProvider
import handlers.ErrorHandler
import models._
import navigation.Navigator
import pages.register.leadtrustee.individual.{MatchedYesNoPage, TrusteesNinoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.RegistrationsRepository
import services.{DraftRegistrationService, TrustsIndividualCheckService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.individual.NinoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class NinoController @Inject()(
                                override val messagesApi: MessagesApi,
                                implicit val frontendAppConfig: FrontendAppConfig,
                                registrationsRepository: RegistrationsRepository,
                                @LeadTrusteeIndividual navigator: Navigator,
                                val standardActionSets: StandardActionSets,
                                val nameAction: NameRequiredActionImpl,
                                formProvider: NinoFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: NinoView,
                                val trustsIndividualCheckService: TrustsIndividualCheckService,
                                errorHandler: ErrorHandler,
                                errorPageView: InternalServerErrorPageView,
                                draftRegistrationService: DraftRegistrationService
                              )(implicit val ec: ExecutionContext) extends FrontendBaseController with NinoControllerHelper with I18nSupport with Logging {

  private def getForm(draftId: String, index: Int)(implicit request: TrusteeNameRequest[AnyContent]): Future[Form[String]] = {
    getSettlorNinos(draftId).map { existingSettlorNinos =>
      formProvider("leadTrustee.individual.nino", request.userAnswers, index, Seq(existingSettlorNinos))
    }
  }

  private def getSettlorNinos(draftId: String)(implicit request: TrusteeNameRequest[AnyContent]) = {
    draftRegistrationService.retrieveSettlorNinos(draftId)
  }

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>
      maxFailedAttemptsReached(draftId).flatMap { isMaxAttemptsReached =>
        if (isMaxAttemptsReached) {
          Future.successful(redirectToFailedAttemptsPage(index, draftId))
        } else {
          getForm(draftId, index).map { form =>

            val preparedForm = request.userAnswers.get(TrusteesNinoPage(index)) match {
              case None => form
              case Some(value) => form.fill(value)
            }

            Ok(view(preparedForm, draftId, index, request.trusteeName, isLeadTrusteeMatched(index)))
          }
        }
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>
      getForm(draftId, index).flatMap { form =>
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName, isLeadTrusteeMatched(index)))),
          value => {
            request.userAnswers.set(TrusteesNinoPage(index), value) match {
              case Success(updatedAnswers) =>
                trustsIndividualCheckService.matchLeadTrustee(updatedAnswers, index).map { matchingResponse =>
                  handleMatchingResponse(updatedAnswers, index, draftId, matchingResponse)
                }
              case Failure(_) =>
                logger.error("[NinoController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(errorPageView()))
            }
          }
        )
      }
  }

  private def handleMatchingResponse(updatedAnswers: UserAnswers, index: Int, draftId: String,
                                     matchingResponse: TrustsIndividualCheckServiceResponse)
                                    (implicit hc: HeaderCarrier, request: TrusteeNameRequest[_]): Result = {
    updatedAnswers.set(MatchedYesNoPage(index), matchingResponse == SuccessfulMatchResponse) match {
      case Success(updatedAnswersWithMatched) =>
        registrationsRepository.set(updatedAnswersWithMatched)
        matchingResponse match {
          case SuccessfulMatchResponse | ServiceNotIn5mldModeResponse =>
            Redirect(navigator.nextPage(TrusteesNinoPage(index), draftId, updatedAnswersWithMatched))
          case UnsuccessfulMatchResponse =>
            removeNinoAndRedirect(updatedAnswersWithMatched, index, draftId, Redirect(routes.MatchingFailedController.onPageLoad(index, draftId)))
          case LockedMatchResponse =>
            removeNinoAndRedirect(updatedAnswersWithMatched, index, draftId, Redirect(routes.MatchingLockedController.onPageLoad(index, draftId)))
          case _ =>
            InternalServerError(errorPageView())
        }
      case Failure(_) =>
        logger.error("[NinoController][handleMatching] Error while storing user answers")
        InternalServerError(errorPageView())
    }
  }

  private def removeNinoAndRedirect(updatedAnswers: UserAnswers, index: Int, draftId: String, redirect: Result)
                                   (implicit hc: HeaderCarrier, request: TrusteeNameRequest[_]): Result = {
    updatedAnswers.remove(TrusteesNinoPage(index)) match {
      case Success(updatedAnswersWithMatched) =>
        registrationsRepository.set(updatedAnswersWithMatched)
        redirect
      case Failure(_) => {
        logger.error("[NinoController][removeNinoAndRedirect] Error while removing NI from user answers")
        InternalServerError(errorPageView())
      }
    }
  }
}
