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

package repositories

import base.SpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection}
import models.core.pages.TrusteeOrLeadTrustee._
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.registration.pages.AddATrustee
import models.{RegistrationSubmission, Status, UserAnswers}
import pages.entitystatus.TrusteeStatus
import pages.register.leadtrustee.{individual => ltind}
import pages.register.trustees.{organisation => torg}
import pages.register.{AddATrusteePage, TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.libs.json.{JsBoolean, JsString, Json}

import java.time.LocalDate

class SubmissionSetFactorySpec extends SpecBase {

  private val expectedTrusteeMappedJson = Json.parse(
    """
      |[
      | {
      |   "trusteeOrg":{
      |     "name":"Org Name1"
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
      |   "dateOfBirth":"2000-10-10",
      |   "phoneNumber":"0191 1111111",
      |   "identification":{
      |     "nino":"AB123456C"
      |   }
      | }
      |}
      |""".stripMargin)

  private def addTrustee(index: Int, userAnswers: UserAnswers): UserAnswers = {
    userAnswers
      .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
      .set(torg.NamePage(index), "Org Name1").success.value
      .set(TrusteeStatus(index), Status.Completed).success.value
  }

  private def addLeadTrustee(index: Int, userAnswers: UserAnswers): UserAnswers = {
    userAnswers.set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
      .set(ltind.TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
      .set(ltind.TrusteesDateOfBirthPage(index), LocalDate.of(2000,10,10)).success.value
      .set(ltind.TrusteeNinoYesNoPage(index), true).success.value
      .set(ltind.TrusteesNinoPage(index), "AB123456C").success.value
      .set(ltind.AddressUkYesNoPage(index), true).success.value
      .set(ltind.UkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
      .set(ltind.EmailAddressYesNoPage(index), false).success.value
      .set(ltind.TelephoneNumberPage(index), "0191 1111111").success.value
      .set(TrusteeStatus(index), Status.Completed).success.value
  }

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "create submission data set" when {

      "there are no trustees or lead trustees" must {
        "return a valid empty data set" in {
          factory.createFrom(emptyUserAnswers) mustBe RegistrationSubmission.DataSet(
            data = Json.toJson(emptyUserAnswers),
            registrationPieces = List.empty,
            answerSections = List.empty)
        }
      }

      "there is a lead trustee" must {
        "return with mapped data" in {
          val leadTrusteeOnlySections = List(
            AnswerSection(
              headingKey = Some("answersPage.section.trustee.subheading"),
              rows = List(
                AnswerRow("leadTrustee.individualOrBusiness.checkYourAnswersLabel", "Individual", "first name Last Name"),
                AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", "first name middle name Last Name", ""),
                AnswerRow("leadTrustee.individual.dateOfBirth.checkYourAnswersLabel", "10 October 2000", "first name Last Name"),
                AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("leadTrustee.individual.nino.checkYourAnswersLabel", "AB 12 34 56 C", "first name Last Name"),
                AnswerRow("leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("leadTrustee.individual.ukAddress.checkYourAnswersLabel", "line1<br />line2<br />NE65QA", "first name Last Name"),
                AnswerRow("leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel", "No", "first name Last Name"),
                AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", "0191 1111111", "first name Last Name")
              ),
              sectionKey = Some("answersPage.section.trustees.heading"),
              headingArgs = Seq("1")
            )
          )
          val userAnswers = addLeadTrustee(0, emptyUserAnswers)
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(Json.toJson(userAnswers), List(
                        RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson),
                        RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
                        RegistrationSubmission.MappedPiece("correspondence/address",
                          Json.parse("""{"line1":"line1","line2":"line2","postCode":"NE65QA","country":"GB"}""")),
                        RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 1111111"))
                      ), leadTrusteeOnlySections)
        }
      }

      "there is a lead trustee and trustee" must {
        "return  with mapped data" in {
          val answerSections = List(
            AnswerSection(
              headingKey = Some("answersPage.section.trustee.subheading"),
              rows = List(
                AnswerRow("leadTrustee.individualOrBusiness.checkYourAnswersLabel", "Individual", "first name Last Name"),
                AnswerRow("leadTrustee.individual.name.checkYourAnswersLabel", "first name middle name Last Name", ""),
                AnswerRow("leadTrustee.individual.dateOfBirth.checkYourAnswersLabel", "10 October 2000", "first name Last Name"),
                AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("leadTrustee.individual.nino.checkYourAnswersLabel", "AB 12 34 56 C", "first name Last Name"),
                AnswerRow("leadTrustee.individual.liveInTheUkYesNo.checkYourAnswersLabel", "Yes", "first name Last Name"),
                AnswerRow("leadTrustee.individual.ukAddress.checkYourAnswersLabel", "line1<br />line2<br />NE65QA", "first name Last Name"),
                AnswerRow("leadTrustee.individual.emailAddressYesNo.checkYourAnswersLabel", "No", "first name Last Name"),
                AnswerRow("leadTrustee.individual.telephoneNumber.checkYourAnswersLabel", "0191 1111111", "first name Last Name")
              ),
              sectionKey = Some("answersPage.section.trustees.heading"),
              headingArgs = Seq("1")
            ),
            AnswerSection(
              headingKey = Some("answersPage.section.trustee.subheading"),
              rows = List(
                AnswerRow("trustee.individualOrBusiness.checkYourAnswersLabel", "Business", "Org Name1"),
                AnswerRow("trustee.organisation.name.checkYourAnswersLabel", "Org Name1", "Org Name1")),
              sectionKey = None,
              headingArgs = Seq("2")
            )
          )
          val userAnswers =
            addTrustee(1,
              addLeadTrustee(0, emptyUserAnswers))
              .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(data = Json.toJson(userAnswers), registrationPieces = List(
                        RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson),
                        RegistrationSubmission.MappedPiece("trust/entities/trustees", expectedTrusteeMappedJson),
                        RegistrationSubmission.MappedPiece("correspondence/abroadIndicator", JsBoolean(false)),
                        RegistrationSubmission.MappedPiece("correspondence/address",
                          Json.parse("""{"line1":"line1","line2":"line2","postCode":"NE65QA","country":"GB"}""")),
                        RegistrationSubmission.MappedPiece("correspondence/phoneNumber", JsString("0191 1111111"))

                      ), answerSections = answerSections)
        }
      }
    }
  }
}
