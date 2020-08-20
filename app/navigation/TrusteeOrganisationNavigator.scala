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
import controllers.register.trustees.organisation.routes._
import models.ReadableUserAnswers
import pages.Page
import pages.register.trustees.organisation._
import play.api.mvc.Call

class TrusteeOrganisationNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers)
                       (implicit config: FrontendAppConfig): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case NamePage(index) => UtrYesNoController.onPageLoad(index, draftId)
    case UtrPage(index) => CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => CheckDetailsController.onPageLoad(index, draftId)
    case InternationalAddressPage(index) => CheckDetailsController.onPageLoad(index, draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UtrYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        UtrYesNoPage(index),
        UtrController.onPageLoad(index, draftId),
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
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      conditionalNavigation(draftId)
}
