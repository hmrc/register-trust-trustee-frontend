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

package navigation

import config.FrontendAppConfig
import javax.inject.Inject
import models.ReadableUserAnswers
import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness._
import models.registration.pages.AddATrustee
import pages.Page
import pages.register.trustees._
import pages.register.trustees.individual._
import play.api.mvc.Call
import sections.Trustees

class TrusteeRoutes @Inject()(config: FrontendAppConfig){
  def route(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case IsThisLeadTrusteePage(index) => _ => controllers.register.trustees.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, draftId)
    case TrusteeIndividualOrBusinessPage(index) => ua => trusteeIndividualOrBusinessRoute(ua, index, draftId)

    case TrusteesNamePage(index) => _ => controllers.register.trustees.individual.routes.DateOfBirthController.onPageLoad(index, draftId)
    case TrusteesDateOfBirthPage(index)  => ua => trusteeDateOfBirthRoute(ua, index, draftId)
    case TrusteeAUKCitizenPage(index)  => ua => trusteeAUKCitizenRoute(ua, index, draftId)
    case TrusteesNinoPage(index)  => _ => controllers.register.trustees.individual.routes.LiveInTheUKYesNoController.onPageLoad(index, draftId)
    case TrusteeAddressInTheUKPage(index)   => ua => trusteeLiveInTheUKRoute(ua, index, draftId)
    case TrusteesUkAddressPage(index) => _ => controllers.register.trustees.individual.routes.TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index)  => _ => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case TrusteesAnswerPage  => _ => controllers.register.trustees.routes.AddATrusteeController.onPageLoad(draftId)
    case AddATrusteePage  => addATrusteeRoute(draftId)
    case AddATrusteeYesNoPage  => addATrusteeYesNoRoute(draftId)
  }

  private def registrationTaskList(draftId: String): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def addATrusteeYesNoRoute(draftId: String)(answers: ReadableUserAnswers) : Call = {
    answers.get(AddATrusteeYesNoPage) match {
      case Some(true) =>
        controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(0, draftId)
      case Some(false) => registrationTaskList(draftId)
      case _ => sessionExpired
    }
  }

  private def addATrusteeRoute(draftId: String)(answers: ReadableUserAnswers) = {
    val addAnother = answers.get(AddATrusteePage)

    def routeToTrusteeIndex = {
      val trustees = answers.get(Trustees).getOrElse(List.empty)
      trustees match {
        case Nil =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(0, draftId)
        case t if t.nonEmpty =>
          controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(t.size, draftId)
      }
    }

    addAnother match {
      case Some(AddATrustee.YesNow) =>
        routeToTrusteeIndex
      case Some(AddATrustee.YesLater) => registrationTaskList(draftId)
      case Some(AddATrustee.NoComplete) => registrationTaskList(draftId)
      case _ => sessionExpired
    }
  }

  private def trusteeAUKCitizenRoute(answers: ReadableUserAnswers, index: Int, draftId: String) = answers.get(TrusteeAUKCitizenPage(index)) match {
    case Some(true)   => controllers.register.trustees.individual.routes.NinoController.onPageLoad(index, draftId)
    case Some(false)  => controllers.register.trustees.individual.routes.NinoYesNoController.onPageLoad(index, draftId)
    case None         => sessionExpired
  }

  private def trusteeLiveInTheUKRoute(answers: ReadableUserAnswers, index: Int, draftId: String) = answers.get(TrusteeAddressInTheUKPage(index)) match {
    case Some(true)   => controllers.register.trustees.individual.routes.UkAddressController.onPageLoad(index, draftId)
    case Some(false)  => controllers.register.trustees.individual.routes.LiveInTheUKYesNoController.onPageLoad(index, draftId)
    case None         => sessionExpired
  }

  private def trusteeDateOfBirthRoute(answers: ReadableUserAnswers, index : Int, draftId: String) = answers.get(IsThisLeadTrusteePage(index)) match {
    case Some(true) => controllers.register.trustees.individual.routes.NinoYesNoController.onPageLoad(index, draftId)
    case Some(false) => controllers.register.trustees.routes.TrusteesAnswerPageController.onPageLoad(index, draftId)
    case None => sessionExpired
  }

  private def trusteeIndividualOrBusinessRoute(answers: ReadableUserAnswers, index : Int, draftId: String) = {
    (answers.get(IsThisLeadTrusteePage(index)), answers.get(TrusteeIndividualOrBusinessPage(index))) match {
      case (Some(false), Some(IndividualOrBusiness.Individual)) => controllers.register.trustees.individual.routes.NameController.onPageLoad(index, draftId)
      case (Some(false), Some(IndividualOrBusiness.Business)) => controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, draftId)
      case (Some(true), Some(IndividualOrBusiness.Individual)) => controllers.register.trustees.individual.routes.NameController.onPageLoad(index, draftId)
      case (Some(true), Some(IndividualOrBusiness.Business)) => controllers.register.leadtrustee.organisation.routes.UkRegisteredYesNoController.onPageLoad(index, draftId)
      case _ => sessionExpired
    }
  }

  private def sessionExpired = {
    controllers.routes.SessionExpiredController.onPageLoad()
  }
}

