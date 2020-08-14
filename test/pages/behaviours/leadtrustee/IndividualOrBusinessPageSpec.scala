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

package pages.behaviours.leadtrustee

import models.UserAnswers
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.IndividualOrBusinessPage
import pages.register.leadtrustee.organisation._

class IndividualOrBusinessPageSpec extends PageBehaviours {

  "IndividualOrBusiness Page" must {

    "implement cleanup logic" when {

      "INDIVIDUAL selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(UkRegisteredYesNoPage, true).success.value
                .set(NamePage, "name").success.value
                .set(UtrPage, "utr").success.value
                .set(AddressUkYesNoPage, true).success.value
                .set(UkAddressPage, UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).success.value
                .set(InternationalAddressPage, InternationalAddress("Line 1", "Line 2", None, "COUNTRY")).success.value
                .set(EmailAddressYesNoPage, true).success.value
                .set(EmailAddressPage, "email").success.value
                .set(TelephoneNumberPage, "tel").success.value
                .set(IndividualOrBusinessPage, Individual).success.value

            result.get(UkRegisteredYesNoPage) mustNot be(defined)
            result.get(NamePage) mustNot be(defined)
            result.get(UtrPage) mustNot be(defined)
            result.get(AddressUkYesNoPage) mustNot be(defined)
            result.get(UkAddressPage) mustNot be(defined)
            result.get(InternationalAddressPage) mustNot be(defined)
            result.get(EmailAddressYesNoPage) mustNot be(defined)
            result.get(EmailAddressPage) mustNot be(defined)
            result.get(TelephoneNumberPage) mustNot be(defined)
        }
      }
    }
  }

}
