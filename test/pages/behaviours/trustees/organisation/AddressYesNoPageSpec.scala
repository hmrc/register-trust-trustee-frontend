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

package pages.behaviours.trustees.organisation

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.organisation._

class AddressYesNoPageSpec extends PageBehaviours {

  private val index: Int = 0

  "AddressYesNo Page" must {

    beRetrievable[Boolean](AddressYesNoPage(index))

    beSettable[Boolean](AddressYesNoPage(index))

    beRemovable[Boolean](AddressYesNoPage(index))

    "implement cleanup logic" when {

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(AddressUkYesNoPage(index), true).success.value
                .set(UkAddressPage(index), UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).success.value
                .set(InternationalAddressPage(index), InternationalAddress("Line 1", "Line 2", None, "COUNTRY")).success.value
                .set(AddressYesNoPage(index), false).success.value

            result.get(AddressUkYesNoPage(index)) mustNot be(defined)
            result.get(UkAddressPage(index)) mustNot be(defined)
            result.get(InternationalAddressPage(index)) mustNot be(defined)
        }
      }
    }
  }

}
