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

package controllers.register.trustees.organisation

import config.FrontendAppConfig
import config.annotations.TrusteeOrganisation
import controllers.actions._
import controllers.actions.register.trustees.organisation.NameRequiredActionImpl
import forms.YesNoFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.trustees.organisation.AddressUkYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.trustees.organisation.AddressUkYesNoView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AddressUkYesNoController @Inject()(
                                          override val messagesApi: MessagesApi,
                                          implicit val frontendAppConfig: FrontendAppConfig,
                                          registrationsRepository: RegistrationsRepository,
                                          @TrusteeOrganisation navigator: Navigator,
                                          standardActionSets: StandardActionSets,
                                          nameAction: NameRequiredActionImpl,
                                          formProvider: YesNoFormProvider,
                                          val controllerComponents: MessagesControllerComponents,
                                          view: AddressUkYesNoView,
                                          errorPageView: InternalServerErrorPageView
                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction(index)

  private val form: Form[Boolean] = formProvider.withPrefix("trustee.organisation.addressUkYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(AddressUkYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

        value => {
          request.userAnswers.set(AddressUkYesNoPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map{ _ =>
                Redirect(navigator.nextPage(AddressUkYesNoPage(index), draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[AddressUkYesNoController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }
}
