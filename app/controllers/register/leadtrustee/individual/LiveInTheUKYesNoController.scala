/*
 * Copyright 2021 HM Revenue & Customs
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
import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.individual.AddressUkYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.LiveInTheUKYesNoView

import scala.concurrent.{ExecutionContext, Future}

class LiveInTheUKYesNoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            implicit val frontendAppConfig: FrontendAppConfig,
                                            registrationsRepository: RegistrationsRepository,
                                            @LeadTrusteeIndividual navigator: Navigator,
                                            standardActionSets: StandardActionSets,
                                            nameAction: NameRequiredActionImpl,
                                            formProvider: YesNoFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: LiveInTheUKYesNoView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form: Form[Boolean] = formProvider.withPrefix("leadTrustee.individual.liveInTheUkYesNo")

      val preparedForm = request.userAnswers.get(AddressUkYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form: Form[Boolean] = formProvider.withPrefix("leadTrustee.individual.liveInTheUkYesNo")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddressUkYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddressUkYesNoPage(index), draftId, updatedAnswers))
        }
      )
  }
}
