/*
 * Copyright 2020 HM Revenue & Customs
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

import config.annotations.LeadTrusteeOrganisation
import controllers.actions.StandardActionSets
import controllers.actions.register.leadtrustee.organisation.NameRequiredAction
import forms.TelephoneNumberFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.organisation.TelephoneNumberPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.leadtrustee.organisation.TelephoneNumberView

import scala.concurrent.{ExecutionContext, Future}

class TelephoneNumberController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val controllerComponents: MessagesControllerComponents,
                                           registrationsRepository: RegistrationsRepository,
                                           @LeadTrusteeOrganisation navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           nameAction: NameRequiredAction,
                                           formProvider: TelephoneNumberFormProvider,
                                           view: TelephoneNumberView
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction

  val form: Form[String] = formProvider("leadTrustee.organisation.telephoneNumber")

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(TelephoneNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, request.trusteeName))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId, request.trusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(TelephoneNumberPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(TelephoneNumberPage, draftId, updatedAnswers))
      )
  }
}
