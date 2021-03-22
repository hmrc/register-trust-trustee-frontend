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

import base.SpecBase
import generators.Generators
import mapping.registration.IdentificationMapper.buildAddress
import models.RegistrationSubmission
import models.core.pages.IndividualOrBusiness._
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.libs.json.{JsBoolean, JsString, Json}

import java.time.LocalDate

class CorrespondenceMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  private val correspondenceMapper = injector.instanceOf[CorrespondenceMapper]

  "CorrespondenceMapper" when {

    "user answers is empty" must {

      "create empty list" in {
        correspondenceMapper.build(emptyUserAnswers) mustBe List.empty
      }
    }

    "user answers is not empty" when {

      "for a UK lead trustee individual" must {

        "not be able to create correspondence pieces when do not have all answers" in {
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(ltind.TrusteesNamePage(0), FullName("First", None, "Last")).success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "be able to create a correspondence when have all required answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(ltind.TrusteesNamePage(0), FullName("First", None, "Last")).success.value
            .set(ltind.TrusteesDateOfBirthPage(0), LocalDate.of(2010, 10, 10)).success.value
            .set(ltind.TrusteeNinoYesNoPage(0), true).success.value
            .set(ltind.TrusteesNinoPage(0), "nino").success.value
            .set(ltind.AddressUkYesNoPage(0), true).success.value
            .set(ltind.UkAddressPage(0), address).success.value
            .set(ltind.EmailAddressYesNoPage(0), false).success.value
            .set(ltind.TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(buildAddress(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }

      }

      "for a UK lead trustee organisation" must {

        "not be able to create a correspondence when do not have all answers" in {
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(0), true).success.value
            .set(ltorg.NamePage(0), "Org Name").success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "be able to create a correspondence when have all required answers" in {
          val address = UKAddress("First line", "Second line", None, Some("Newcastle"), "NE981ZZ")

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(0), true).success.value
            .set(ltorg.NamePage(0), "Org Name").success.value
            .set(ltorg.UtrPage(0), "1234567890").success.value
            .set(ltorg.AddressUkYesNoPage(0), true).success.value
            .set(ltorg.UkAddressPage(0), address).success.value
            .set(ltorg.EmailAddressYesNoPage(0), false).success.value
            .set(ltorg.TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(buildAddress(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }
      }

      "for a Non-UK lead trustee organisation" must {

        "not be able to create pieces when do not have all answers" in {
          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
            .set(ltorg.NamePage(0), "Org Name").success.value

          correspondenceMapper.build(userAnswers) mustBe List.empty
        }

        "be able to create a correspondence when have all required answers" in {
          val address = InternationalAddress("First line", "Second line", None, "DE")

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(0), true).success.value
            .set(ltorg.NamePage(0), "Org Name").success.value
            .set(ltorg.UtrPage(0), "1234567890").success.value
            .set(ltorg.AddressUkYesNoPage(0), false).success.value
            .set(ltorg.InternationalAddressPage(0), address).success.value
            .set(ltorg.EmailAddressYesNoPage(0), false).success.value
            .set(ltorg.TelephoneNumberPage(0), "0191 222222").success.value

          correspondenceMapper.build(userAnswers) mustBe
            List(
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(true)),
              RegistrationSubmission.MappedPiece("correspondence/address", Json.toJson(buildAddress(address))),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 222222"))
            )
        }
      }
    }
  }
}
