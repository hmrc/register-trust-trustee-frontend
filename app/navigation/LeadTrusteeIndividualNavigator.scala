/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.register.leadtrustee.individual.mld5.{routes => mld5Rts}
import controllers.register.leadtrustee.individual.{routes => rts}
import models.ReadableUserAnswers
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import pages.Page
import pages.register.leadtrustee.individual._
import pages.register.leadtrustee.individual.mld5._
import play.api.mvc.Call

class LeadTrusteeIndividualNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case TrusteesNamePage(index)         => _ => rts.DateOfBirthController.onPageLoad(index, draftId)
    case TrusteesDateOfBirthPage(index)  =>
      _ => mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId)
    case CountryOfNationalityPage(index) => _ => rts.NinoYesNoController.onPageLoad(index, draftId)
    case TrusteesNinoPage(index)         => _ => mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index)   => _ => rts.InternationalAddressController.onPageLoad(index, draftId)
    case PassportDetailsPage(index)      => _ => mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index)        => _ => mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case UkAddressPage(index)            => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index)         => _ => rts.TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index)      => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(
    draftId: String
  )(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ TrusteeNinoYesNoPage(index)                 =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.NinoController.onPageLoad(index, draftId),
          noCall = rts.TrusteeDetailsChoiceController.onPageLoad(index, draftId)
        )
    case page @ AddressUkYesNoPage(index)                   =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.UkAddressController.onPageLoad(index, draftId),
          noCall = rts.InternationalAddressController.onPageLoad(index, draftId)
        )
    case TrusteeDetailsChoicePage(index)                    => ua => detailsRoutes(ua, index, draftId)
    case page @ EmailAddressYesNoPage(index)                =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.EmailAddressController.onPageLoad(index, draftId),
          noCall = rts.TelephoneNumberController.onPageLoad(index, draftId)
        )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.NinoYesNoController.onPageLoad(index, draftId),
          noCall = mld5Rts.CountryOfNationalityController.onPageLoad(index, draftId)
        )
    case page @ CountryOfResidenceInTheUkYesNoPage(index)   =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.UkAddressController.onPageLoad(index, draftId),
          noCall = mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
        )
  }

  private def detailsRoutes(answers: ReadableUserAnswers, index: Int, draftId: String): Call =
    answers.get(TrusteeDetailsChoicePage(index)) match {
      case Some(IdCard)   => rts.IDCardDetailsController.onPageLoad(index, draftId)
      case Some(Passport) => rts.PassportDetailsController.onPageLoad(index, draftId)
      case _              => controllers.routes.SessionExpiredController.onPageLoad
    }

}
