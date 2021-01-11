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

package pages.behaviours.trustees.individual

import java.time.LocalDate

import models.UserAnswers
import models.core.pages.UKAddress
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.individual._

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

            val initial: UserAnswers =
              userAnswers
                .set(AddressYesNoPage(index), true).success.value
                .set(AddressUkYesNoPage(index), true).success.value
                .set(UkAddressPage(index), UKAddress("line1", "line2", None, None, "postcode")).success.value
                .set(PassportDetailsYesNoPage(index), true).success.value
                .set(PassportDetailsPage(index), PassportOrIdCardDetails("FR", "num", LocalDate.of(2010, 5, 4))).success.value
                .set(IDCardDetailsYesNoPage(index), true).success.value
                .set(IDCardDetailsPage(index), PassportOrIdCardDetails("FR", "num", LocalDate.of(2010, 5, 4))).success.value

            val result = initial.set(AddressYesNoPage(index), false).success.value

            result.get(AddressUkYesNoPage(index)) mustNot be(defined)
            result.get(UkAddressPage(index)) mustNot be(defined)
            result.get(PassportDetailsYesNoPage(index)) mustNot be(defined)
            result.get(PassportDetailsPage(index)) mustNot be(defined)
            result.get(IDCardDetailsYesNoPage(index)) mustNot be(defined)
            result.get(IDCardDetailsPage(index)) mustNot be(defined)
        }
      }
    }
  }

}