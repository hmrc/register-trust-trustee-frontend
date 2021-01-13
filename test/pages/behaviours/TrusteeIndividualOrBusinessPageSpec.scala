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

package pages.behaviours

import models.Status.Completed
import models.UserAnswers
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{IndividualOrBusiness, InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.entitystatus.TrusteeStatus
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.{organisation => ltorg}
import pages.register.trustees.{organisation => torg}

class TrusteeIndividualOrBusinessPageSpec extends PageBehaviours {

  private val index: Int = 0
  private val name: String = "Name"
  private val utr: String = "utr"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val email: String = "email"
  private val tel: String = "tel"

  "TrusteeIndividualOrBusiness Page" must {

    beRetrievable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    beSettable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    beRemovable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    "implement cleanup logic" when {

      "INDIVIDUAL selected and value changed" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val initial: UserAnswers = userAnswers
              .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

              .set(torg.NamePage(index), name).success.value
              .set(torg.UtrYesNoPage(index), true).success.value
              .set(torg.UtrPage(index), utr).success.value
              .set(torg.AddressYesNoPage(index), true).success.value
              .set(torg.AddressUkYesNoPage(index), true).success.value
              .set(torg.UkAddressPage(index), ukAddress).success.value
              .set(torg.InternationalAddressPage(index), internationalAddress).success.value

              .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
              .set(ltorg.NamePage(index), name).success.value
              .set(ltorg.UtrPage(index), utr).success.value
              .set(ltorg.nonTaxable.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
              .set(ltorg.nonTaxable.CountryOfResidencePage(index), "FR").success.value
              .set(ltorg.AddressUkYesNoPage(index), true).success.value
              .set(ltorg.UkAddressPage(index), ukAddress).success.value
              .set(ltorg.InternationalAddressPage(index), internationalAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(index), true).success.value
              .set(ltorg.EmailAddressPage(index), email).success.value
              .set(ltorg.TelephoneNumberPage(index), tel).success.value

              .set(TrusteeStatus(index), Completed).success.value

            val result: UserAnswers = initial
              .set(TrusteeIndividualOrBusinessPage(index), Individual).success.value

            result.get(torg.NamePage(index)) mustNot be(defined)
            result.get(torg.UtrYesNoPage(index)) mustNot be(defined)
            result.get(torg.UtrPage(index)) mustNot be(defined)
            result.get(torg.AddressYesNoPage(index)) mustNot be(defined)
            result.get(torg.AddressUkYesNoPage(index)) mustNot be(defined)
            result.get(torg.UkAddressPage(index)) mustNot be(defined)
            result.get(torg.InternationalAddressPage(index)) mustNot be(defined)

            result.get(ltorg.UkRegisteredYesNoPage(index)) mustNot be(defined)
            result.get(ltorg.NamePage(index)) mustNot be(defined)
            result.get(ltorg.UtrPage(index)) mustNot be(defined)
            result.get(ltorg.nonTaxable.CountryOfResidenceInTheUkYesNoPage(index)) mustNot be(defined)
            result.get(ltorg.nonTaxable.CountryOfResidencePage(index)) mustNot be(defined)
            result.get(ltorg.UkAddressPage(index)) mustNot be(defined)
            result.get(ltorg.InternationalAddressPage(index)) mustNot be(defined)
            result.get(ltorg.EmailAddressYesNoPage(index)) mustNot be(defined)
            result.get(ltorg.EmailAddressPage(index)) mustNot be(defined)
            result.get(ltorg.TelephoneNumberPage(index)) mustNot be(defined)

            result.get(TrusteeStatus(index)) mustNot be(defined)
        }
      }
    }

    "don't implement cleanup logic" when {

      "BUSINESS selected and value unchanged" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val initial: UserAnswers = userAnswers
              .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

              .set(torg.NamePage(index), name).success.value
              .set(torg.UtrYesNoPage(index), true).success.value
              .set(torg.UtrPage(index), utr).success.value
              .set(torg.AddressYesNoPage(index), true).success.value
              .set(torg.AddressUkYesNoPage(index), true).success.value
              .set(torg.UkAddressPage(index), ukAddress).success.value
              .set(torg.InternationalAddressPage(index), internationalAddress).success.value

              .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
              .set(ltorg.NamePage(index), name).success.value
              .set(ltorg.UtrPage(index), utr).success.value
              .set(ltorg.nonTaxable.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
              .set(ltorg.nonTaxable.CountryOfResidencePage(index), "FR").success.value
              .set(ltorg.AddressUkYesNoPage(index), true).success.value
              .set(ltorg.UkAddressPage(index), ukAddress).success.value
              .set(ltorg.InternationalAddressPage(index), internationalAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(index), true).success.value
              .set(ltorg.EmailAddressPage(index), email).success.value
              .set(ltorg.TelephoneNumberPage(index), tel).success.value

              .set(TrusteeStatus(index), Completed).success.value

            val result: UserAnswers = initial
              .set(TrusteeIndividualOrBusinessPage(index), Business).success.value

            result.get(torg.NamePage(index)) must be(defined)
            result.get(torg.UtrYesNoPage(index)) must be(defined)
            result.get(torg.UtrPage(index)) must be(defined)
            result.get(torg.AddressYesNoPage(index)) must be(defined)
            result.get(torg.AddressUkYesNoPage(index)) must be(defined)
            result.get(torg.UkAddressPage(index)) must be(defined)
            result.get(torg.InternationalAddressPage(index)) must be(defined)

            result.get(ltorg.UkRegisteredYesNoPage(index)) must be(defined)
            result.get(ltorg.NamePage(index)) must be(defined)
            result.get(ltorg.UtrPage(index)) must be(defined)
            result.get(ltorg.nonTaxable.CountryOfResidenceInTheUkYesNoPage(index)) must be(defined)
            result.get(ltorg.nonTaxable.CountryOfResidencePage(index)) must be(defined)
            result.get(ltorg.AddressUkYesNoPage(index)) must be(defined)
            result.get(ltorg.UkAddressPage(index)) must be(defined)
            result.get(ltorg.InternationalAddressPage(index)) must be(defined)
            result.get(ltorg.EmailAddressYesNoPage(index)) must be(defined)
            result.get(ltorg.EmailAddressPage(index)) must be(defined)
            result.get(ltorg.TelephoneNumberPage(index)) must be(defined)

            result.get(TrusteeStatus(index)) must be(defined)
        }
      }
    }
  }

}
