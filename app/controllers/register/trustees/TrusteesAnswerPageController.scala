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

package controllers.register.trustees

import controllers.actions._
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import controllers.filters.IndexActionFilterProvider
import javax.inject.Inject
import models.Status.Completed
import models.core.pages.IndividualOrBusiness
import navigation.Navigator
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage, TrusteesAnswerPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.Trustees
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.answers.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.register.trustees.TrusteesAnswerPageView

import scala.concurrent.{ExecutionContext, Future}

class TrusteesAnswerPageController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              registrationsRepository: RegistrationsRepository,
                                              identify: RegistrationIdentifierAction,
                                              navigator: Navigator,
                                              getData: DraftIdRetrievalActionProvider,
                                              requireData: RegistrationDataRequiredAction,
                                              requiredAnswer: RequiredAnswerActionProvider,
                                              validateIndex : IndexActionFilterProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: TrusteesAnswerPageView,
                                              countryOptions : CountryOptions
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index : Int, draftId: String) =
    identify andThen getData(draftId) andThen
      requireData andThen
      validateIndex(index, Trustees) andThen
      requiredAnswer(RequiredAnswer(TrusteeIndividualOrBusinessPage(index),routes.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId))) andThen
      requiredAnswer(RequiredAnswer(IsThisLeadTrusteePage(index), routes.IsThisLeadTrusteeController.onPageLoad(index, draftId)))

  def onPageLoad(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val isLead = request.userAnswers.get(IsThisLeadTrusteePage(index)).get

      val trusteeIndividualOrBusinessMessagePrefix = if (isLead) "leadTrusteeIndividualOrBusiness" else "trusteeIndividualOrBusiness"

      val sections = Seq(
        AnswerSection(
          None,
          request.userAnswers.get(TrusteeIndividualOrBusinessPage(index)) match {
            case Some(IndividualOrBusiness.Individual) =>
              Seq(
                checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
                checkYourAnswersHelper.trusteeFullName(index),
                checkYourAnswersHelper.trusteesDateOfBirth(index),
                checkYourAnswersHelper.trusteeNinoYesNo(index),
                checkYourAnswersHelper.trusteesNino(index),
                checkYourAnswersHelper.trusteeLiveInTheUK(index),
                checkYourAnswersHelper.trusteesUkAddress(index),
                checkYourAnswersHelper.trusteePassportDetailsYesNo(index),
                checkYourAnswersHelper.trusteesPassportDetails(index),
                checkYourAnswersHelper.trusteeIDCardDetailsYesNo(index),
                checkYourAnswersHelper.trusteesIDCardDetails(index),
                checkYourAnswersHelper.telephoneNumber(index)
              ).flatten

            case Some(IndividualOrBusiness.Business) =>
              Seq(
                checkYourAnswersHelper.trusteeIndividualOrBusiness(index, trusteeIndividualOrBusinessMessagePrefix),
                checkYourAnswersHelper.trusteeUtrYesNo(index),
                checkYourAnswersHelper.trusteeOrgName(index),
                checkYourAnswersHelper.trusteeUtr(index),
                checkYourAnswersHelper.orgAddressInTheUkYesNo(index),
                checkYourAnswersHelper.trusteesOrgUkAddress(index),
                checkYourAnswersHelper.trusteeOrgInternationalAddress(index)
              ).flatten

              case None =>
              Nil
          }
        )
      )

      Ok(view(index, draftId ,sections))
  }

  def onSubmit(index : Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

    val answers = request.userAnswers.set(TrusteeStatus(index), Completed)

    for {
      updatedAnswers <- Future.fromTry(answers)
      _              <- registrationsRepository.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(TrusteesAnswerPage, draftId, request.userAnswers))
  }
}