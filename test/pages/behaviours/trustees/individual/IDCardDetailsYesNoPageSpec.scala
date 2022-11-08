/*
 * Copyright 2022 HM Revenue & Customs
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
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.individual.{IDCardDetailsPage, IDCardDetailsYesNoPage}

import java.time.LocalDate

class IDCardDetailsYesNoPageSpec extends PageBehaviours {

  private val index: Int = 0

  "IDCardDetailsYesNo Page" must {

    beRetrievable[Boolean](IDCardDetailsYesNoPage(index))

    beSettable[Boolean](IDCardDetailsYesNoPage(index))

    beRemovable[Boolean](IDCardDetailsYesNoPage(index))

    "implement cleanup logic" when {

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val initial: UserAnswers =
              userAnswers
                .set(IDCardDetailsYesNoPage(index), true).success.value
                .set(IDCardDetailsPage(index), PassportOrIdCardDetails("FR", "num", LocalDate.of(2010, 5, 4))).success.value

            val result = initial.set(IDCardDetailsYesNoPage(index), false).success.value

            result.get(IDCardDetailsPage(index)) mustNot be(defined)
        }
      }
    }
  }

}