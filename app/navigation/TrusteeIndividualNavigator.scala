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
import controllers.register.trustees.individual.routes._
import controllers.register.trustees.individual.mld5.{routes => mld5Rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.trustees.individual._
import pages.register.trustees.individual.mld5._
import play.api.mvc.Call

class TrusteeIndividualNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => ua => navigateAwayFromDateOfBirthPage(index, draftId, ua.is5mldEnabled)
    case CountryOfNationalityPage(index) => _ => NinoYesNoController.onPageLoad(index, draftId)
    case NinoPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = DateOfBirthController.onPageLoad(index, draftId),
        noCall = navigateAwayFromDateOfBirthPage(index, draftId, ua.is5mldEnabled)
      )
    case page @ CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = NinoYesNoController.onPageLoad(index, draftId)
      )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = NinoYesNoController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page @ NinoYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = NinoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromNinoPage(index, draftId, ua.is5mldEnabled)
      )
    case page @ CountryOfResidencyYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5Rts.CountryOfResidencyInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = AddressYesNoController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidencyInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressYesNoController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfResidencyController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = CheckDetailsController.onPageLoad(index, draftId)
      )
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = UkAddressController.onPageLoad(index, draftId),
        noCall = InternationalAddressController.onPageLoad(index, draftId)
      )
    case page @ PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = PassportDetailsController.onPageLoad(index, draftId),
        noCall = IDCardDetailsYesNoController.onPageLoad(index, draftId)
      )
    case page @ IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = IDCardDetailsController.onPageLoad(index, draftId),
        noCall = CheckDetailsController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromDateOfBirthPage(index: Int, draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5Rts.CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    } else {
      NinoYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoPage(index: Int, draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5Rts.CountryOfResidencyYesNoController.onPageLoad(index, draftId)
    } else {
      AddressYesNoController.onPageLoad(index, draftId)
    }
  }
}
