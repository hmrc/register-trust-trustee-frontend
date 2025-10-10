/*
 * Copyright 2025 HM Revenue & Customs
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

package pages.behaviours.trustees.individual

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.individual._

class AddressUkYesNoPageSpec extends PageBehaviours {

  private val index: Int = 0

  "AddressUkYesNo Page" must {

    beRetrievable[Boolean](AddressUkYesNoPage(index))

    beSettable[Boolean](AddressUkYesNoPage(index))

    beRemovable[Boolean](AddressUkYesNoPage(index))

    "implement cleanup logic" when {

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val initial: UserAnswers =
              userAnswers
                .set(AddressUkYesNoPage(index), true).success.value
                .set(UkAddressPage(index), UKAddress("line1", "line2", None, None, "postcode")).success.value

            val result = initial.set(AddressUkYesNoPage(index), false).success.value

            result.get(UkAddressPage(index)) mustNot be(defined)
        }
      }

      "YES selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val initial: UserAnswers =
              userAnswers
                .set(AddressUkYesNoPage(index), false).success.value
                .set(InternationalAddressPage(index), InternationalAddress("line1", "line2", None, "FR")).success.value

            val result = initial.set(AddressUkYesNoPage(index), true).success.value

            result.get(InternationalAddressPage(index)) mustNot be(defined)
        }
      }
    }
  }

}
