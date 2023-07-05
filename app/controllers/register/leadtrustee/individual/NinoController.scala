/*
 * Copyright 2023 HM Revenue & Customs
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
import services.DraftRegistrationService
import services.TrustsIndividualCheckService
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
                                standardActionSets: StandardActionSets,
                                nameAction: NameRequiredActionImpl,
                                formProvider: NinoFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: NinoView,
                                service: TrustsIndividualCheckService,
                                errorHandler: ErrorHandler,
                                errorPageView: InternalServerErrorPageView,
                                draftRegistrationService: DraftRegistrationService
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def getForm(draftId: String, index: Int)(implicit request: TrusteeNameRequest[AnyContent]): Future[Form[String]] = {
    for {
      existingSettlorNinos <- getSettlorNinos(draftId)
    } yield {
      formProvider("leadTrustee.individual.nino", request.userAnswers, index, Seq(existingSettlorNinos))
    }
  }

  private def getSettlorNinos(draftId: String)(implicit request: TrusteeNameRequest[AnyContent]) = {
    draftRegistrationService.retrieveSettlorNinos(draftId)
  }

  private def actions(index: Int, draftId: String): ActionBuilder[TrusteeNameRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  private def isLeadTrusteeMatched(index: Int)(implicit request: TrusteeNameRequest[_]) =
    request.userAnswers.isLeadTrusteeMatched(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      getForm(draftId, index).map { form =>

        val preparedForm = request.userAnswers.get(TrusteesNinoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.trusteeName, isLeadTrusteeMatched(index)))
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      getForm(draftId, index).flatMap { form =>

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName, isLeadTrusteeMatched(index)))),

          value => {
            request.userAnswers.set(TrusteesNinoPage(index), value) match {
              case Success(updatedAnswers) =>
                service.matchLeadTrustee(updatedAnswers, index).map { matchingResponse =>
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
                                    (implicit hc: HeaderCarrier, request: TrusteeNameRequest[_]): Result =
    updatedAnswers.set(MatchedYesNoPage(index), matchingResponse == SuccessfulMatchResponse) match {
      case Success(updatedAnswersWithMatched) =>
        registrationsRepository.set(updatedAnswersWithMatched)
        matchingResponse match {
          case SuccessfulMatchResponse | ServiceNotIn5mldModeResponse =>
            Redirect(navigator.nextPage(TrusteesNinoPage(index), draftId, updatedAnswersWithMatched))
          case UnsuccessfulMatchResponse =>
            Redirect(routes.MatchingFailedController.onPageLoad(index, draftId))
          case LockedMatchResponse =>
            Redirect(routes.MatchingLockedController.onPageLoad(index, draftId))
          case _ =>
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
      case Failure(_) =>
        logger.error("[NinoController][handleMatchingResponse] Error while storing user answers")
        InternalServerError(errorPageView())
    }
}
