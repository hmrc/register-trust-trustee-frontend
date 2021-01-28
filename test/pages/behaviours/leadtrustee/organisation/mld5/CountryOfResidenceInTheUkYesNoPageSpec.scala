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

package pages.behaviours.leadtrustee.organisation.mld5

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.organisation.{InternationalAddressPage, UkAddressPage}
import pages.register.leadtrustee.organisation.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}

class CountryOfResidenceInTheUkYesNoPageSpec extends PageBehaviours {

  "CountryOfResidenceInTheUkYesNoPage" must {

    beRetrievable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

    beSettable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

    beRemovable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

    "Yes selected - set CountryOfResidencePage to 'GB' and remove international address" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial
            .set(CountryOfResidencePage(0), "ES").success.value
            .set(InternationalAddressPage(0), InternationalAddress("Line 1", "Line 2", None, "ES")).success.value

          val result = answers.set(CountryOfResidenceInTheUkYesNoPage(0), true).success.value

          result.get(CountryOfResidencePage(0)).get mustBe "GB"
          result.get(InternationalAddressPage(0)) mustNot be(defined)
      }
    }

    "No selected -  remove Uk address" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial
            .set(CountryOfResidencePage(0), "ES").success.value
            .set(UkAddressPage(0), UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).success.value

          val result = answers.set(CountryOfResidenceInTheUkYesNoPage(0), false).success.value

          result.get(CountryOfResidencePage(0)).get mustBe "ES"
          result.get(UkAddressPage(0)) mustNot be(defined)
      }
    }

  }
}
