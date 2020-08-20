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
import models.RegistrationSubmission
import models.core.pages.IndividualOrBusiness._
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.trustees._
import pages.register.trustees.individual._
import pages.register.trustees.{organisation => org}
import play.api.libs.json.{JsBoolean, JsString, Json}

class CorrespondenceMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val correspondenceMapper = injector.instanceOf[CorrespondenceMapper]
  private val addressMapper = injector.instanceOf[AddressMapper]

  "CorrespondenceMapper" when {

    "user answers is empty" must {

      "create empty list" in {
        correspondenceMapper.build(emptyUserAnswers) mustBe List.empty
      }
    }

    "user answers is not empty" when {

      "for a UK lead trustee individual" must {

        "must not be able to create correspondence pieces when do not have all answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(UkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "must be able to create a correspondence when have all required answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(NamePage(0), FullName("First", None, "Last")).success.value
            .set(DateOfBirthPage(0), LocalDate.of(2010, 10, 10)).success.value
            .set(NinoYesNoPage(0), false).success.value
            .set(AddressUkYesNoPage(0), true).success.value
            .set(UkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(addressMapper.build(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }

      }

      "for a UK lead trustee organisation" must {

        "not be able to create a correspondence when do not have all answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(org.NamePage(0), "Org Name").success.value
            .set(org.UkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "be able to create a correspondence when have all required answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(org.UtrYesNoPage(0), true).success.value
            .set(org.NamePage(0), "Org Name").success.value
            .set(org.UtrPage(0), "1234567890").success.value
            .set(org.AddressUkYesNoPage(0), true).success.value
            .set(org.UkAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(addressMapper.build(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }
      }

      "for a Non-UK lead trustee organisation" must {

        "not be able to create pieces when do not have all answers" in {
          val address = InternationalAddress("First line", "Second line", None, "DE")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(org.NamePage(0), "Org Name").success.value
            .set(org.InternationalAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "be able to create a correspondence when have all required answers" in {
          val address = InternationalAddress("First line", "Second line", None, "DE")

          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(org.UtrYesNoPage(0), true).success.value
            .set(org.NamePage(0), "Org Name").success.value
            .set(org.UtrPage(0), "1234567890").success.value
            .set(org.AddressUkYesNoPage(0), false).success.value
            .set(org.InternationalAddressPage(0), address).success.value
            .set(TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(true)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(addressMapper.build(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }
      }
    }
  }
}
