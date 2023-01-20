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

package controllers.register.trustees.individual.mld5

import config.FrontendAppConfig
import config.annotations.TrusteeIndividual
import controllers.actions._
import controllers.actions.register.trustees.individual.NameRequiredActionImpl
import forms.YesNoDontKnowFormProvider
import models.YesNoDontKnow
import navigation.Navigator
import pages.register.trustees.individual.mld5.MentalCapacityYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.InternalServerErrorPageView
import views.html.register.trustees.individual.mld5.MentalCapacityYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class MentalCapacityYesNoController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               implicit val frontendAppConfig: FrontendAppConfig,
                                               repository: RegistrationsRepository,
                                               @TrusteeIndividual navigator: Navigator,
                                               standardActionSets: StandardActionSets,
                                               nameAction: NameRequiredActionImpl,
                                               formProvider: YesNoDontKnowFormProvider,
                                               view: MentalCapacityYesNoView,
                                               errorPageView: InternalServerErrorPageView
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[YesNoDontKnow] = formProvider.withPrefix("trustee.individual.5mld.mentalCapacityYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(MentalCapacityYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.trusteeName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.trusteeName))),

          value =>
            request.userAnswers.set(MentalCapacityYesNoPage(index), value) match {
              case Success(updatedAnswers) =>
                repository.set(updatedAnswers).map { _ =>
                  Redirect(navigator.nextPage(MentalCapacityYesNoPage(index), draftId, updatedAnswers))
                }
              case Failure(_) =>
                logger.error("[MentalCapacityYesNoController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(errorPageView()))
            }
        )
    }
}
