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
import controllers.register.leadtrustee.organisation.{routes => rts}
import controllers.register.leadtrustee.organisation.nonTaxable.{routes => mld5Rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.leadtrustee.organisation._
import pages.register.leadtrustee.organisation.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}
import play.api.mvc.Call

class LeadTrusteeOrganisationNavigator extends Navigator {

  override def simpleNavigation(draftId: String, fiveMldEnabled: Boolean): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UkRegisteredYesNoPage(index) => _ => rts.NameController.onPageLoad(index, draftId)
    case NamePage(index) => ua => nameRoute(ua, index, draftId, fiveMldEnabled)
    case UtrPage(index) => _ => fiveMldYesNo(draftId, index, fiveMldEnabled)
    case CountryOfResidencePage(index) => _ => rts.InternationalAddressController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ => rts.EmailAddressYesNoController.onPageLoad(index, draftId)
    case EmailAddressPage(index) => _ => rts.TelephoneNumberController.onPageLoad(index, draftId)
    case TelephoneNumberPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String, fiveMldEnabled: Boolean)
                                    (implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage(index),
        rts.UkAddressController.onPageLoad(index, draftId),
        rts.InternationalAddressController.onPageLoad(index, draftId)
      )
    case EmailAddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        EmailAddressYesNoPage(index),
        rts.EmailAddressController.onPageLoad(index, draftId),
        rts.TelephoneNumberController.onPageLoad(index, draftId)
      )
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        CountryOfResidenceInTheUkYesNoPage(index),
        rts.UkAddressController.onPageLoad(index, draftId),
        mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
      )
  }

  private def nameRoute(ua: ReadableUserAnswers, index: Int, draftId: String, fiveMldEnabled: Boolean): Call = {
    ua.get(UkRegisteredYesNoPage(index)) match {
      case Some(true) => rts.UtrController.onPageLoad(index, draftId)
      case Some(false) => fiveMldYesNo(draftId, index, fiveMldEnabled)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def fiveMldYesNo(draftId: String, index: Int, fiveMld: Boolean): Call = {
    if (fiveMld) {
      mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId)
    } else {
      rts.AddressUkYesNoController.onPageLoad(index, draftId)
    }
  }

}
