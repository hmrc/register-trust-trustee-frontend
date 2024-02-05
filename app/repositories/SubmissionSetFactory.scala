/*
 * Copyright 2024 HM Revenue & Customs
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

import mapping.registration.{CorrespondenceMapper, LeadTrusteeMapper, TrusteeMapper}
import models._
import play.api.Logging
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.answers.CheckYourAnswersHelper
import utils.print.PrintHelpers
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject()(trusteeMapper: TrusteeMapper,
                                     leadTrusteeMapper: LeadTrusteeMapper,
                                     correspondenceMapper: CorrespondenceMapper,
                                     printHelpers: PrintHelpers) extends Logging {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {

    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      registrationPieces = mappedData(userAnswers),
      answerSections = answerSections(userAnswers)
    )
  }

  private def mappedData(userAnswers: UserAnswers) = {

    logger.info(s"[mappedData] attempting to generate mapped data")

      val result: Option[List[RegistrationSubmission.MappedPiece]] = leadTrusteeMapper.build(userAnswers) match {
        case Some(x) =>
          val leadTrusteesPiece = RegistrationSubmission.MappedPiece("trust/entities/leadTrustees", Json.toJson(x))

          trusteeMapper.build(userAnswers) match {
            case Some(trustees) =>
              val trusteesPiece = RegistrationSubmission.MappedPiece("trust/entities/trustees", Json.toJson(trustees))
              Some(List(leadTrusteesPiece, trusteesPiece))
            case _ =>
              Some(List(leadTrusteesPiece))
          }
        case None =>
          logger.warn(s"[mappedDataIfCompleted] unable to generate a lead trustee")
          None
      }

      result match {
        case Some(pieces) => pieces ++ correspondenceMapper.build(userAnswers)
        case None =>
          logger.warn(s"[mappedDataIfCompleted] lead trustee status is complete, no data to map")
          List.empty
      }
  }

  private def answerSections(userAnswers: UserAnswers)
                                       (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {
      val helper = new CheckYourAnswersHelper(printHelpers)(userAnswers, userAnswers.draftId, canEdit = false)

      helper.trustees match {
        case Some(answerSections: Seq[AnswerSection]) => answerSections.toList map convertForSubmission
        case None => List.empty
      }
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(
      headingKey = section.headingKey,
      rows = section.rows.map(convertForSubmission),
      sectionKey = section.sectionKey,
      headingArgs = section.headingArgs.map(_.toString)
    )
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }
}
