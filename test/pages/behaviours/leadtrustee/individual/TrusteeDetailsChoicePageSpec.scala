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

package pages.behaviours.leadtrustee.individual

import java.time.LocalDate

import models.UserAnswers
import models.core.pages.UKAddress
import models.registration.pages.DetailsChoice._
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.individual._

class TrusteeDetailsChoicePageSpec extends PageBehaviours {

  private val index = 0
  private val passportOrIdCardDetails: PassportOrIdCardDetails =
    PassportOrIdCardDetails("FR", "num", LocalDate.parse("2010-05-04"))

  "TrusteeDetailsChoice Page" must {

    "implement cleanup logic" when {

      "PASSPORT selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(IDCardDetailsPage(index), passportOrIdCardDetails).success.value
                .set(TrusteeDetailsChoicePage(index), Passport).success.value

            result.get(IDCardDetailsPage(index)) mustNot be(defined)
        }
      }

      "ID CARD selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(PassportDetailsPage(index), passportOrIdCardDetails).success.value
                .set(TrusteeDetailsChoicePage(index), IdCard).success.value

            result.get(PassportDetailsPage(index)) mustNot be(defined)
        }
      }
    }
  }

}
