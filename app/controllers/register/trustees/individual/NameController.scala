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

package controllers.register.trustees.individual

import config.FrontendAppConfig
import config.annotations.TrusteeIndividual
import controllers.actions._
import controllers.filters.IndexActionFilterProvider
import forms.NameFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.trustees.individual.NamePage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.trustees.individual.NameView

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class NameController @Inject()(
                                override val messagesApi: MessagesApi,
                                implicit val frontendAppConfig: FrontendAppConfig,
                                registrationsRepository: RegistrationsRepository,
                                @TrusteeIndividual navigator: Navigator,
                                standardActionSets: StandardActionSets,
                                validateIndex: IndexActionFilterProvider,
                                formProvider: NameFormProvider,
                                val controllerComponents: MessagesControllerComponents,
                                view: NameView,
                                errorPageView: InternalServerErrorPageView
                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen validateIndex(index, Trustees)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form = formProvider("trustee.individual.name")

      val preparedForm = request.userAnswers.get(NamePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index))

  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form = formProvider("trustee.individual.name")

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {
          request.userAnswers.set(NamePage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map{ _ =>
                Redirect(navigator.nextPage(NamePage(index), draftId, updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[NameController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(errorPageView()))
          }
        }
      )
  }
}
