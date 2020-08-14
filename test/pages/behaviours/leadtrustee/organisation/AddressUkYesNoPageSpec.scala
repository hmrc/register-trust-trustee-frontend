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

package pages.behaviours.leadtrustee.organisation

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.organisation._

class AddressUkYesNoPageSpec extends PageBehaviours {

  "AddressUkYesNo Page" must {

    beRetrievable[Boolean](AddressUkYesNoPage)

    beSettable[Boolean](AddressUkYesNoPage)

    beRemovable[Boolean](AddressUkYesNoPage)

    "implement cleanup logic" when {

      "YES selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(InternationalAddressPage, InternationalAddress("Line 1", "Line 2", None, "COUNTRY")).success.value
                .set(AddressUkYesNoPage, true).success.value

            result.get(InternationalAddressPage) mustNot be(defined)
        }
      }

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(UkAddressPage, UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).success.value
                .set(AddressUkYesNoPage, false).success.value

            result.get(UkAddressPage) mustNot be(defined)
        }
      }
    }
  }

}
