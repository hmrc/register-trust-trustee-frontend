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

package controllers.register.trustees.organisation

import config.annotations.TrusteeOrganisation
import controllers.actions._
import forms.UKAddressFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.register.trustees.organisation.{UkAddressPage, NamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trustees.organisation.TrusteesOrgUkAddressView

import scala.concurrent.{ExecutionContext, Future}

class UkAddressController @Inject()(
                                     override val messagesApi: MessagesApi,
                                     registrationsRepository: RegistrationsRepository,
                                     @TrusteeOrganisation navigator: Navigator,
                                     standardActionSets: StandardActionSets,
                                     formProvider: UKAddressFormProvider,
                                     val controllerComponents: MessagesControllerComponents,
                                     view: TrusteesOrgUkAddressView
                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val orgName = request.userAnswers.get(NamePage(index)).get

      val preparedForm = request.userAnswers.get(UkAddressPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, orgName))
  }


  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val orgName = request.userAnswers.get(NamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, orgName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(UkAddressPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(UkAddressPage(index), draftId, updatedAnswers))
        }
      )
  }
}
