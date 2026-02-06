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

package utils.print

import base.SpecBase
import controllers.register.leadtrustee.individual.mld5.{routes => mld5Rts}
import controllers.register.leadtrustee.individual.{routes => rts}
import controllers.register.{routes => regRts}
import models.UserAnswers
import models.core.pages.IndividualOrBusiness.Individual
import models.core.pages.TrusteeOrLeadTrustee.LeadTrustee
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.DetailsChoice.{IdCard, Passport}
import models.registration.pages.PassportOrIdCardDetails
import pages.register.leadtrustee.individual._
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class LeadTrusteeIndividualPrintHelperSpec extends SpecBase {

  private val index: Int                                 = 0
  private val name: FullName                             = FullName("First", None, "Last")
  private val nino: String                               = "AA000000A"
  private val ukAddress: UKAddress                       = UKAddress("Line 1", "Line 2", None, None, "NE1 1NE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "DE")
  private val email: String                              = "email@example.com"
  private val tel: String                                = "999"
  private val dob                                        = LocalDate.parse("2020-08-21")
  private val passport                                   = PassportOrIdCardDetails("DE", "0987654321234", LocalDate.parse("2020-08-21"))
  private val idCard                                     = PassportOrIdCardDetails("DE", "0987654321234", LocalDate.parse("2020-08-21"))

  "LeadTrusteeIndividualPrintHelper" must {

    val helper = injector.instanceOf[LeadTrusteeIndividualPrintHelper]

    val baseAnswers = emptyUserAnswers
      .set(TrusteeOrLeadTrusteePage(index), LeadTrustee)
      .success
      .value
      .set(TrusteeIndividualOrBusinessPage(index), Individual)
      .success
      .value
      .set(TrusteesNamePage(index), name)
      .success
      .value

    "render a print section" when {

      "lead trustee is passport and uk address" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(TrusteesNamePage(index), name)
          .success
          .value
          .set(TrusteesDateOfBirthPage(index), dob)
          .success
          .value
          .set(TrusteeNinoYesNoPage(index), false)
          .success
          .value
          .set(TrusteeDetailsChoicePage(index), Passport)
          .success
          .value
          .set(PassportDetailsPage(index), passport)
          .success
          .value
          .set(AddressUkYesNoPage(index), true)
          .success
          .value
          .set(UkAddressPage(index), ukAddress)
          .success
          .value
          .set(EmailAddressYesNoPage(index), false)
          .success
          .value
          .set(TelephoneNumberPage(index), tel)
          .success
          .value

        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow(
              "leadTrustee.individualOrBusiness.checkYourAnswersLabel",
              Html("Individual"),
              Some(regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.name.checkYourAnswersLabel",
              Html("First Last"),
              Some(rts.NameController.onPageLoad(index, fakeDraftId).url)
            ),
            AnswerRow(
              "leadTrustee.individual.dateOfBirth.checkYourAnswersLabel",
              Html("21 August 2020"),
              Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.ninoYesNo.checkYourAnswersLabel",
              Html("No"),
              Some(rts.NinoYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel",
              Html("Passport"),
              Some(rts.TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.passportDetails.checkYourAnswersLabel",
              Html("Germany<br />0987654321234<br />21 August 2020"),
              Some(rts.PassportDetailsController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel",
              Html("Yes"),
              Some(rts.LiveInTheUKYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.ukAddress.checkYourAnswersLabel",
              Html("Line 1<br />Line 2<br />NE1 1NE"),
              Some(rts.UkAddressController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel",
              Html("No"),
              Some(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.telephoneNumber.checkYourAnswersLabel",
              Html("999"),
              Some(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId).url),
              name.toString
            )
          )
        )
      }

      "lead trustee is not UK registered with international address and an email" in {
        val userAnswers: UserAnswers = baseAnswers
          .set(TrusteesNamePage(index), name)
          .success
          .value
          .set(TrusteesDateOfBirthPage(index), dob)
          .success
          .value
          .set(TrusteeNinoYesNoPage(index), false)
          .success
          .value
          .set(TrusteeDetailsChoicePage(index), IdCard)
          .success
          .value
          .set(IDCardDetailsPage(index), idCard)
          .success
          .value
          .set(AddressUkYesNoPage(index), false)
          .success
          .value
          .set(InternationalAddressPage(index), internationalAddress)
          .success
          .value
          .set(EmailAddressYesNoPage(index), true)
          .success
          .value
          .set(EmailAddressPage(index), email)
          .success
          .value
          .set(TelephoneNumberPage(index), tel)
          .success
          .value

        val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

        result mustBe AnswerSection(
          None,
          Seq(
            AnswerRow(
              "leadTrustee.individualOrBusiness.checkYourAnswersLabel",
              Html("Individual"),
              Some(regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.name.checkYourAnswersLabel",
              Html("First Last"),
              Some(rts.NameController.onPageLoad(index, fakeDraftId).url)
            ),
            AnswerRow(
              "leadTrustee.individual.dateOfBirth.checkYourAnswersLabel",
              Html("21 August 2020"),
              Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.ninoYesNo.checkYourAnswersLabel",
              Html("No"),
              Some(rts.NinoYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel",
              Html("ID card"),
              Some(rts.TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.iDCardDetails.checkYourAnswersLabel",
              Html("Germany<br />0987654321234<br />21 August 2020"),
              Some(rts.IDCardDetailsController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel",
              Html("No"),
              Some(rts.LiveInTheUKYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.internationalAddress.checkYourAnswersLabel",
              Html("Line 1<br />Line 2<br />Germany"),
              Some(rts.InternationalAddressController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel",
              Html("Yes"),
              Some(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.email.checkYourAnswersLabel",
              Html("email@example.com"),
              Some(rts.EmailAddressController.onPageLoad(index, fakeDraftId).url),
              name.toString
            ),
            AnswerRow(
              "leadTrustee.individual.telephoneNumber.checkYourAnswersLabel",
              Html("999"),
              Some(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId).url),
              name.toString
            )
          )
        )
      }

      "in 5mld mode" when {
        "lead trustee is UK Nationality, UK Resident and UK address" in {
          val userAnswers: UserAnswers = baseAnswers
            .set(TrusteesNamePage(index), name)
            .success
            .value
            .set(TrusteesDateOfBirthPage(index), dob)
            .success
            .value
            .set(mld5.CountryOfNationalityInTheUkYesNoPage(index), true)
            .success
            .value
            .set(TrusteeNinoYesNoPage(index), false)
            .success
            .value
            .set(TrusteeDetailsChoicePage(index), Passport)
            .success
            .value
            .set(PassportDetailsPage(index), passport)
            .success
            .value
            .set(mld5.CountryOfResidenceInTheUkYesNoPage(index), true)
            .success
            .value
            .set(UkAddressPage(index), ukAddress)
            .success
            .value
            .set(EmailAddressYesNoPage(index), false)
            .success
            .value
            .set(TelephoneNumberPage(index), tel)
            .success
            .value

          val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

          result mustBe AnswerSection(
            None,
            Seq(
              AnswerRow(
                "leadTrustee.individualOrBusiness.checkYourAnswersLabel",
                Html("Individual"),
                Some(regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.name.checkYourAnswersLabel",
                Html("First Last"),
                Some(rts.NameController.onPageLoad(index, fakeDraftId).url)
              ),
              AnswerRow(
                "leadTrustee.individual.dateOfBirth.checkYourAnswersLabel",
                Html("21 August 2020"),
                Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel",
                Html("Yes"),
                Some(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.ninoYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(rts.NinoYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel",
                Html("Passport"),
                Some(rts.TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.passportDetails.checkYourAnswersLabel",
                Html("Germany<br />0987654321234<br />21 August 2020"),
                Some(rts.PassportDetailsController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
                Html("Yes"),
                Some(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.ukAddress.checkYourAnswersLabel",
                Html("Line 1<br />Line 2<br />NE1 1NE"),
                Some(rts.UkAddressController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.telephoneNumber.checkYourAnswersLabel",
                Html("999"),
                Some(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId).url),
                name.toString
              )
            )
          )
        }

        "lead trustee is not UK nationality or UK resident with international address and an email" in {
          val userAnswers: UserAnswers = baseAnswers
            .set(TrusteesNamePage(index), name)
            .success
            .value
            .set(TrusteesDateOfBirthPage(index), dob)
            .success
            .value
            .set(mld5.CountryOfNationalityInTheUkYesNoPage(index), false)
            .success
            .value
            .set(mld5.CountryOfNationalityPage(index), "DE")
            .success
            .value
            .set(TrusteeNinoYesNoPage(index), false)
            .success
            .value
            .set(TrusteeDetailsChoicePage(index), IdCard)
            .success
            .value
            .set(IDCardDetailsPage(index), idCard)
            .success
            .value
            .set(mld5.CountryOfResidenceInTheUkYesNoPage(index), false)
            .success
            .value
            .set(mld5.CountryOfResidencePage(index), "DE")
            .success
            .value
            .set(InternationalAddressPage(index), internationalAddress)
            .success
            .value
            .set(EmailAddressYesNoPage(index), true)
            .success
            .value
            .set(EmailAddressPage(index), email)
            .success
            .value
            .set(TelephoneNumberPage(index), tel)
            .success
            .value

          val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

          result mustBe AnswerSection(
            None,
            Seq(
              AnswerRow(
                "leadTrustee.individualOrBusiness.checkYourAnswersLabel",
                Html("Individual"),
                Some(regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.name.checkYourAnswersLabel",
                Html("First Last"),
                Some(rts.NameController.onPageLoad(index, fakeDraftId).url)
              ),
              AnswerRow(
                "leadTrustee.individual.dateOfBirth.checkYourAnswersLabel",
                Html("21 August 2020"),
                Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfNationality.checkYourAnswersLabel",
                Html("Germany"),
                Some(mld5Rts.CountryOfNationalityController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.ninoYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(rts.NinoYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.trusteeDetailsChoice.checkYourAnswersLabel",
                Html("ID card"),
                Some(rts.TrusteeDetailsChoiceController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.iDCardDetails.checkYourAnswersLabel",
                Html("Germany<br />0987654321234<br />21 August 2020"),
                Some(rts.IDCardDetailsController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfResidence.checkYourAnswersLabel",
                Html("Germany"),
                Some(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.internationalAddress.checkYourAnswersLabel",
                Html("Line 1<br />Line 2<br />Germany"),
                Some(rts.InternationalAddressController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel",
                Html("Yes"),
                Some(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.email.checkYourAnswersLabel",
                Html("email@example.com"),
                Some(rts.EmailAddressController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.telephoneNumber.checkYourAnswersLabel",
                Html("999"),
                Some(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId).url),
                name.toString
              )
            )
          )
        }

        "lead trustee has gone through lead trustee matching" in {
          val userAnswers: UserAnswers = baseAnswers
            .set(TrusteesNamePage(index), name)
            .success
            .value
            .set(TrusteesDateOfBirthPage(index), dob)
            .success
            .value
            .set(mld5.CountryOfNationalityInTheUkYesNoPage(index), false)
            .success
            .value
            .set(mld5.CountryOfNationalityPage(index), "DE")
            .success
            .value
            .set(TrusteeNinoYesNoPage(index), true)
            .success
            .value
            .set(TrusteesNinoPage(index), nino)
            .success
            .value
            .set(MatchedYesNoPage(index), true)
            .success
            .value
            .set(mld5.CountryOfResidenceInTheUkYesNoPage(index), false)
            .success
            .value
            .set(mld5.CountryOfResidencePage(index), "DE")
            .success
            .value
            .set(InternationalAddressPage(index), internationalAddress)
            .success
            .value
            .set(EmailAddressYesNoPage(index), true)
            .success
            .value
            .set(EmailAddressPage(index), email)
            .success
            .value
            .set(TelephoneNumberPage(index), tel)
            .success
            .value

          val result = helper.checkDetailsSection(userAnswers, name.toString, index, fakeDraftId)

          result mustBe AnswerSection(
            None,
            Seq(
              AnswerRow(
                "leadTrustee.individualOrBusiness.checkYourAnswersLabel",
                Html("Individual"),
                Some(regRts.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url),
                name.toString,
                canEdit = false
              ),
              AnswerRow(
                "leadTrustee.individual.name.checkYourAnswersLabel",
                Html("First Last"),
                Some(rts.NameController.onPageLoad(index, fakeDraftId).url),
                canEdit = false
              ),
              AnswerRow(
                "leadTrustee.individual.dateOfBirth.checkYourAnswersLabel",
                Html("21 August 2020"),
                Some(rts.DateOfBirthController.onPageLoad(index, fakeDraftId).url),
                name.toString,
                canEdit = false
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfNationality.checkYourAnswersLabel",
                Html("Germany"),
                Some(mld5Rts.CountryOfNationalityController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.ninoYesNo.checkYourAnswersLabel",
                Html("Yes"),
                Some(rts.NinoYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString,
                canEdit = false
              ),
              AnswerRow(
                "leadTrustee.individual.nino.checkYourAnswersLabel",
                Html("AA 00 00 00 A"),
                Some(rts.NinoController.onPageLoad(index, fakeDraftId).url),
                name.toString,
                canEdit = false
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
                Html("No"),
                Some(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.5mld.countryOfResidence.checkYourAnswersLabel",
                Html("Germany"),
                Some(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.internationalAddress.checkYourAnswersLabel",
                Html("Line 1<br />Line 2<br />Germany"),
                Some(rts.InternationalAddressController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel",
                Html("Yes"),
                Some(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.email.checkYourAnswersLabel",
                Html("email@example.com"),
                Some(rts.EmailAddressController.onPageLoad(index, fakeDraftId).url),
                name.toString
              ),
              AnswerRow(
                "leadTrustee.individual.telephoneNumber.checkYourAnswersLabel",
                Html("999"),
                Some(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId).url),
                name.toString
              )
            )
          )
        }
      }
    }
  }

}
