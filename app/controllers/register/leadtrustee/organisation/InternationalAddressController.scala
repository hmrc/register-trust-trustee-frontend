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

package controllers.register.leadtrustee.organisation

import config.FrontendAppConfig
import config.annotations.LeadTrusteeOrganisation
import controllers.actions._
import controllers.actions.register.leadtrustee.organisation.NameRequiredActionImpl
import forms.InternationalAddressFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.organisation.InternationalAddressPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.organisation.InternationalAddressView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class InternationalAddressController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                implicit val frontendAppConfig: FrontendAppConfig,
                                                registrationsRepository: RegistrationsRepository,
                                                @LeadTrusteeOrganisation navigator: Navigator,
                                                standardActionSets: StandardActionSets,
                                                nameAction: NameRequiredActionImpl,
                                                formProvider: InternationalAddressFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: InternationalAddressView,
                                                val countryOptions: CountryOptionsNonUK,
                                                errorPageView: InternalServerErrorPageView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form = formProvider()

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(InternationalAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, draftId, index, request.trusteeName))),

        value => {
          request.userAnswers.set(InternationalAddressPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map{ _ =>
                Redirect(navigator.nextPage(InternationalAddressPage(index), draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[InternationalAddressController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }
}
