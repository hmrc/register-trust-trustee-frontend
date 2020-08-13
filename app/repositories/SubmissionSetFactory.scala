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

import javax.inject.Inject
import mapping.registration.{LeadTrusteeMapper, TrusteeMapper}
import models.Status.{Completed, InProgress}
import models._
import models.registration.pages.AddATrustee
import pages.register.trustees.AddATrusteePage
import play.api.i18n.Messages
import play.api.libs.json.Json
import viewmodels.{AnswerRow, AnswerSection}

class SubmissionSetFactory @Inject()(trusteeMapper: TrusteeMapper, leadTrusteeMapper: LeadTrusteeMapper) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = trusteesStatus(userAnswers)
    answerSectionsIfCompleted(userAnswers, status)

    RegistrationSubmission.DataSet(
      Json.toJson(userAnswers),
      status,
      mappedDataIfCompleted(userAnswers, status),
      answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def trusteesStatus(userAnswers: UserAnswers): Option[Status] = {
    val noMoreToAdd = userAnswers.get(AddATrusteePage).contains(AddATrustee.NoComplete)

    userAnswers.get(_root_.sections.Trustees) match {
      case Some(l) =>
        if (l.isEmpty) {
          None
        } else {
          val hasLeadTrustee = l.exists(_.isLead)
          val isComplete = !l.exists(_.status == InProgress) && noMoreToAdd && hasLeadTrustee

          if (isComplete) {
            Some(Completed)
          } else {
            Some(InProgress)
          }
        }
      case None => None
    }
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]): List[RegistrationSubmission.MappedPiece] = {
    if (status.contains(Status.Completed)) {
      val result: Option[List[RegistrationSubmission.MappedPiece]] = for {
        leadTrustees <- leadTrusteeMapper.build(userAnswers)
      } yield {
        val leadTrusteesPiece = RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", Json.toJson(leadTrustees))
        trusteeMapper.build(userAnswers) match {
          case Some(trustees) =>
            val trusteesPiece = RegistrationSubmission.MappedPiece("trust/entities/trustees", Json.toJson(trustees))
            List(leadTrusteesPiece, trusteesPiece)
          case _ => List(leadTrusteesPiece)
        }
      }
      result match {
        case Some(pieces) => pieces
        case None => List.empty
      }

    } else {
      List.empty
    }
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

//    if (status.contains(Status.Completed)) {
//
//      val individualBeneficiariesHelper = new IndividualBeneficiaryAnswersHelper(countryOptions)(userAnswers, userAnswers.draftId, false)
//      val classOfBeneficiariesHelper = new ClassOfBeneficiariesAnswersHelper(countryOptions)(userAnswers, userAnswers.draftId, false)
//      val charityBeneficiariesHelper = new CharityBeneficiaryAnswersHelper(countryOptions)(userAnswers, userAnswers.draftId, false)
//      val trustBeneficiariesHelper = new TrustBeneficiaryAnswersHelper(countryOptions)(userAnswers, userAnswers.draftId, false)
//
//      val entitySections = List(
//        individualBeneficiariesHelper.individualBeneficiaries,
//        classOfBeneficiariesHelper.classOfBeneficiaries,
//        charityBeneficiariesHelper.charityBeneficiaries,
//        trustBeneficiariesHelper.trustBeneficiaries,
//        companyBeneficiaryAnswersHelper.companyBeneficiaries(userAnswers, canEdit = false),
//        largeBeneficiaryAnswersHelper.employmentRelatedBeneficiaries(userAnswers, canEdit = false),
//        otherBeneficiaryAnswersHelper.otherBeneficiaries(userAnswers, canEdit = false)
//      ).flatten.flatten
//
//      val updatedFirstSection = AnswerSection(
//        entitySections.head.headingKey,
//        entitySections.head.rows,
//        Some(Messages("answerPage.section.beneficiaries.heading"))
//      )
//
//      val updatedSections = updatedFirstSection :: entitySections.tail
//
//      updatedSections.map(convertForSubmission)
//
//    } else {
      List.empty
//    }
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(section.headingKey, section.rows.map(convertForSubmission), section.sectionKey)
  }
}
