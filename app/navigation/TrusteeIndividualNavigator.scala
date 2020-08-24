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
import controllers.register.trustees.individual.routes._
import controllers.register.trustees.routes._
import models.ReadableUserAnswers
import models.core.pages.IndividualOrBusiness
import models.registration.pages.AddATrustee
import pages.Page
import pages.register.trustees.individual._
import pages.register.trustees._
import play.api.mvc.Call
import sections.Trustees

class TrusteeIndividualNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers)
                       (implicit config: FrontendAppConfig): Call = routes(draftId)(config)(page)(userAnswers)

  private def registrationTaskList(draftId: String)(implicit config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def simpleNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, Call] = {
    case IsThisLeadTrusteePage(index) => TrusteeIndividualOrBusinessController.onPageLoad(index, draftId)

    case NamePage(index) => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => NinoYesNoController.onPageLoad(index, draftId)
    case NinoPage(index) => AddressYesNoController.onPageLoad(index, draftId)
    case UkAddressPage(index) =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => CheckDetailsController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => CheckDetailsController.onPageLoad(index, draftId)

    case TrusteesAnswerPage  => AddATrusteeController.onPageLoad(draftId)
  }

  private def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteeIndividualOrBusinessPage(index) => ua => {
      (ua.get(IsThisLeadTrusteePage(index)), ua.get(TrusteeIndividualOrBusinessPage(index))) match {
        case (Some(false), Some(IndividualOrBusiness.Individual)) => controllers.register.trustees.individual.routes.NameController.onPageLoad(index, draftId)
        case (Some(true), Some(IndividualOrBusiness.Individual)) => controllers.register.leadtrustee.individual.routes.NameController.onPageLoad(index, draftId)
        case (Some(false), Some(IndividualOrBusiness.Business)) => controllers.register.trustees.organisation.routes.NameController.onPageLoad(index, draftId)
        case (Some(true), Some(IndividualOrBusiness.Business)) => controllers.register.leadtrustee.organisation.routes.UkRegisteredYesNoController.onPageLoad(index, draftId)
        case _ => controllers.routes.SessionExpiredController.onPageLoad()
      }
    }
    case AddATrusteeYesNoPage => ua => {
      yesNoNav(
        ua,
        AddATrusteeYesNoPage,
        IsThisLeadTrusteeController.onPageLoad(0, draftId),
        registrationTaskList(draftId)
      )
    }
    case AddATrusteePage => ua => {
      val addAnother = ua.get(AddATrusteePage)
      def routeToTrusteeIndex = {
        val trustees = ua.get(Trustees).getOrElse(List.empty)
        trustees match {
          case Nil =>
            IsThisLeadTrusteeController.onPageLoad(0, draftId)
          case t if t.nonEmpty =>
            IsThisLeadTrusteeController.onPageLoad(t.size, draftId)
        }
      }
      addAnother match {
        case Some(AddATrustee.YesNow) =>
          routeToTrusteeIndex
        case Some(AddATrustee.YesLater) => registrationTaskList(draftId)
        case Some(AddATrustee.NoComplete) => registrationTaskList(draftId)
        case _ => controllers.routes.SessionExpiredController.onPageLoad()
      }
    }
    case DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        DateOfBirthYesNoPage(index),
        DateOfBirthController.onPageLoad(index, draftId),
        NinoYesNoController.onPageLoad(index, draftId)
      )
    case NinoYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        NinoYesNoPage(index),
        NinoController.onPageLoad(index, draftId),
        AddressYesNoController.onPageLoad(index, draftId)
      )
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        AddressUkYesNoController.onPageLoad(index, draftId),
        CheckDetailsController.onPageLoad(index, draftId)
      )
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage(index),
        UkAddressController.onPageLoad(index, draftId),
        InternationalAddressController.onPageLoad(index, draftId)
      )
    case PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        PassportDetailsYesNoPage(index),
        PassportDetailsController.onPageLoad(index, draftId),
        IDCardDetailsYesNoController.onPageLoad(index, draftId)
      )
    case IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        IDCardDetailsYesNoPage(index),
        IDCardDetailsController.onPageLoad(index, draftId),
        CheckDetailsController.onPageLoad(index, draftId)
      )
  }

  private def routes(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      conditionalNavigation(draftId)
}
