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
import forms.NameFormProvider
import handlers.ErrorHandler
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.leadtrustee.individual.TrusteesNamePage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.NameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class NameController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val frontendAppConfig: FrontendAppConfig,
  registrationsRepository: RegistrationsRepository,
  @LeadTrusteeIndividual navigator: Navigator,
  standardActionSets: StandardActionSets,
  formProvider: NameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: NameView,
  errorHandler: ErrorHandler
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val form = formProvider("leadTrustee.individual.name")

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index)

  private def isLeadTrusteeMatched(index: Int)(implicit request: RegistrationDataRequest[_]) =
    request.userAnswers.isLeadTrusteeMatched(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(TrusteesNamePage(index)) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId, index, isLeadTrusteeMatched(index)))

  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, isLeadTrusteeMatched(index)))),
        value =>
          request.userAnswers.set(TrusteesNamePage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(TrusteesNamePage(index), draftId, updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[NameController][onSubmit] Error while storing user answers")
              errorHandler.internalServerErrorTemplate.flatMap(html => Future.successful(InternalServerError(html)))
          }
      )
  }

}
