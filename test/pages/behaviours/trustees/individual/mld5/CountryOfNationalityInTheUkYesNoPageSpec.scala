/*
 * Copyright 2024 HM Revenue & Customs
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

package pages.behaviours.trustees.individual.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.trustees.individual.mld5.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage}
import utils.Constants._

class CountryOfNationalityInTheUkYesNoPageSpec extends PageBehaviours {

  "CountryOfNationalityInTheUkYesNoPage" must {

    beRetrievable[Boolean](CountryOfNationalityInTheUkYesNoPage(0))

    beSettable[Boolean](CountryOfNationalityInTheUkYesNoPage(0))

    beRemovable[Boolean](CountryOfNationalityInTheUkYesNoPage(0))


    "Yes selected - set CountryOfNationalityPage to 'GB' " in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial.set(CountryOfNationalityYesNoPage(0), true).success.value
            .set(CountryOfNationalityPage(0), "ES").success.value

          val result = answers.set(CountryOfNationalityInTheUkYesNoPage(0), true).success.value

          result.get(CountryOfNationalityPage(0)).get mustBe GB
      }
    }

    "No selected" in {
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers = initial.set(CountryOfNationalityYesNoPage(0), true).success.value
            .set(CountryOfNationalityPage(0), "ES").success.value

          val result = answers.set(CountryOfNationalityInTheUkYesNoPage(0), false).success.value

          result.get(CountryOfNationalityPage(0)).get mustBe "ES"
      }
    }

  }
}
