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

package mapping.registration

import java.time.LocalDate
import base.SpecBase
import generators.Generators
import models.{AddressType, IdentificationOrgType, IdentificationType, LeadTrusteeIndType, LeadTrusteeOrgType, LeadTrusteeType, PassportType}
import models.core.pages.TrusteeOrLeadTrustee._
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import models.registration.pages.{DetailsChoice, PassportOrIdCardDetails}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}

class LeadTrusteeMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val leadTrusteeMapper: Mapper[LeadTrusteeType] = injector.instanceOf[LeadTrusteeMapper]

  "LeadTrusteeMapper" when {

    "user answers is empty" must {

      "not be able to create LeadTrusteeType" in {
        val userAnswers = emptyUserAnswers
        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "user answers is not empty " when {

      "mapping a lead trustee individual" must {

        "create LeadTrusteeType for lead trustee individual with nino" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(ltind.TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
            .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
            .set(ltind.TrusteeNinoYesNoPage(index), true).success.value
            .set(ltind.TrusteesNinoPage(index), "AB123456C").success.value
            .set(ltind.AddressUkYesNoPage(index), true).success.value
            .set(ltind.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
            .set(ltind.EmailAddressYesNoPage(index), false).success.value
            .set(ltind.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeInd = Some(
              LeadTrusteeIndType(
                FullName("first name", Some("middle name"), "Last Name"),
                dateOfBirth = LocalDate.of(1500,10,10),
                phoneNumber = "0191 1111111",
                email = None,
                identification = IdentificationType(nino = Some("AB123456C"), None, None),
                countryOfResidence = None,
                nationality = None
              ))
          )
        }

        "create LeadTrusteeType for lead trustee individual with passport, address and email" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(ltind.TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
            .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
            .set(ltind.mld5.CountryOfNationalityInTheUkYesNoPage(index), true).success.value
            .set(ltind.TrusteeNinoYesNoPage(index), false).success.value
            .set(ltind.mld5.CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(ltind.TrusteeDetailsChoicePage(index), DetailsChoice.Passport).success.value
            .set(ltind.PassportDetailsPage(index), PassportOrIdCardDetails("FR", "number", LocalDate.of(1500,10,10))).success.value
            .set(ltind.AddressUkYesNoPage(index), true).success.value
            .set(ltind.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
            .set(ltind.EmailAddressYesNoPage(index), true).success.value
            .set(ltind.EmailAddressPage(index), "adam@test.com").success.value
            .set(ltind.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeInd = Some(
              LeadTrusteeIndType(
                FullName("first name", Some("middle name"), "Last Name"),
                dateOfBirth = LocalDate.of(1500,10,10),
                phoneNumber = "0191 1111111",
                email = Some("adam@test.com"),
                identification = IdentificationType(
                  nino = None,
                  passport = Some(PassportType("number", LocalDate.of(1500,10,10), "FR")),
                  address = Some(AddressType(line1 = "line1", line2 = "line2", line3 = None, line4 = None, postCode = Some("NE65QA"), country = "GB"))),
                countryOfResidence = Some("GB"),
                nationality = Some("GB")
              ))
          )
        }

        "create LeadTrusteeType for lead trustee individual with id card, address and email" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(ltind.TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
            .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
            .set(ltind.mld5.CountryOfNationalityInTheUkYesNoPage(index), false).success.value
            .set(ltind.mld5.CountryOfNationalityPage(index), "DE").success.value
            .set(ltind.TrusteeNinoYesNoPage(index), false).success.value
            .set(ltind.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(ltind.mld5.CountryOfResidencePage(index), "DE").success.value
            .set(ltind.TrusteeDetailsChoicePage(index), DetailsChoice.Passport).success.value
            .set(ltind.PassportDetailsPage(index), PassportOrIdCardDetails("DE", "number", LocalDate.of(1500,10,10))).success.value
            .set(ltind.AddressUkYesNoPage(index), false).success.value
            .set(ltind.InternationalAddressPage(index), InternationalAddress("line1", "line2" , Some("line3"), "DE")).success.value
            .set(ltind.EmailAddressYesNoPage(index), true).success.value
            .set(ltind.EmailAddressPage(index), "adam@test.com").success.value
            .set(ltind.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeInd = Some(
              LeadTrusteeIndType(
                FullName("first name", Some("middle name"), "Last Name"),
                dateOfBirth = LocalDate.of(1500,10,10),
                phoneNumber = "0191 1111111",
                email = Some("adam@test.com"),
                identification = IdentificationType(
                  nino = None,
                  passport = Some(PassportType("number", LocalDate.of(1500,10,10), "DE")),
                  address = Some(AddressType(line1 = "line1", line2 = "line2", line3 = Some("line3"), line4 = None, postCode = None, country = "DE"))),
                countryOfResidence = Some("DE"),
                nationality = Some("DE")
              ))
          )
        }

        "not create LeadTrusteeType for not lead trustee" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), Trustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(pages.register.trustees.individual.NamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
            .set(pages.register.trustees.individual.DateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value

          leadTrusteeMapper.build(userAnswers) mustNot be(defined)
        }

        "not create LeadTrusteeType without telephone number" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(ltind.TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
            .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
            .set(ltind.TrusteeNinoYesNoPage(index), true).success.value
            .set(ltind.AddressUkYesNoPage(index), true).success.value
            .set(ltind.TrusteesNinoPage(index), "AB123456C").success.value
            .set(ltind.UkAddressPage(index), UKAddress("line1", "line2",None, Some("line4"), "NE65QA")).success.value

          leadTrusteeMapper.build(userAnswers) mustNot be(defined)

        }

      }

      "mapping a lead trustee business" must {

        "be able to create LeadTrusteeType from UK registered business, UK address" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.UtrPage(index), "1234567890").success.value
            .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(ltorg.AddressUkYesNoPage(index), true).success.value
            .set(ltorg.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
            .set(ltorg.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeOrg = Some(LeadTrusteeOrgType("Org Name",
              phoneNumber = "0191 1111111",
              email = None,
              identification = IdentificationOrgType(
                utr = Some("1234567890"),
                None
              ),
              countryOfResidence = Some("GB")
            ))
          )
        }

        "be able to create LeadTrusteeType from UK registered business with non-uk address" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
            .set(ltorg.UtrPage(index), "1234567890").success.value
            .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.AddressUkYesNoPage(index), false).success.value
            .set(ltorg.InternationalAddressPage(index), InternationalAddress("line1", "line2", None, "FR")).success.value
            .set(ltorg.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeOrg = Some(LeadTrusteeOrgType("Org Name",
              phoneNumber = "0191 1111111",
              email = None,
              identification = IdentificationOrgType(
                utr = Some("1234567890"),
                address = None
              ),
              countryOfResidence = Some("GB")
            ))
          )
        }

        "be able to create LeadTrusteeType from Non-UK registered business with uk address and email" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(ltorg.AddressUkYesNoPage(index), true).success.value
            .set(ltorg.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
            .set(ltorg.TelephoneNumberPage(index), "0191 1111111").success.value
            .set(ltorg.EmailAddressYesNoPage(index), true).success.value
            .set(ltorg.EmailAddressPage(index), "adam@test.com").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeOrg = Some(LeadTrusteeOrgType(
              "Org Name",
              phoneNumber = "0191 1111111",
              email = Some("adam@test.com"),
              identification = IdentificationOrgType(
                utr = None,
                address = Some(AddressType(
                  line1 = "line1",
                  line2 = "line2",
                  line3 = None,
                  line4 = None,
                  postCode = Some("NE65QA"),
                  country = "GB"
                ))
              ),
              countryOfResidence = Some("GB")
            ))
          )
        }

        "be able to create LeadTrusteeType from Non-UK registered business with non-uk address" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(ltorg.mld5.CountryOfResidencePage(index), "FR").success.value
            .set(ltorg.AddressUkYesNoPage(index), false).success.value
            .set(ltorg.InternationalAddressPage(index), InternationalAddress("line1", "line2", None, "FR")).success.value
            .set(ltorg.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeOrg = Some(LeadTrusteeOrgType("Org Name",
              phoneNumber = "0191 1111111",
              email = None,
              identification = IdentificationOrgType(
                utr = None,
                address = Some(AddressType(
                  line1 = "line1",
                  line2 = "line2",
                  line3 = None,
                  line4 = None,
                  postCode = None,
                  country = "FR"
                ))
              ),
              countryOfResidence = Some("FR")
            ))
          )
        }

        "be able to create LeadTrusteeType with lead trustee organisation Non-UK, no UTR" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.mld5.CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(ltorg.mld5.CountryOfResidencePage(index), "FR").success.value
            .set(ltorg.AddressUkYesNoPage(index), false).success.value
            .set(ltorg.InternationalAddressPage(index), InternationalAddress("line1", "line2", None, "FR")).success.value
            .set(ltorg.TelephoneNumberPage(index), "0191 1111111").success.value

          leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
            leadTrusteeOrg = Some(LeadTrusteeOrgType("Org Name",
              phoneNumber = "0191 1111111",
              email = None,
              identification = IdentificationOrgType(
                utr = None,
                address = Some(AddressType(
                  line1 = "line1",
                  line2 = "line2",
                  line3 = None,
                  line4 = None,
                  postCode = None,
                  country = "FR"
                ))
              ),
              countryOfResidence = Some("FR")
            ))
          )
        }

        "not be able to create LeadTrusteeType with only trustee organisation which is not lead." in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), Trustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value

          leadTrusteeMapper.build(userAnswers) mustNot be(defined)
        }

        "not be able to create LeadTrusteeType without telephone number for trustee organisation" in {

          val index = 0
          val userAnswers = emptyUserAnswers
            .set(register.TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
            .set(register.TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
            .set(ltorg.NamePage(index), "Org Name").success.value
            .set(ltorg.UtrPage(index), "1234567890").success.value
            .set(ltorg.AddressUkYesNoPage(index), true).success.value
            .set(ltorg.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value

          leadTrusteeMapper.build(userAnswers) mustNot be(defined)

        }

      }

    }
  }
}
