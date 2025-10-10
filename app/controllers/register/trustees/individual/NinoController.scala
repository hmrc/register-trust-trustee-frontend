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
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.trustees.individual.NinoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

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
                                view: NinoView,
                                errorPageView: InternalServerErrorPageView,
                                draftRegistrationService: DraftRegistrationService
                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen validateIndex(index, Trustees) andThen nameAction(index)

  private def getForm(draftId: String, index: Int)(implicit request: TrusteeNameRequest[AnyContent]): Future[Form[String]] = {
    for {
      existingSettlorNinos <- getSettlorNinos(draftId)
    } yield {
      formProvider("trustee.individual.nino", request.userAnswers, index, Seq(existingSettlorNinos))
    }
  }

  private def getSettlorNinos(draftId: String)(implicit request: TrusteeNameRequest[AnyContent]) = {
      draftRegistrationService.retrieveSettlorNinos(draftId)
    }

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      getForm(draftId, index).map { form =>

        val preparedForm = request.userAnswers.get(NinoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.trusteeName))
      }
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      getForm(draftId, index).flatMap { form =>

        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

          value => {
            request.userAnswers.set(NinoPage(index), value) match {
              case Success(updatedAnswers) =>
                registrationsRepository.set(updatedAnswers).map { _ =>
                  Redirect(navigator.nextPage(NinoPage(index), draftId, updatedAnswers))
                }
              case Failure(_) =>
                logger.error("[NinoController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(errorPageView()))
            }
          }
        )
      }
  }
}
