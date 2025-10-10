/*
 * Copyright 2025 HM Revenue & Customs
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
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import forms.YesNoFormProvider
import navigation.Navigator
import pages.register.leadtrustee.individual.TrusteeNinoYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.TrustsIndividualCheckService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.individual.NinoYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class NinoYesNoController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     implicit val frontendAppConfig: FrontendAppConfig,
                                     registrationsRepository: RegistrationsRepository,
                                     @LeadTrusteeIndividual navigator: Navigator,
                                     val standardActionSets: StandardActionSets,
                                     val nameAction: NameRequiredActionImpl,
                                     formProvider: YesNoFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: NinoYesNoView,
                                     errorPageView: InternalServerErrorPageView,
                                     val trustsIndividualCheckService: TrustsIndividualCheckService
                                   )(implicit val ec: ExecutionContext) extends FrontendBaseController with NinoControllerHelper with I18nSupport with Logging {

  private val form = formProvider.withPrefix("leadTrustee.individual.ninoYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>
      maxFailedAttemptsReached(draftId).flatMap { isMaxAttemptsReached =>
        if (isMaxAttemptsReached) {
          Future.successful(redirectToFailedAttemptsPage(index, draftId))
        } else {
          val preparedForm = request.userAnswers.get(TrusteeNinoYesNoPage(index)) match {
            case None => form
            case Some(value) => form.fill(value)
          }
          Future.successful(Ok(view(preparedForm, draftId, index, request.trusteeName, isLeadTrusteeMatched(index))))
        }
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName, isLeadTrusteeMatched(index)))),

        value => {
          request.userAnswers.set(TrusteeNinoYesNoPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map{ _ =>
                Redirect(navigator.nextPage(TrusteeNinoYesNoPage(index), draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[NinoYesNoController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }
}
