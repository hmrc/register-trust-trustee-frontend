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
import mapping.registration.{CorrespondenceMapper, LeadTrusteeMapper, TrusteeMapper}
import models.Status.{Completed, InProgress}
import models._
import models.registration.pages.AddATrustee
import pages.register.trustees.AddATrusteePage
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.answers.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import utils.print.PrintHelpers
import viewmodels.{AnswerRow, AnswerSection}

class SubmissionSetFactory @Inject()(trusteeMapper: TrusteeMapper,
                                     leadTrusteeMapper: LeadTrusteeMapper,
                                     correspondenceMapper: CorrespondenceMapper,
                                     countryOptions: CountryOptions,
                                     printHelpers: PrintHelpers) {

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
        case Some(pieces) => pieces ++ correspondenceMapper.build(userAnswers)
        case None => List.empty
      }

    } else {
      List.empty
    }
  }

  private def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {
    if (status.contains(Status.Completed)) {
        val helper = new CheckYourAnswersHelper(countryOptions, printHelpers)(userAnswers, userAnswers.draftId, canEdit = false)

        helper.trustees match {
          case Some(answerSections: Seq[AnswerSection]) => answerSections.toList map convertForSubmission
          case None => List.empty
        }
    } else {
      List.empty
    }
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(section.headingKey, section.rows.map(convertForSubmission), section.sectionKey)
  }
}
