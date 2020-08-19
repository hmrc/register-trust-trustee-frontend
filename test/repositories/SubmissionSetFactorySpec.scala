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

package repositories

import java.time.LocalDate

import base.SpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection}
import models.Status.{Completed, InProgress}
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddATrustee
import models.{RegistrationSubmission, Status, UserAnswers}
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.individual._
import pages.register.trustees.{AddATrusteePage, IsThisLeadTrusteePage, TelephoneNumberPage, TrusteeIndividualOrBusinessPage, organisation => org}
import play.api.libs.json.{JsBoolean, JsString, Json}

class SubmissionSetFactorySpec extends SpecBase {

  private val expectedTrusteeMappedJson = Json.parse(
    """
      |[
      | {
      |   "trusteeOrg":{
      |     "name":"Org Name1",
      |     "identification":{}
      |   }
      | }
      |]
      |""".stripMargin)

  private val expectedLeadTrusteeMappedJson = Json.parse(
    """
      |{
      | "leadTrusteeInd":{
      |   "name":{
      |     "firstName":"first name",
      |     "middleName":"middle name",
      |     "lastName":"Last Name"
      |   },
      |   "dateOfBirth":"1500-10-10",
      |   "phoneNumber":"0191 1111111",
      |   "identification":{
      |     "nino":"AB123456C"
      |   }
      | }
      |}
      |""".stripMargin)

  private def addTrustee(index: Int, userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(TrusteeStatus(index), Status.Completed).success.value
      .set(IsThisLeadTrusteePage(index), false).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
      .set(org.NamePage(index), "Org Name1").success.value
  }

  private def addLeadTrustee(index: Int, userAnswers: UserAnswers): UserAnswers = {
    userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
      .set(TrusteeStatus(index), Status.Completed).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
      .set(NamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
      .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
      .set(NinoYesNoPage(index), true).success.value
      .set(NinoPage(index), "AB123456C").success.value
      .set(AddressUkYesNoPage(index), true).success.value
      .set(UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
      .set(TelephoneNumberPage(index), "0191 1111111").success.value
  }

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "create submission data set" when {

      "there are no trustees or lead trustees" must {
        "return a valid empty data set" in {
          factory.createFrom(emptyUserAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(emptyUserAnswers),
            None,
            List.empty,
            List.empty
          )
        }
      }

      "there are trustees that are incomplete" must {
        "return InProgress status" in {
          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(IsThisLeadTrusteePage(1), false).success.value
            .set(TrusteeStatus(1), Status.Completed).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(InProgress),
            List.empty,
            List.empty
          )
        }
      }

      "there are trustees that are complete, but section flagged not complete" must {
        "return InProgress status" in {
          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeStatus(0), Status.Completed).success.value
            .set(IsThisLeadTrusteePage(1), false).success.value
            .set(TrusteeStatus(1), Status.Completed).success.value
            .set(AddATrusteePage, AddATrustee.YesLater).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(InProgress),
            List.empty,
            List.empty
          )
        }
      }

      "there are completed trustees, the section is flagged as completed, but there is no lead trustee" must {
        "return InProgress status" in {
          val userAnswers = emptyUserAnswers
            .set(IsThisLeadTrusteePage(0), false).success.value
            .set(TrusteeStatus(0), Status.Completed).success.value
            .set(IsThisLeadTrusteePage(1), false).success.value
            .set(TrusteeStatus(1), Status.Completed).success.value
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(InProgress),
            List.empty,
            List.empty
          )
        }
      }

      "there is a completed lead trustee, and section flagged as complete" must {
        "return Completed with mapped data" in {
          val leadTrusteeOnlySections = List(
            AnswerSection(
              Some("answerPage.section.trustee.subheading 1"),
              List(
                AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", "Individual", ""),
                AnswerRow("leadTrusteesName.checkYourAnswersLabel", "first name middle name Last Name", ""),
                AnswerRow("trusteesDateOfBirth.checkYourAnswersLabel", "10 October 1500", "first name Last Name"),
                AnswerRow("trusteeAUKCitizen.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("trusteesNino.checkYourAnswersLabel", "AB 12 34 56 C", "first name Last Name"),
                AnswerRow("trusteeLiveInTheUK.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("trusteesUkAddress.checkYourAnswersLabel", "line1<br />line2<br />NE65QA", "first name Last Name"),
                AnswerRow("telephoneNumber.checkYourAnswersLabel", "0191 1111111", "first name Last Name")
              ),
              Some("answerPage.section.trustees.heading")
            )
          )
          val userAnswers = addLeadTrustee(0, emptyUserAnswers)
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(Completed),
            List(
              RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson),
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address",
                Json.parse("""{"line1":"line1","line2":"line2","postCode":"NE65QA","country":"GB"}""")),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 1111111"))
            ),
            leadTrusteeOnlySections
          )
        }
      }

      "there is a completed lead trustee and trustee, and section flagged as complete" must {
        "return Completed with mapped data" in {
          val answerSections = List(
            AnswerSection(
              Some("answerPage.section.trustee.subheading 1"),
              List(
                AnswerRow("leadTrusteeIndividualOrBusiness.checkYourAnswersLabel", "Individual", ""),
                AnswerRow("leadTrusteesName.checkYourAnswersLabel", "first name middle name Last Name", ""),
                AnswerRow("trusteesDateOfBirth.checkYourAnswersLabel", "10 October 1500", "first name Last Name"),
                AnswerRow("trusteeAUKCitizen.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("trusteesNino.checkYourAnswersLabel", "AB 12 34 56 C", "first name Last Name"),
                AnswerRow("trusteeLiveInTheUK.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("trusteesUkAddress.checkYourAnswersLabel", "line1<br />line2<br />NE65QA", "first name Last Name"),
                AnswerRow("telephoneNumber.checkYourAnswersLabel", "0191 1111111", "first name Last Name")
              ),
              Some("answerPage.section.trustees.heading")
            ),
            AnswerSection(
              Some("answerPage.section.trustee.subheading 2"),
              List(
                AnswerRow("trusteeIndividualOrBusiness.checkYourAnswersLabel", "Business", ""),
                AnswerRow("trusteeBusinessName.checkYourAnswersLabel", "Org Name1", "")),
              None
            )
          )
          val userAnswers =
            addTrustee(1,
              addLeadTrustee(0, emptyUserAnswers))
              .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(Completed),
            List(
              RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson),
              RegistrationSubmission.MappedPiece("trust/entities/trustees", expectedTrusteeMappedJson),
              RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
              RegistrationSubmission.MappedPiece("correspondence/address",
                Json.parse("""{"line1":"line1","line2":"line2","postCode":"NE65QA","country":"GB"}""")),
              RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 1111111"))

            ),
            answerSections
          )
        }
      }
    }
  }
}
