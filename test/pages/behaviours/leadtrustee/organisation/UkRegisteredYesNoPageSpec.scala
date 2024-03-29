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

package pages.behaviours.leadtrustee.organisation

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.organisation.{UkRegisteredYesNoPage, UtrPage}

class UkRegisteredYesNoPageSpec extends PageBehaviours {

  val index = 0

  "UkRegisteredYesNo Page" must {

    beRetrievable[Boolean](UkRegisteredYesNoPage(index))

    beSettable[Boolean](UkRegisteredYesNoPage(index))

    beRemovable[Boolean](UkRegisteredYesNoPage(index))

    "implement cleanup logic" when {

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers =
              userAnswers
                .set(UtrPage(index), "utr").success.value
                .set(UkRegisteredYesNoPage(index), false).success.value

            result.get(UtrPage(index)) mustNot be(defined)
        }
      }
    }
  }

}
