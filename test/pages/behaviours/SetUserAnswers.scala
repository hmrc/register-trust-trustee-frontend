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

package pages.behaviours

import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.DetailsChoice.Passport
import models.registration.pages.PassportOrIdCardDetails
import models.{UserAnswers, YesNoDontKnow}
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.trustees.{individual => tind, organisation => torg}

import java.time.LocalDate

object SetUserAnswers {

  private val businessName: String = "Name"
  private val individualName: FullName = FullName("First", None, "Last")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val utr: String = "utr"
  private val nino: String = "nino"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val country: String = "DE"
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, country)
  private val passportOrIdCardDetails: PassportOrIdCardDetails = PassportOrIdCardDetails(country, "number", date)
  private val email: String = "email"
  private val tel: String = "tel"

  implicit class SetAnswers(userAnswers: UserAnswers) {

    def setLeadIndividualAnswers(index: Int): UserAnswers = {
      userAnswers
        .set(ltind.TrusteesNamePage(index), individualName).success.value
        .set(ltind.TrusteesDateOfBirthPage(index), date).success.value
        .set(ltind.mld5.CountryOfNationalityInTheUkYesNoPage(index), false).success.value
        .set(ltind.mld5.CountryOfNationalityPage(index), country).success.value
        .set(ltind.TrusteeNinoYesNoPage(index), true).success.value
        .set(ltind.TrusteesNinoPage(index), nino).success.value
        .set(ltind.MatchedYesNoPage(index), true).success.value
        .set(ltind.TrusteeDetailsChoicePage(index), Passport).success.value
        .set(ltind.PassportDetailsPage(index), passportOrIdCardDetails).success.value
        .set(ltind.IDCardDetailsPage(index), passportOrIdCardDetails).success.value
        .set(ltind.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(ltind.mld5.CountryOfResidencePage(index), country).success.value
        .set(ltind.AddressUkYesNoPage(index), true).success.value
        .set(ltind.UkAddressPage(index), ukAddress).success.value
        .set(ltind.InternationalAddressPage(index), internationalAddress).success.value
    }

    def setIndividualAnswers(index: Int): UserAnswers = {
      userAnswers
        .set(tind.NamePage(index), individualName).success.value
        .set(tind.DateOfBirthYesNoPage(index), true).success.value
        .set(tind.DateOfBirthPage(index), date).success.value
        .set(tind.mld5.CountryOfNationalityYesNoPage(index), true).success.value
        .set(tind.mld5.CountryOfNationalityInTheUkYesNoPage(index), false).success.value
        .set(tind.mld5.CountryOfNationalityPage(index), country).success.value
        .set(tind.NinoYesNoPage(index), true).success.value
        .set(tind.NinoPage(index), nino).success.value
        .set(tind.mld5.CountryOfResidenceYesNoPage(index), true).success.value
        .set(tind.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(tind.mld5.CountryOfResidencePage(index), country).success.value
        .set(tind.AddressYesNoPage(index), true).success.value
        .set(tind.AddressUkYesNoPage(index), true).success.value
        .set(tind.UkAddressPage(index), ukAddress).success.value
        .set(tind.InternationalAddressPage(index), internationalAddress).success.value
        .set(tind.PassportDetailsYesNoPage(index), true).success.value
        .set(tind.PassportDetailsPage(index), passportOrIdCardDetails).success.value
        .set(tind.IDCardDetailsYesNoPage(index), true).success.value
        .set(tind.IDCardDetailsPage(index), passportOrIdCardDetails).success.value
        .set(tind.mld5.MentalCapacityYesNoPage(index), YesNoDontKnow.Yes).success.value
    }

    def setLeadBusinessAnswers(index: Int): UserAnswers = {
      userAnswers
        .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
        .set(ltorg.NamePage(index), businessName).success.value
        .set(ltorg.UtrPage(index), utr).success.value
        .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(ltorg.mld5.CountryOfResidencePage(index), country).success.value
        .set(ltorg.AddressUkYesNoPage(index), true).success.value
        .set(ltorg.UkAddressPage(index), ukAddress).success.value
        .set(ltorg.InternationalAddressPage(index), internationalAddress).success.value
        .set(ltorg.EmailAddressYesNoPage(index), true).success.value
        .set(ltorg.EmailAddressPage(index), email).success.value
        .set(ltorg.TelephoneNumberPage(index), tel).success.value
    }

    def setBusinessAnswers(index: Int): UserAnswers = {
      userAnswers
        .set(torg.NamePage(index), businessName).success.value
        .set(torg.UtrYesNoPage(index), true).success.value
        .set(torg.UtrPage(index), utr).success.value
        .set(torg.mld5.CountryOfResidenceYesNoPage(index), true).success.value
        .set(torg.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
        .set(torg.mld5.CountryOfResidencePage(index), country).success.value
        .set(torg.AddressYesNoPage(index), true).success.value
        .set(torg.AddressUkYesNoPage(index), true).success.value
        .set(torg.UkAddressPage(index), ukAddress).success.value
        .set(torg.InternationalAddressPage(index), internationalAddress).success.value
    }
  }
}
