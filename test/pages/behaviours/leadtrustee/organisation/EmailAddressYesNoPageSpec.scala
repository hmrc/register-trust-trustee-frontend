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

package pages.behaviours.leadtrustee.organisation

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.leadtrustee.organisation._

class EmailAddressYesNoPageSpec extends PageBehaviours {

  val index = 0

  "EmailAddressYesNo Page" must {

    beRetrievable[Boolean](EmailAddressYesNoPage(index))

    beSettable[Boolean](EmailAddressYesNoPage(index))

    beRemovable[Boolean](EmailAddressYesNoPage(index))

    "implement cleanup logic" when {

      "NO selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers =
            userAnswers
              .set(EmailAddressPage(index), "email")
              .success
              .value
              .set(EmailAddressYesNoPage(index), false)
              .success
              .value

          result.get(EmailAddressPage(index)) mustNot be(defined)
        }
    }
  }

}
