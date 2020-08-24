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
import models.ReadableUserAnswers
import pages.Page
import pages.register.trustees.individual._
import play.api.mvc.Call

class TrusteeIndividualNavigator extends Navigator {

  override def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => _ => NinoYesNoController.onPageLoad(index, draftId)
    case NinoPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => _ =>  PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
  }

  override def conditionalNavigation(draftId: String)(implicit config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(ua, DateOfBirthYesNoPage(index), DateOfBirthController.onPageLoad(index, draftId), NinoYesNoController.onPageLoad(index, draftId))
    case NinoYesNoPage(index) => ua =>
      yesNoNav(ua, NinoYesNoPage(index), NinoController.onPageLoad(index, draftId), AddressYesNoController.onPageLoad(index, draftId))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(ua, AddressYesNoPage(index), AddressUkYesNoController.onPageLoad(index, draftId), CheckDetailsController.onPageLoad(index, draftId))
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(ua, AddressUkYesNoPage(index), UkAddressController.onPageLoad(index, draftId), InternationalAddressController.onPageLoad(index, draftId))
    case PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(ua, PassportDetailsYesNoPage(index), PassportDetailsController.onPageLoad(index, draftId), IDCardDetailsYesNoController.onPageLoad(index, draftId))
    case IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(ua, IDCardDetailsYesNoPage(index), IDCardDetailsController.onPageLoad(index, draftId), CheckDetailsController.onPageLoad(index, draftId))
  }
}
