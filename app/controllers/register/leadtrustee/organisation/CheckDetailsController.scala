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

import config.FrontendAppConfig
import controllers.actions._
import controllers.actions.register.leadtrustee.organisation.NameRequiredAction
import javax.inject.Inject
import models.Status.Completed
import navigation.Navigator
import pages.entitystatus.LeadTrusteeStatus
import pages.register.trustees.TrusteesAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.print.LeadTrusteeOrganisationPrintHelper
import viewmodels.AnswerSection
import views.html.register.leadtrustee.organisation.CheckDetailsView

import scala.concurrent.{ExecutionContext, Future}

class CheckDetailsController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        registrationsRepository: RegistrationsRepository,
                                        navigator: Navigator,
                                        standardActionSets: StandardActionSets,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: CheckDetailsView,
                                        val appConfig: FrontendAppConfig,
                                        printHelper: LeadTrusteeOrganisationPrintHelper,
                                        nameAction: NameRequiredAction
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen nameAction

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val section: AnswerSection = printHelper.checkDetailsSection(request.userAnswers, request.trusteeName, draftId)

      Ok(view(section, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(LeadTrusteeStatus, Completed)

      for {
        updatedAnswers <- Future.fromTry(answers)
        _ <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(TrusteesAnswerPage, draftId, request.userAnswers))
  }
}
