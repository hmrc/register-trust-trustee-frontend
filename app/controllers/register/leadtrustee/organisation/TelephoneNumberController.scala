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

package controllers.register.leadtrustee.organisation

import config.FrontendAppConfig
import config.annotations.LeadTrusteeOrganisation
import controllers.actions.StandardActionSets
import controllers.actions.register.leadtrustee.organisation.NameRequiredActionImpl
import forms.TelephoneNumberFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.organisation.TelephoneNumberPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.organisation.TelephoneNumberView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class TelephoneNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val frontendAppConfig: FrontendAppConfig,
  val controllerComponents: MessagesControllerComponents,
  registrationsRepository: RegistrationsRepository,
  @LeadTrusteeOrganisation navigator: Navigator,
  standardActionSets: StandardActionSets,
  nameAction: NameRequiredActionImpl,
  formProvider: TelephoneNumberFormProvider,
  view: TelephoneNumberView,
  errorPageView: InternalServerErrorPageView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction(index)

  val form: Form[String] = formProvider("leadTrustee.organisation.telephoneNumber")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(TelephoneNumberPage(index)) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),
        value =>
          request.userAnswers.set(TelephoneNumberPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(TelephoneNumberPage(index), draftId, updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[TelephoneNumberController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
      )
  }

}
