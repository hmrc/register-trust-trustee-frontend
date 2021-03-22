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
import controllers.actions.StandardActionSets
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.register.leadtrustee.individual.FailedMatchingPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.leadtrustee.individual.FailedMatchingView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FailedMatchingController @Inject()(
                                          val controllerComponents: MessagesControllerComponents,
                                          implicit val frontendAppConfig: FrontendAppConfig,
                                          standardActionSets: StandardActionSets,
                                          view: FailedMatchingView,
                                          registrationsRepository: RegistrationsRepository,
                                          @LeadTrusteeIndividual navigator: Navigator
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.indexValidated(draftId, index)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val numberFailedAttempts: Int = request.userAnswers.get(FailedMatchingPage(index)).getOrElse(1)
      Ok(view(draftId, index, numberFailedAttempts))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val numberFailedAttempts: Int = request.userAnswers.get(FailedMatchingPage(index)).getOrElse(1)

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(FailedMatchingPage(index), numberFailedAttempts + 1))
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(FailedMatchingPage(index), draftId, updatedAnswers))
  }
}
