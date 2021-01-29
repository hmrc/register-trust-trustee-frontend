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

package navigation

import config.FrontendAppConfig
import controllers.register.leadtrustee.individual.{routes => rts}
import controllers.register.leadtrustee.individual.mld5.{routes => mld5Rts}
import models.ReadableUserAnswers
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import pages.Page
import pages.register.leadtrustee.individual._
import pages.register.leadtrustee.individual.mld5._
import play.api.mvc.Call

class LeadTrusteeIndividualNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteesNamePage(index) => _ => rts.DateOfBirthController.onPageLoad(index, draftId)
    case TrusteesDateOfBirthPage(index) => ua => navigateAwayFromDateOfBirthPage(draftId, index, ua.is5mldEnabled)
    case CountryOfNationalityPage(index) => _ => rts.NinoYesNoController.onPageLoad(index, draftId)
    case TrusteesNinoPage(index) => ua => navigateAwayFromNinoOrIdPages(draftId, index, ua.is5mldEnabled)
    case CountryOfResidencePage(index) => _ => rts.InternationalAddressController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => ua => navigateAwayFromNinoOrIdPages(draftId, index, ua.is5mldEnabled)
    case IDCardDetailsPage(index) => ua => navigateAwayFromNinoOrIdPages(draftId, index, ua.is5mldEnabled)
    case InternationalAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index) => _ => rts.TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ TrusteeNinoYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.NinoController.onPageLoad(index, draftId),
        noCall = rts.TrusteeDetailsChoiceController.onPageLoad(index, draftId)
      )
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.UkAddressController.onPageLoad(index, draftId),
        noCall = rts.InternationalAddressController.onPageLoad(index, draftId)
      )
    case TrusteeDetailsChoicePage(index) => ua =>
      detailsRoutes(ua, index, draftId)
    case page @ EmailAddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.EmailAddressController.onPageLoad(index, draftId),
        noCall = rts.TelephoneNumberController.onPageLoad(index, draftId)
      )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.NinoYesNoController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.UkAddressController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def detailsRoutes(answers: ReadableUserAnswers, index: Int, draftId: String): Call = {
    answers.get(TrusteeDetailsChoicePage(index)) match {
      case Some(IdCard) => rts.IDCardDetailsController.onPageLoad(index, draftId)
      case Some(Passport) => rts.PassportDetailsController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def navigateAwayFromDateOfBirthPage(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId)
    } else {
      rts.NinoYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoOrIdPages(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    } else {
      rts.LiveInTheUKYesNoController.onPageLoad(index, draftId)
    }
  }
}
