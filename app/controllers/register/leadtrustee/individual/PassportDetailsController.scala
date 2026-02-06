/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.PassportOrIdCardFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.individual.PassportDetailsPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.individual.PassportDetailsView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class PassportDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val frontendAppConfig: FrontendAppConfig,
  registrationsRepository: RegistrationsRepository,
  @LeadTrusteeIndividual navigator: Navigator,
  standardActionSets: StandardActionSets,
  nameAction: NameRequiredActionImpl,
  formProvider: PassportOrIdCardFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PassportDetailsView,
  val countryOptions: CountryOptions,
  errorPageView: InternalServerErrorPageView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val form = formProvider("leadTrustee.individual.passportDetails")

  private def actions(index: Int, draftId: String) =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(PassportDetailsPage(index)) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, countryOptions.options(), draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(
            BadRequest(view(formWithErrors, countryOptions.options(), draftId, index, request.trusteeName))
          ),
        value =>
          request.userAnswers.set(PassportDetailsPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(PassportDetailsPage(index), draftId, updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[PassportDetailsController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
      )
  }

}
