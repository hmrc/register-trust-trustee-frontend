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

package pages.behaviours.leadtrustee.individual

import java.time.LocalDate

import models.UserAnswers
import models.registration.pages.DetailsChoice.Passport
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.individual._

class TrusteeNinoYesNoPageSpec extends PageBehaviours {

  private val index: Int = 0
  private val passportOrIdCardDetails: PassportOrIdCardDetails =
    PassportOrIdCardDetails("FR", "num", LocalDate.parse("2010-05-04"))

  "TrusteeNinoYesNo Page" must {

    beRetrievable[Boolean](TrusteeNinoYesNoPage(index))

    beSettable[Boolean](TrusteeNinoYesNoPage(index))

    beRemovable[Boolean](TrusteeNinoYesNoPage(index))

    "implement cleanup logic" when {

      "YES selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val initial: UserAnswers =
              userAnswers
                .set(TrusteeNinoYesNoPage(index), false).success.value
                .set(TrusteeDetailsChoicePage(index), Passport).success.value
                .set(PassportDetailsPage(index), passportOrIdCardDetails).success.value
                .set(IDCardDetailsPage(index), passportOrIdCardDetails).success.value

            val result = initial.set(TrusteeNinoYesNoPage(index), true).success.value

            result.get(TrusteeDetailsChoicePage(index)) mustNot be(defined)
            result.get(PassportDetailsPage(index)) mustNot be(defined)
            result.get(IDCardDetailsPage(index)) mustNot be(defined)
        }
      }

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val initial: UserAnswers =
              userAnswers
                .set(TrusteeNinoYesNoPage(index), true).success.value
                .set(TrusteesNinoPage(index), "nino").success.value

            val result = initial.set(TrusteeNinoYesNoPage(index), false).success.value

            result.get(TrusteesNinoPage(index)) mustNot be(defined)
        }
      }
    }
  }

}