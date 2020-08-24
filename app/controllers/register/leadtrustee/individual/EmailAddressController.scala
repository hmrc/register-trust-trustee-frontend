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

package controllers.register.leadtrustee.individual

import config.FrontendAppConfig
import config.annotations.LeadTrusteeIndividual
import controllers.actions.StandardActionSets
import controllers.actions.register.leadtrustee.individual.NameRequiredActionImpl
import forms.EmailAddressFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.leadtrustee.individual.{EmailAddressPage, TrusteesNamePage}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.EmailAddressView

import scala.concurrent.{ExecutionContext, Future}

class EmailAddressController @Inject()(
                                        val controllerComponents: MessagesControllerComponents,
                                        implicit val frontendAppConfig: FrontendAppConfig,
                                        standardActionSets: StandardActionSets,
                                        nameAction: NameRequiredActionImpl,
                                        formProvider: EmailAddressFormProvider,
                                        view: EmailAddressView,
                                        registrationsRepository: RegistrationsRepository,
                                        @LeadTrusteeIndividual navigator: Navigator
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[String] = formProvider.withPrefix("leadTrustee.individual.email")

  private def actions(index: Int, draftId: String) =
    standardActionSets.indexValidated(draftId, index) andThen nameAction(index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(EmailAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(EmailAddressPage(index), value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(EmailAddressPage(index), draftId, updatedAnswers))
      )
  }
}
