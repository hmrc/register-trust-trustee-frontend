/*
 * Copyright 2020 HM Revenue & Customs
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
import mapping.Mapping
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.trustees.individual._
import pages.register.trustees._
import pages.register.leadtrustee.{organisation => ltorg}

class LeadTrusteeMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val leadTrusteeMapper: Mapping[LeadTrusteeType] = injector.instanceOf[LeadTrusteeMapper]

  "LeadTrusteeMapper" when {

    "user answers is empty" must {
      "not be able to create LeadTrusteeType" in {
        val userAnswers = emptyUserAnswers
        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "user answers is not empty " must {

      "be able to create LeadTrusteeType with lead trustee individual" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(NamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(DateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
          .set(NinoYesNoPage(index), true).success.value
          .set(NinoPage(index), "AB123456C").success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
          .set(TelephoneNumberPage(index), "0191 1111111").success.value

        leadTrusteeMapper.build(userAnswers).value mustBe LeadTrusteeType(
          leadTrusteeInd = Some(LeadTrusteeIndType(
            NameType("first name", Some("middle name"), "Last Name"),
            dateOfBirth = LocalDate.of(1500,10,10),
            phoneNumber = "0191 1111111",
            email = None,
            identification = IdentificationType(
              nino = Some("AB123456C"),
              None,
              None
            )
          ))
        )
      }

      "be able to create LeadTrusteeType from UK registered business, UK address" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
          .set(ltorg.NamePage(index), "Org Name").success.value
          .set(ltorg.UtrPage(index), "1234567890").success.value
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
            )
          ))
        )
      }

      "be able to create LeadTrusteeType from UK registered business with non-uk address" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.UkRegisteredYesNoPage(index), true).success.value
          .set(ltorg.UtrPage(index), "1234567890").success.value
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
            )
          ))
        )
      }

      "be able to create LeadTrusteeType from Non-UK registered business with uk address" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
          .set(ltorg.NamePage(index), "Org Name").success.value
          .set(ltorg.AddressUkYesNoPage(index), true).success.value
          .set(ltorg.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
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
                postCode = Some("NE65QA"),
                country = "GB"
              ))
            )
          ))
        )
      }

      "be able to create LeadTrusteeType from Non-UK registered business with non-uk address" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
          .set(ltorg.NamePage(index), "Org Name").success.value
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
            )
          ))
        )
      }

      "be able to create LeadTrusteeType with lead trustee organisation Non-UK, no UTR" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.UkRegisteredYesNoPage(index), false).success.value
          .set(ltorg.NamePage(index), "Org Name").success.value
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
            )
          ))
        )
      }

      "not be able to create LeadTrusteeType with only trustee individual which is not lead." in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(NamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(DateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value

        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }

      "not be able to create LeadTrusteeType with only trustee organisation which is not lead." in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(ltorg.NamePage(index), "Org Name").success.value

        leadTrusteeMapper.build(userAnswers) mustNot be(defined)
      }

      "not be able to create LeadTrusteeType without telephone number for trustee individual" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(NamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
          .set(DateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
          .set(NinoYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(NinoPage(index), "AB123456C").success.value
          .set(UkAddressPage(index), UKAddress("line1", "line2",None, Some("line4"), "NE65QA")).success.value

         leadTrusteeMapper.build(userAnswers) mustNot be(defined)

      }

      "not be able to create LeadTrusteeType without telephone number for trustee organisation" in {

        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
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
