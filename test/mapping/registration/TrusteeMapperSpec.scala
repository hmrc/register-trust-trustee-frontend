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
import models.UserAnswers
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.trustees.individual._
import pages.register.trustees.{organisation => org, _}

class TrusteeMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val trusteeMapper: Mapping[List[TrusteeType]] = injector.instanceOf[TrusteeMapper]

  "TrusteeMapper" when {

    "user answers is empty" must {

      "not be able to create TrusteeType" in {
        val userAnswers = emptyUserAnswers
        trusteeMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "user answers is not empty" must {

      "be able to create a Trustee Individual with minimum data" in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index), FullName("first name", None, "last name")).success.value

        trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = NameType("first name", None, "last name"),
            dateOfBirth = None,
            phoneNumber = None,
            identification = Some(IdentificationType(None, None, None)))
          ),
          None
        )
      }

      "be able to create a Trustee Organisation with minimum data" in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index), "Org Name").success.value

        trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
          trusteeInd = None,
          trusteeOrg = Some(TrusteeOrgType(
            name = "Org Name",
            phoneNumber = None,
            email = None,
            identification = IdentificationOrgType(None, None)
          ))
        )
      }

      "be able to create a list of Trustee Individuals with minimum data" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index0), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index0), FullName("first name", None, "last name")).success.value

            .set(IsThisLeadTrusteePage(index1), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index1), IndividualOrBusiness.Individual).success.value
            .set(TrusteesNamePage(index1), FullName("second name", None, "second name")).success.value

        trusteeMapper.build(userAnswers).value mustBe List(TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = NameType("first name", None, "last name"),
            dateOfBirth = None,
            phoneNumber = None,
            identification = Some(IdentificationType(None, None, None)))
          ),
          None
        ),
          TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = NameType("second name", None, "second name"),
              dateOfBirth = None,
              phoneNumber = None,
              identification = Some(IdentificationType(None, None, None)))
            ),
            None
          ))
      }

      "be able to create a list of Trustee Organisations with minimum data" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(IsThisLeadTrusteePage(index0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index0), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index0), "Org Name1").success.value

            .set(IsThisLeadTrusteePage(index1), false).success.value
            .set(TrusteeIndividualOrBusinessPage(index1), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index1), "Org Name2").success.value

        trusteeMapper.build(userAnswers).value mustBe
          List(
            TrusteeType(
              None,
              trusteeOrg = Some(
                  TrusteeOrgType(
                  name = "Org Name1",
                  phoneNumber = None,
                  email = None,
                  identification = IdentificationOrgType(None, None)
                )
              )
            ),
            TrusteeType(
              None,
              trusteeOrg = Some(
                TrusteeOrgType(
                  name = "Org Name2",
                  phoneNumber = None,
                  email = None,
                  identification = IdentificationOrgType(None, None)
                )
              )
            )
          )
      }

      "be able to create a Trustee Individual with full data" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name", Some("middle name"), "last name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500, 10, 10)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value

        trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = NameType("first name", Some("middle name"), "last name"),
            dateOfBirth = Some(LocalDate.of(1500, 10, 10)),
            phoneNumber = None,
            identification = Some(
              IdentificationType(
                Some("AB123456C"),
                None,
                Some(AddressType("line1", "line2", None, None, Some("NE65QA"), "GB"))
              )
            )
          )
          ),
          None
        )
      }

      "be able to create a Trustee Organisation with full data" when {

        val index = 0

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(org.NamePage(index), "Org Name").success.value
          .set(org.UtrYesNoPage(index), true).success.value
          .set(org.UtrPage(index), "1234567890").success.value
          .set(org.AddressYesNoPage(index), true).success.value

        "UK address" in {

          val userAnswers = baseAnswers
            .set(org.AddressUkYesNoPage(index), true).success.value
            .set(org.UkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = "Org Name",
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  Some("1234567890"),
                  Some(AddressType("line1", "line2", None, None, Some("NE65QA"), "GB"))
                )
              )
            )
          )
        }

        "non-UK address" in {

          val userAnswers = baseAnswers
            .set(org.AddressUkYesNoPage(index), false).success.value
            .set(org.InternationalAddressPage(index), InternationalAddress("line1", "line2", None, "DE")).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = "Org Name",
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  Some("1234567890"),
                  Some(AddressType("line1", "line2", None, None, None, "DE"))
                )
              )
            )
          )
        }
      }

      "not be able to create a Trustee Individual when there is only a LeadTrustee" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(TrusteesNamePage(index), FullName("first name", Some("middle name"), "Last Name")).success.value
          .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500, 10, 10)).success.value
          .set(TrusteeAUKCitizenPage(index), true).success.value
          .set(TrusteeAddressInTheUKPage(index), true).success.value
          .set(TrusteesNinoPage(index), "AB123456C").success.value
          .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value

        trusteeMapper.build(userAnswers) mustNot be(defined)

      }

      "not be able to create a Trustee Organisation when there is only a LeadTrustee" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), true).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(org.NamePage(index), "Org Name").success.value
          .set(org.UtrYesNoPage(index), true).success.value
          .set(org.UtrPage(index), "1234567890").success.value
          .set(org.AddressYesNoPage(index), true).success.value
          .set(org.AddressUkYesNoPage(index), true).success.value
          .set(org.UkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value

        trusteeMapper.build(userAnswers) mustNot be(defined)

      }
    }
  }

}
