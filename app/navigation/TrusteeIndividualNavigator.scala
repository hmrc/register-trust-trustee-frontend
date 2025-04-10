/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.register.trustees.individual.mld5.{routes => mld5Rts}
import controllers.register.trustees.individual.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.trustees.individual._
import pages.register.trustees.individual.mld5._
import play.api.mvc.Call

class TrusteeIndividualNavigator extends Navigator {
  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => _ => mld5Rts.CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    case CountryOfNationalityPage(index) => ua => navigateAwayFromNationalityPages(index, draftId, ua)
    case CountryOfResidencePage(index) => ua => navigateAwayFromResidencePages(index, draftId, ua)
    case NinoPage(index) => ua => navigateAwayFromNinoPage(index, draftId, ua)
    case UkAddressPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page@DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = DateOfBirthController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfNationalityYesNoController.onPageLoad(index, draftId)
      )
    case page@CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromNationalityPages(index, draftId, ua)
      )
    case page@CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromNationalityPages(index, draftId, ua),
        noCall = mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page@NinoYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = NinoController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
      )
    case page@CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromResidencePages(index, draftId, ua)
      )
    case page@CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromResidencePages(index, draftId, ua),
        noCall = mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
    case page@AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
      )
    case page@AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = UkAddressController.onPageLoad(index, draftId),
        noCall = InternationalAddressController.onPageLoad(index, draftId)
      )
    case page@PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = PassportDetailsController.onPageLoad(index, draftId),
        noCall = IDCardDetailsYesNoController.onPageLoad(index, draftId)
      )
    case page@IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = IDCardDetailsController.onPageLoad(index, draftId),
        noCall = mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
      )
    case _@MentalCapacityYesNoPage(index) => _ =>
      CheckDetailsController.onPageLoad(index, draftId)
  }

  private def navigateAwayFromNationalityPages(index: Int, draftId: String, answers: ReadableUserAnswers): Call = {
    if (answers.isTaxable) {
      NinoYesNoController.onPageLoad(index, draftId)
    } else {
      mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoPage(index: Int, draftId: String, answers: ReadableUserAnswers): Call = {
    if (answers.isTaxable) {
      mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromResidencePages(index: Int, draftId: String, answers: ReadableUserAnswers): Call = {
    (answers.isTaxable, answers.get(NinoYesNoPage(index))) match {
      case (true, Some(false)) => AddressYesNoController.onPageLoad(index, draftId)
      case _ => mld5Rts.MentalCapacityYesNoController.onPageLoad(index, draftId)
    }
  }

}
