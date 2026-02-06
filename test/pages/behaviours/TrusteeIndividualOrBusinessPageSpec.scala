/*
 * Copyright 2026 HM Revenue & Customs
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
import models.core.pages.IndividualOrBusiness
import models.core.pages.IndividualOrBusiness._
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.SetUserAnswers.SetAnswers
import pages.entitystatus.TrusteeStatus
import pages.register.TrusteeIndividualOrBusinessPage
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.trustees.{individual => tind, organisation => torg}

class TrusteeIndividualOrBusinessPageSpec extends PageBehaviours {

  private val index: Int = 0

  "TrusteeIndividualOrBusiness Page" must {

    beRetrievable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    beSettable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    beRemovable[IndividualOrBusiness](TrusteeIndividualOrBusinessPage(index))

    "implement cleanup logic" when {

      "INDIVIDUAL selected and value changed" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val initial: UserAnswers = userAnswers
            .set(TrusteeIndividualOrBusinessPage(index), Business)
            .success
            .value
            .setLeadBusinessAnswers(index)
            .setBusinessAnswers(index)
            .set(TrusteeStatus(index), Completed)
            .success
            .value

          val result: UserAnswers = initial
            .set(TrusteeIndividualOrBusinessPage(index), Individual)
            .success
            .value

          result.get(torg.NamePage(index)) mustNot be(defined)
          result.get(torg.UtrYesNoPage(index)) mustNot be(defined)
          result.get(torg.UtrPage(index)) mustNot be(defined)
          result.get(torg.mld5.CountryOfResidenceYesNoPage(index)) mustNot be(defined)
          result.get(torg.mld5.CountryOfResidenceInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(torg.mld5.CountryOfResidencePage(index)) mustNot be(defined)
          result.get(torg.AddressYesNoPage(index)) mustNot be(defined)
          result.get(torg.AddressUkYesNoPage(index)) mustNot be(defined)
          result.get(torg.UkAddressPage(index)) mustNot be(defined)
          result.get(torg.InternationalAddressPage(index)) mustNot be(defined)

          result.get(ltorg.UkRegisteredYesNoPage(index)) mustNot be(defined)
          result.get(ltorg.NamePage(index)) mustNot be(defined)
          result.get(ltorg.UtrPage(index)) mustNot be(defined)
          result.get(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(ltorg.mld5.CountryOfResidencePage(index)) mustNot be(defined)
          result.get(ltorg.UkAddressPage(index)) mustNot be(defined)
          result.get(ltorg.InternationalAddressPage(index)) mustNot be(defined)
          result.get(ltorg.EmailAddressYesNoPage(index)) mustNot be(defined)
          result.get(ltorg.EmailAddressPage(index)) mustNot be(defined)
          result.get(ltorg.TelephoneNumberPage(index)) mustNot be(defined)

          result.get(TrusteeStatus(index)) mustNot be(defined)
        }

      "BUSINESS selected and value changed" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val initial: UserAnswers = userAnswers
            .set(TrusteeIndividualOrBusinessPage(index), Individual)
            .success
            .value
            .setLeadIndividualAnswers(index)
            .setIndividualAnswers(index)
            .set(TrusteeStatus(index), Completed)
            .success
            .value

          val result: UserAnswers = initial
            .set(TrusteeIndividualOrBusinessPage(index), Business)
            .success
            .value

          result.get(tind.NamePage(index)) mustNot be(defined)
          result.get(tind.DateOfBirthYesNoPage(index)) mustNot be(defined)
          result.get(tind.DateOfBirthPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfNationalityYesNoPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfNationalityInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfNationalityPage(index)) mustNot be(defined)
          result.get(tind.NinoYesNoPage(index)) mustNot be(defined)
          result.get(tind.NinoPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfResidenceYesNoPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfResidenceInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(tind.mld5.CountryOfResidencePage(index)) mustNot be(defined)
          result.get(tind.AddressYesNoPage(index)) mustNot be(defined)
          result.get(tind.AddressUkYesNoPage(index)) mustNot be(defined)
          result.get(tind.UkAddressPage(index)) mustNot be(defined)
          result.get(tind.InternationalAddressPage(index)) mustNot be(defined)
          result.get(tind.PassportDetailsYesNoPage(index)) mustNot be(defined)
          result.get(tind.PassportDetailsPage(index)) mustNot be(defined)
          result.get(tind.IDCardDetailsYesNoPage(index)) mustNot be(defined)
          result.get(tind.IDCardDetailsPage(index)) mustNot be(defined)
          result.get(tind.mld5.MentalCapacityYesNoPage(index)) mustNot be(defined)

          result.get(ltind.TrusteesNamePage(index)) mustNot be(defined)
          result.get(ltind.TrusteesDateOfBirthPage(index)) mustNot be(defined)
          result.get(ltind.mld5.CountryOfNationalityInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(ltind.mld5.CountryOfNationalityPage(index)) mustNot be(defined)
          result.get(ltind.TrusteeNinoYesNoPage(index)) mustNot be(defined)
          result.get(ltind.TrusteesNinoPage(index)) mustNot be(defined)
          result.get(ltind.TrusteeDetailsChoicePage(index)) mustNot be(defined)
          result.get(ltind.PassportDetailsPage(index)) mustNot be(defined)
          result.get(ltind.IDCardDetailsPage(index)) mustNot be(defined)
          result.get(ltind.mld5.CountryOfResidenceInTheUkYesNoPage(index)) mustNot be(defined)
          result.get(ltind.mld5.CountryOfResidencePage(index)) mustNot be(defined)
          result.get(ltind.AddressUkYesNoPage(index)) mustNot be(defined)
          result.get(ltind.UkAddressPage(index)) mustNot be(defined)
          result.get(ltind.InternationalAddressPage(index)) mustNot be(defined)

          result.get(TrusteeStatus(index)) mustNot be(defined)
        }
    }

    "not implement cleanup logic" when {

      "BUSINESS selected and value unchanged" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val initial: UserAnswers = userAnswers
            .set(TrusteeIndividualOrBusinessPage(index), Business)
            .success
            .value
            .setLeadBusinessAnswers(index)
            .setBusinessAnswers(index)
            .set(TrusteeStatus(index), Completed)
            .success
            .value

          val result: UserAnswers = initial
            .set(TrusteeIndividualOrBusinessPage(index), Business)
            .success
            .value

          result.get(torg.NamePage(index))                                must be(defined)
          result.get(torg.UtrYesNoPage(index))                            must be(defined)
          result.get(torg.UtrPage(index))                                 must be(defined)
          result.get(torg.mld5.CountryOfResidenceYesNoPage(index))        must be(defined)
          result.get(torg.mld5.CountryOfResidenceInTheUkYesNoPage(index)) must be(defined)
          result.get(torg.mld5.CountryOfResidencePage(index))             must be(defined)
          result.get(torg.AddressYesNoPage(index))                        must be(defined)
          result.get(torg.AddressUkYesNoPage(index))                      must be(defined)
          result.get(torg.UkAddressPage(index))                           must be(defined)
          result.get(torg.InternationalAddressPage(index))                must be(defined)

          result.get(ltorg.UkRegisteredYesNoPage(index))                   must be(defined)
          result.get(ltorg.NamePage(index))                                must be(defined)
          result.get(ltorg.UtrPage(index))                                 must be(defined)
          result.get(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index)) must be(defined)
          result.get(ltorg.mld5.CountryOfResidencePage(index))             must be(defined)
          result.get(ltorg.AddressUkYesNoPage(index))                      must be(defined)
          result.get(ltorg.UkAddressPage(index))                           must be(defined)
          result.get(ltorg.InternationalAddressPage(index))                must be(defined)
          result.get(ltorg.EmailAddressYesNoPage(index))                   must be(defined)
          result.get(ltorg.EmailAddressPage(index))                        must be(defined)
          result.get(ltorg.TelephoneNumberPage(index))                     must be(defined)

          result.get(TrusteeStatus(index)) must be(defined)
        }

      "INDIVIDUAL selected and value unchanged" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val initial: UserAnswers = userAnswers
            .set(TrusteeIndividualOrBusinessPage(index), Individual)
            .success
            .value
            .setLeadIndividualAnswers(index)
            .setIndividualAnswers(index)
            .set(TrusteeStatus(index), Completed)
            .success
            .value

          val result: UserAnswers = initial
            .set(TrusteeIndividualOrBusinessPage(index), Individual)
            .success
            .value

          result.get(tind.NamePage(index))                                  must be(defined)
          result.get(tind.DateOfBirthYesNoPage(index))                      must be(defined)
          result.get(tind.DateOfBirthPage(index))                           must be(defined)
          result.get(tind.mld5.CountryOfNationalityYesNoPage(index))        must be(defined)
          result.get(tind.mld5.CountryOfNationalityInTheUkYesNoPage(index)) must be(defined)
          result.get(tind.mld5.CountryOfNationalityPage(index))             must be(defined)
          result.get(tind.NinoYesNoPage(index))                             must be(defined)
          result.get(tind.NinoPage(index))                                  must be(defined)
          result.get(tind.mld5.CountryOfResidenceYesNoPage(index))          must be(defined)
          result.get(tind.mld5.CountryOfResidenceInTheUkYesNoPage(index))   must be(defined)
          result.get(tind.mld5.CountryOfResidencePage(index))               must be(defined)
          result.get(tind.AddressYesNoPage(index))                          must be(defined)
          result.get(tind.AddressUkYesNoPage(index))                        must be(defined)
          result.get(tind.UkAddressPage(index))                             must be(defined)
          result.get(tind.InternationalAddressPage(index))                  must be(defined)
          result.get(tind.PassportDetailsYesNoPage(index))                  must be(defined)
          result.get(tind.PassportDetailsPage(index))                       must be(defined)
          result.get(tind.IDCardDetailsYesNoPage(index))                    must be(defined)
          result.get(tind.IDCardDetailsPage(index))                         must be(defined)
          result.get(tind.mld5.MentalCapacityYesNoPage(index))              must be(defined)

          result.get(ltind.TrusteesNamePage(index))                          must be(defined)
          result.get(ltind.TrusteesDateOfBirthPage(index))                   must be(defined)
          result.get(ltind.mld5.CountryOfNationalityInTheUkYesNoPage(index)) must be(defined)
          result.get(ltind.mld5.CountryOfNationalityPage(index))             must be(defined)
          result.get(ltind.TrusteeNinoYesNoPage(index))                      must be(defined)
          result.get(ltind.TrusteesNinoPage(index))                          must be(defined)
          result.get(ltind.MatchedYesNoPage(index))                          must be(defined)
          result.get(ltind.TrusteeDetailsChoicePage(index))                  must be(defined)
          result.get(ltind.PassportDetailsPage(index))                       must be(defined)
          result.get(ltind.IDCardDetailsPage(index))                         must be(defined)
          result.get(ltind.mld5.CountryOfResidenceInTheUkYesNoPage(index))   must be(defined)
          result.get(ltind.mld5.CountryOfResidencePage(index))               must be(defined)
          result.get(ltind.AddressUkYesNoPage(index))                        must be(defined)
          result.get(ltind.UkAddressPage(index))                             must be(defined)
          result.get(ltind.InternationalAddressPage(index))                  must be(defined)

          result.get(TrusteeStatus(index)) must be(defined)
        }
    }
  }

}
