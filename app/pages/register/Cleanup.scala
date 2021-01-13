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

package pages.register

import models.UserAnswers
import pages.register.leadtrustee.organisation.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}

import scala.util.Try

trait Cleanup {

  def removeTrusteeIndividual(userAnswers: UserAnswers, index: Int): Try[UserAnswers] = {
    import pages.register.trustees.individual._

    userAnswers.remove(NamePage(index))
      .flatMap(_.remove(DateOfBirthPage(index)))
      .flatMap(_.remove(NinoYesNoPage(index)))
      .flatMap(_.remove(NinoPage(index)))
      .flatMap(_.remove(AddressUkYesNoPage(index)))
      .flatMap(_.remove(UkAddressPage(index)))
      .flatMap(_.remove(InternationalAddressPage(index)))
  }

  def removeTrusteeBusiness(userAnswers: UserAnswers, index: Int): Try[UserAnswers] = {
    import pages.register.trustees.organisation._

    userAnswers.remove(NamePage(index))
      .flatMap(_.remove(UtrYesNoPage(index)))
      .flatMap(_.remove(UtrPage(index)))
      .flatMap(_.remove(AddressYesNoPage(index)))
      .flatMap(_.remove(AddressUkYesNoPage(index)))
      .flatMap(_.remove(UkAddressPage(index)))
      .flatMap(_.remove(InternationalAddressPage(index)))
  }

  def removeLeadTrusteeIndividual(userAnswers: UserAnswers, index: Int): Try[UserAnswers] = {
    import pages.register.leadtrustee.individual._

    userAnswers.remove(TrusteesNamePage(index))
      .flatMap(_.remove(TrusteesDateOfBirthPage(index)))
      .flatMap(_.remove(TrusteeNinoYesNoPage(index)))
      .flatMap(_.remove(TrusteesNinoPage(index)))
      .flatMap(_.remove(TrusteeDetailsChoicePage(index)))
      .flatMap(_.remove(PassportDetailsPage(index)))
      .flatMap(_.remove(IDCardDetailsPage(index)))
      .flatMap(_.remove(AddressUkYesNoPage(index)))
      .flatMap(_.remove(UkAddressPage(index)))
      .flatMap(_.remove(InternationalAddressPage(index)))
      .flatMap(_.remove(EmailAddressYesNoPage(index)))
      .flatMap(_.remove(EmailAddressPage(index)))
      .flatMap(_.remove(TelephoneNumberPage(index)))
  }

  def removeLeadTrusteeBusiness(userAnswers: UserAnswers, index: Int): Try[UserAnswers] = {
    import pages.register.leadtrustee.organisation._

    userAnswers.remove(UkRegisteredYesNoPage(index))
      .flatMap(_.remove(NamePage(index)))
      .flatMap(_.remove(UtrPage(index)))
      .flatMap(_.remove(CountryOfResidenceInTheUkYesNoPage(index)))
      .flatMap(_.remove(CountryOfResidencePage(index)))
      .flatMap(_.remove(AddressUkYesNoPage(index)))
      .flatMap(_.remove(UkAddressPage(index)))
      .flatMap(_.remove(InternationalAddressPage(index)))
      .flatMap(_.remove(EmailAddressYesNoPage(index)))
      .flatMap(_.remove(EmailAddressPage(index)))
      .flatMap(_.remove(TelephoneNumberPage(index)))
  }

}
