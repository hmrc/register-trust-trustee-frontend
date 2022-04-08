/*
 * Copyright 2022 HM Revenue & Customs
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

import config.FrontendAppConfig
import config.annotations.TrusteeIndividual
import controllers.actions._
import controllers.actions.register.TrusteeNameRequest
import controllers.actions.register.trustees.individual.NameRequiredActionImpl
import controllers.filters.IndexActionFilterProvider
import forms.NinoFormProvider
import navigation.Navigator
import pages.register.trustees.individual.NinoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.trustees.individual.NinoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NinoController @Inject()(
                                override val messagesApi: MessagesApi,
                                implicit val frontendAppConfig: FrontendAppConfig,
                                registrationsRepository: RegistrationsRepository,
                                @TrusteeIndividual navigator: Navigator,
                                standardActionSets: StandardActionSets,
                                nameAction: NameRequiredActionImpl,
                                validateIndex: IndexActionFilterProvider,
                                formProvider: NinoFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: NinoView
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen validateIndex(index, Trustees) andThen nameAction(index)

  def form(index: Int)(implicit request: TrusteeNameRequest[AnyContent]): Form[String] =
    formProvider("trustee.individual.nino", request.userAnswers, index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(NinoPage(index)) match {
        case None => form(index)
        case Some(value) => form(index).fill(value)
      }

      Ok(view(preparedForm, draftId, index, request.trusteeName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      form(index).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(NinoPage(index), value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(NinoPage(index), draftId, updatedAnswers))
        }
      )
  }
}
