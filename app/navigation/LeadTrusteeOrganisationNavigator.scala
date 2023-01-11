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

package navigation

import config.FrontendAppConfig
import controllers.register.leadtrustee.organisation.{routes => rts}
import controllers.register.leadtrustee.organisation.mld5.{routes => mld5Rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.leadtrustee.organisation._
import pages.register.leadtrustee.organisation.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}
import play.api.mvc.Call

class LeadTrusteeOrganisationNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UkRegisteredYesNoPage(index) => _ => rts.NameController.onPageLoad(index, draftId)
    case NamePage(index) => ua => navigateAwayFromNameQuestion(ua, index, draftId)
    case UtrPage(index) => _ => mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index) => _ => rts.InternationalAddressController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index) => _ => rts.TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.UkAddressController.onPageLoad(index, draftId),
        noCall = rts.InternationalAddressController.onPageLoad(index, draftId)
      )
    case page @ EmailAddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.EmailAddressController.onPageLoad(index, draftId),
        noCall = rts.TelephoneNumberController.onPageLoad(index, draftId)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = rts.UkAddressController.onPageLoad(index, draftId),
        noCall = mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def navigateAwayFromNameQuestion(ua: ReadableUserAnswers, index: Int, draftId: String): Call = {
    ua.get(UkRegisteredYesNoPage(index)) match {
      case Some(true) => rts.UtrController.onPageLoad(index, draftId)
      case Some(false) => mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad
    }
  }

}
