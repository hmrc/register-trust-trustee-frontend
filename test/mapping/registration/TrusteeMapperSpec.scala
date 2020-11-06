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
import models.core.pages.TrusteeOrLeadTrustee._
import models.core.pages.{FullName, IndividualOrBusiness, InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.trustees.individual._
import pages.register.trustees.{individual => ind, organisation => org}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}

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

      "be able to create a Trustee Individual" when {
        "minimum data" in {
          val index = 0
          val userAnswers =
            emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(ind.NamePage(index), FullName("first name", None, "last name")).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = FullName("first name", None, "last name"),
              dateOfBirth = None,
              phoneNumber = None,
              identification = None
            )),
            trusteeOrg = None
          )
        }

        "full data" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(ind.NamePage(index), FullName("first name", Some("middle name"), "last name")).success.value
            .set(ind.DateOfBirthYesNoPage(index), true).success.value
            .set(ind.DateOfBirthPage(index), LocalDate.of(1500, 10, 10)).success.value
            .set(ind.NinoYesNoPage(index), true).success.value
            .set(ind.NinoPage(index), "AB123456C").success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = FullName("first name", Some("middle name"), "last name"),
              dateOfBirth = Some(LocalDate.of(1500, 10, 10)),
              phoneNumber = None,
              identification = Some(
                IdentificationType(
                  nino = Some("AB123456C"),
                  passport = None,
                  address = None
                )
              )
            )),
            trusteeOrg = None
          )
        }
      }

      "be able to create a Trustee Organisation" when {
        "minimum data" in {
          val index = 0
          val userAnswers =
            emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
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

        "full data" in {
          val index = 0
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index), "Org Name").success.value
            .set(org.UtrYesNoPage(index), true).success.value
            .set(org.UtrPage(index), "1234567890").success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            trusteeInd = None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = "Org Name",
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = Some("1234567890"),
                  address = None
                )
              )
            )
          )
        }

      }

      "be able to create a list of Trustee Individuals with minimum data" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(index0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index0), IndividualOrBusiness.Individual).success.value
            .set(ind.NamePage(index0), FullName("first name", None, "last name")).success.value

            .set(TrusteeOrLeadTrusteePage(index1), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index1), IndividualOrBusiness.Individual).success.value
            .set(ind.NamePage(index1), FullName("second name", None, "second name")).success.value

        trusteeMapper.build(userAnswers).value mustBe List(
          TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = FullName("first name", None, "last name"),
              dateOfBirth = None,
              phoneNumber = None,
              identification = None
            )),
            trusteeOrg = None
          ),
          TrusteeType(
            trusteeInd = Some(TrusteeIndividualType(
              name = FullName("second name", None, "second name"),
              dateOfBirth = None,
              phoneNumber = None,
              identification = None
            )),
            trusteeOrg = None
          )
        )
      }

      "be able to create a list of Trustee Organisations with minimum data" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(index0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index0), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index0), "Org Name1").success.value

            .set(TrusteeOrLeadTrusteePage(index1), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(index1), IndividualOrBusiness.Business).success.value
            .set(org.NamePage(index1), "Org Name2").success.value

        trusteeMapper.build(userAnswers).value mustBe
          List(
            TrusteeType(
              trusteeInd = None,
              trusteeOrg = Some(TrusteeOrgType(
                name = "Org Name1",
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = None,
                  address = None
                )
              ))
            ),
            TrusteeType(
              trusteeInd = None,
              trusteeOrg = Some(TrusteeOrgType(
                name = "Org Name2",
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = None,
                  address = None
                )
              ))
            )
          )
      }

      "be able to create a Trustee Individual with full data" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(ind.NamePage(index), FullName("first name", Some("middle name"), "last name")).success.value
          .set(ind.DateOfBirthYesNoPage(index), true).success.value
          .set(ind.DateOfBirthPage(index), LocalDate.of(1500, 10, 10)).success.value
          .set(ind.NinoYesNoPage(index), false).success.value
          .set(ind.AddressYesNoPage(index), true).success.value
          .set(ind.AddressUkYesNoPage(index), true).success.value
          .set(ind.UkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value
          .set(ind.PassportDetailsYesNoPage(index), false).success.value
          .set(ind.IDCardDetailsYesNoPage(index), false).success.value

        trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
          trusteeInd = Some(TrusteeIndividualType(
            name = FullName("first name", Some("middle name"), "last name"),
            dateOfBirth = Some(LocalDate.of(1500, 10, 10)),
            phoneNumber = None,
            identification = Some(
              IdentificationType(
                nino = None,
                passport = None,
                address = Some(AddressType("line1", "line2", None, None, Some("NE65QA"), "GB"))
              )
            )
          )),
          trusteeOrg = None
        )
      }

      "be able to create a Trustee Organisation with full data" when {

        val index = 0
        val name: String = "Org Name"

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
          .set(org.NamePage(index), name).success.value

        "no address or UTR" in {

          val userAnswers = baseAnswers
            .set(org.UtrYesNoPage(index), false).success.value
            .set(org.AddressYesNoPage(index), false).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = name,
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = None,
                  address = None
                )
              )
            )
          )
        }

        "utr" in {

          val utr = "1234567890"

          val userAnswers = baseAnswers
            .set(org.UtrYesNoPage(index), true).success.value
            .set(org.UtrPage(index), utr).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = name,
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = Some(utr),
                  address = None
                )
              )
            )
          )
        }

        "UK address" in {

          val address: UKAddress = UKAddress("line1", "line2", None, None, "NE65QA")

          val userAnswers = baseAnswers
            .set(org.UtrYesNoPage(index), false).success.value
            .set(org.AddressYesNoPage(index), true).success.value
            .set(org.AddressUkYesNoPage(index), true).success.value
            .set(org.UkAddressPage(index), address).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = name,
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = None,
                  address = Some(AddressType(address.line1, address.line2, address.line3, address.line4, Some(address.postcode), "GB"))
                )
              )
            )
          )
        }

        "non-UK address" in {

          val address: InternationalAddress = InternationalAddress("line1", "line2", None, "DE")

          val userAnswers = baseAnswers
            .set(org.UtrYesNoPage(index), false).success.value
            .set(org.AddressYesNoPage(index), true).success.value
            .set(org.AddressUkYesNoPage(index), false).success.value
            .set(org.InternationalAddressPage(index), address).success.value

          trusteeMapper.build(userAnswers).value.head mustBe TrusteeType(
            None,
            trusteeOrg = Some(
              TrusteeOrgType(
                name = name,
                phoneNumber = None,
                email = None,
                identification = IdentificationOrgType(
                  utr = None,
                  address = Some(AddressType(address.line1, address.line2, address.line3, None, None, address.country))
                )
              )
            )
          )
        }
      }

      "not be able to create a Trustee Individual when there is only a LeadTrustee" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(NamePage(index), FullName("first name", Some("middle name"), "Last Name")).success.value
          .set(DateOfBirthPage(index), LocalDate.of(1500, 10, 10)).success.value
          .set(NinoYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(NinoPage(index), "AB123456C").success.value
          .set(UkAddressPage(index), UKAddress("line1", "line2", None, None, "NE65QA")).success.value

        trusteeMapper.build(userAnswers) mustNot be(defined)

      }

      "not be able to create a Trustee Organisation when there is only a LeadTrustee" in {
        val index = 0
        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
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
