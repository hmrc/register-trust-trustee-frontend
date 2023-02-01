/*
 * Copyright 2023 HM Revenue & Customs
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

package navigation

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.leadtrustee.individual.mld5.{routes => mld5Rts}
import controllers.register.leadtrustee.individual.{routes => rts}
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import models.registration.pages.PassportOrIdCardDetails
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.leadtrustee.individual._
import pages.register.leadtrustee.individual.mld5._

import java.time.LocalDate

class LeadTrusteeIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new LeadTrusteeIndividualNavigator
  val index = 0
  implicit val config: FrontendAppConfig = frontendAppConfig

  "LeadTrusteeIndividual Navigator" must {

    "name page -> Name -> DOB page" in {
      val answers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("First", None, "Last")).success.value

      navigator.nextPage(TrusteesNamePage(index), fakeDraftId, answers)
        .mustBe(rts.DateOfBirthController.onPageLoad(index, fakeDraftId))
    }

    "Date Of Birth page -> Country Of Nationality UK yes no page" in {
      navigator.nextPage(TrusteesDateOfBirthPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Nationality UK yes no page -> No -> Country of Nationality page" in {
      val answers = emptyUserAnswers
        .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

      navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(mld5Rts.CountryOfNationalityController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Nationality UK yes no page -> Yes -> Do You Know NINO page" in {
      val answers = emptyUserAnswers
        .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

      navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.NinoYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Nationality page -> Do You Know NINO page" in {
      navigator.nextPage(CountryOfNationalityPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(rts.NinoYesNoController.onPageLoad(index, fakeDraftId))
    }

    "NinoYesNo -> Yes -> Nino page" in {
      val answers = emptyUserAnswers
        .set(TrusteeNinoYesNoPage(index), true).success.value

      navigator.nextPage(TrusteeNinoYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.NinoController.onPageLoad(index, fakeDraftId))
    }

    "NINO page -> Country Of Residence UK yes no page" in {
      navigator.nextPage(TrusteesNinoPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence UK yes no page -> No -> Country of Residence page" in {
      val answers = emptyUserAnswers
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

      navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence UK yes no page -> Yes -> UK Address page" in {
      val answers = emptyUserAnswers
        .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

      navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence page -> International Address page" in {
      navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(rts.InternationalAddressController.onPageLoad(index, fakeDraftId))
    }

    "NinoYesNo -> No -> Details page" in {
      val answers = emptyUserAnswers
        .set(TrusteeNinoYesNoPage(index), false).success.value

      navigator.nextPage(TrusteeNinoYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId))
    }

    "TrusteeDetailsChoice Page -> IdCard -> IDCard page" in {
      val answers = emptyUserAnswers
        .set(TrusteeDetailsChoicePage(index), IdCard).success.value

      navigator.nextPage(TrusteeDetailsChoicePage(index), fakeDraftId, answers)
        .mustBe(rts.IDCardDetailsController.onPageLoad(index, fakeDraftId))
    }

    "TrusteeDetailsChoice Page -> Passport -> Passport page" in {
      val answers = emptyUserAnswers
        .set(TrusteeDetailsChoicePage(index), Passport).success.value

      navigator.nextPage(TrusteeDetailsChoicePage(index), fakeDraftId, answers)
        .mustBe(rts.PassportDetailsController.onPageLoad(index, fakeDraftId))
    }

    "IDCard Page -> IDCard -> Country Of Residence UK yes no Page" in {
      val answers = emptyUserAnswers
        .set(IDCardDetailsPage(index), PassportOrIdCardDetails("UK", "0987654321234", LocalDate.now())).success.value

      navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, answers)
        .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Passport Page -> Passport -> Country Of Residence UK yes no Page" in {
      val answers = emptyUserAnswers
        .set(PassportDetailsPage(index), PassportOrIdCardDetails("UK", "0987654321234", LocalDate.now())).success.value

      navigator.nextPage(PassportDetailsPage(index), fakeDraftId, answers)
        .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "AUKCitizen Page -> Yes -> UkAddress Page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), true).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
    }


    "AUKCitizen Page -> No -> InternationalAddress Page" in {
      val answers = emptyUserAnswers
        .set(AddressUkYesNoPage(index), false).success.value

      navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.InternationalAddressController.onPageLoad(index, fakeDraftId))
    }

    "UkAddress Page -> UkAddress -> emailYesNo Page" in {
      val answers = emptyUserAnswers
        .set(UkAddressPage(index), UKAddress("value 1", "value 2", Some("value 3"), Some("value 4"), "AB1 1AB")).success.value

      navigator.nextPage(UkAddressPage(index), fakeDraftId, answers)
        .mustBe(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
    }

    "InternationalAddress Page -> InternationalAddress -> emailYesNo Page" in {
      val answers = emptyUserAnswers
        .set(InternationalAddressPage(index), InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value

      navigator.nextPage(InternationalAddressPage(index), fakeDraftId, answers)
        .mustBe(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
    }

    "emailYesNo Page -> Yes -> email Page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage(index), true).success.value

      navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.EmailAddressController.onPageLoad(index, fakeDraftId))
    }

    "emailYesNo Page -> No -> TelephoneNumber Page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressYesNoPage(index), false).success.value

      navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId))
    }


    "email Page -> emailaddress -> TelephoneNumber Page" in {
      val answers = emptyUserAnswers
        .set(EmailAddressPage(index), "test@test.com").success.value

      navigator.nextPage(EmailAddressPage(index), fakeDraftId, answers)
        .mustBe(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId))
    }

    "TelephoneNumber Page -> TelephoneNumber -> CheckDetails Page" in {
      val answers = emptyUserAnswers
        .set(TelephoneNumberPage(index), "123456789").success.value

      navigator.nextPage(TelephoneNumberPage(index), fakeDraftId, answers)
        .mustBe(rts.CheckDetailsController.onPageLoad(index, fakeDraftId))
    }
  }
}
