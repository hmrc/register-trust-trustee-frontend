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

package controllers.register.trustees.individual

import config.annotations.TrusteeIndividual
import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import forms.NinoFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.trustees.IsThisLeadTrusteePage
import pages.register.trustees.individual.{NamePage, NinoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.register.trustees.individual.NinoView

import scala.concurrent.{ExecutionContext, Future}

class NinoController @Inject()(
                                override val messagesApi: MessagesApi,
                                registrationsRepository: RegistrationsRepository,
                                @TrusteeIndividual navigator: Navigator,
                                identify: RegistrationIdentifierAction,
                                getData: DraftIdRetrievalActionProvider,
                                requireData: RegistrationDataRequiredAction,
                                validateIndex: IndexActionFilterProvider,
                                requiredAnswer: RequiredAnswerActionProvider,
                                formProvider: NinoFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: NinoView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    identify andThen
      getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(NamePage(index), routes.NameController.onPageLoad(index, draftId))) andThen
      requiredAnswer(RequiredAnswer(IsThisLeadTrusteePage(index), controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val trusteeName = request.userAnswers.get(NamePage(index)).get.toString

      val form = formProvider("trustee.individual.nino")

      val preparedForm = request.userAnswers.get(NinoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val trusteeName = request.userAnswers.get(NamePage(index)).get.toString

      val form = formProvider("trustee.individual.nino")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, trusteeName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NinoPage(index), value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NinoPage(index), draftId, updatedAnswers))
        }
      )
  }
}
