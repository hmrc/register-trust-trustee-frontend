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

import controllers.actions.StandardActionSets
import controllers.actions.register.TrusteeNameRequest
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import models.UserAnswers
import models.registration.pages.DetailsChoice
import models.registration.pages.DetailsChoice._
import pages.register.leadtrustee.individual.{TrusteeDetailsChoicePage, TrusteeNinoYesNoPage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.individual.MatchingLockedView

import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success}

class MatchingLockedController @Inject()(val controllerComponents: MessagesControllerComponents,
                                          standardActionSets: StandardActionSets,
                                          nameAction: NameRequiredActionImpl,
                                          view: MatchingLockedView,
                                          registrationsRepository: RegistrationsRepository,
                                          errorPageView: InternalServerErrorPageView
                                        ) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String): ActionBuilder[TrusteeNameRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      Ok(view(draftId, index, request.trusteeName))
  }

  def continueWithPassport(index: Int, draftId: String): Action[AnyContent] =
    amendUserAnswersAndRedirect(index, draftId, Passport, routes.PassportDetailsController.onPageLoad(index, draftId))

  def continueWithIdCard(index: Int, draftId: String): Action[AnyContent] =
    amendUserAnswersAndRedirect(index, draftId, IdCard, routes.IDCardDetailsController.onPageLoad(index, draftId))

  private def amendUserAnswersAndRedirect(index: Int,
                                          draftId: String,
                                          detailsChoice: DetailsChoice,
                                          call: Call): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>
      request.userAnswers.set(TrusteeNinoYesNoPage(index), false) match {
        case Success(ninoYesNoSet) =>
          handleDetailsChoiceSet(ninoYesNoSet, index, detailsChoice, call)
        case Failure(_) =>
          logger.error("[MatchingLockedController][amendUserAnswersAndRedirect] Error while storing user answers")
         Future.successful(InternalServerError(errorPageView()))
      }
  }

  private def handleDetailsChoiceSet(ninoYesNoSet: UserAnswers, index: Int, detailsChoice: DetailsChoice, call: Call)
                                    (implicit hc: HeaderCarrier, request: TrusteeNameRequest[_]): Future[Result] = {
    ninoYesNoSet.set(TrusteeDetailsChoicePage(index), detailsChoice) match {
      case Success(detailsChoiceSet) =>
        registrationsRepository.set(detailsChoiceSet)
        Future.successful(Redirect(call))
      case Failure(_) =>
        logger.error("[MatchingLockedController][handleDetailsChoiceSet] Error while storing user answers")
        Future.successful(InternalServerError(errorPageView()))

    }
  }
}
