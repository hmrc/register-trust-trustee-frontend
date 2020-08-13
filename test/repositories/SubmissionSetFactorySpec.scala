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
import models.RegistrationSubmission.AnswerSection
import models.Status.{Completed, InProgress}
import models.core.pages.{FullName, IndividualOrBusiness, UKAddress}
import models.{RegistrationSubmission, Status, UserAnswers}
import models.registration.pages.AddATrustee
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.individual._
import pages.register.trustees.organisation.TrusteeOrgNamePage
import pages.register.trustees.{AddATrusteePage, IsThisLeadTrusteePage, TelephoneNumberPage, TrusteeIndividualOrBusinessPage}
import play.api.libs.json.Json

import scala.collection.immutable.Nil

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
      .set(TrusteeOrgNamePage(index), "Org Name1").success.value
  }

  private def addLeadTrustee(index: Int, userAnswers: UserAnswers): UserAnswers = {
    userAnswers.set(IsThisLeadTrusteePage(index), true).success.value
      .set(TrusteeStatus(index), Status.Completed).success.value
      .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
      .set(TrusteesNamePage(index), FullName("first name",  Some("middle name"), "Last Name")).success.value
      .set(TrusteesDateOfBirthPage(index), LocalDate.of(1500,10,10)).success.value
      .set(TrusteeAUKCitizenPage(index), true).success.value
      .set(TrusteeAddressInTheUKPage(index), true).success.value
      .set(TrusteesNinoPage(index), "AB123456C").success.value
      .set(TelephoneNumberPage(index), "0191 1111111").success.value
      .set(TrusteesUkAddressPage(index), UKAddress("line1", "line2" ,None, None, "NE65QA")).success.value
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
          val userAnswers = addLeadTrustee(0, emptyUserAnswers)
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(Completed),
            List(RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson)),
            List.empty
          )
        }
      }

      "there is a completed lead trustee and trustee, and section flagged as complete" must {
        "return Completed with mapped data" in {
          val userAnswers =
            addTrustee(1,
              addLeadTrustee(0, emptyUserAnswers))
            .set(AddATrusteePage, AddATrustee.NoComplete).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            Some(Completed),
            List(
              RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", expectedLeadTrusteeMappedJson),
              RegistrationSubmission.MappedPiece("trust/entities/trustees", expectedTrusteeMappedJson)
            ),
            List.empty
          )
        }
      }
    }

//    "return completed answer sections" when {
//
//      "only one beneficiary" must {
//        "have 'Beneficiaries' as section key" when {
//          "individual beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(IndividualBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Individual beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "class of beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(ClassBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Class of beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "charity beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(CharityBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Charity beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "trust beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(TrustBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Trust beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "company beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(CompanyBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Company beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "large beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(LargeBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Employment related beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//
//          "other beneficiary only" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(OtherBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Other beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                )
//              )
//          }
//        }
//      }
//
//      "more than one beneficiary" must {
//        "have 'Beneficiaries' as section key of the topmost section" when {
//          "individual beneficiary and class of beneficiary" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(IndividualBeneficiaryStatus(0), Completed).success.value
//              .set(ClassBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Individual beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                ),
//                AnswerSection(
//                  Some("Class of beneficiary 1"),
//                  Nil,
//                  None
//                )
//              )
//          }
//
//          "class of beneficiary and trust beneficiary" in {
//            val userAnswers: UserAnswers = emptyUserAnswers
//              .set(ClassBeneficiaryStatus(0), Completed).success.value
//              .set(TrustBeneficiaryStatus(0), Completed).success.value
//
//            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
//              List(
//                AnswerSection(
//                  Some("Class of beneficiary 1"),
//                  Nil,
//                  Some("Beneficiaries")
//                ),
//                AnswerSection(
//                  Some("Trust beneficiary 1"),
//                  Nil,
//                  None
//                )
//              )
//          }
//        }
//      }
//
//    }
  }

}
