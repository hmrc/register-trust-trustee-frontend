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

package utils.print

import java.time.LocalDate

import com.google.inject.Inject
import models.UserAnswers
import models.core.pages.{Address, FullName}
import models.registration.pages.PassportOrIdCardDetails
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()() {

  def bind(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)
          (implicit messages: Messages): Bound = new Bound(userAnswers, name, countryOptions)

  class Bound(userAnswers: UserAnswers, name: String, countryOptions: CountryOptions)(implicit messages: Messages) {

    def nameQuestion(query: Gettable[FullName],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x.fullName),
          Some(changeUrl)
        )
      }
    }

    def stringQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x),
          Some(changeUrl),
          name
        )
      }
    }

    def intQuestion(query: Gettable[Int],
                       labelKey: String,
                       changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x.toString),
          Some(changeUrl),
          name
        )
      }
    }

    def yesNoQuestion(query: Gettable[Boolean],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          yesOrNo(x),
          Some(changeUrl),
          name
        )
      }
    }

    def dateQuestion(query: Gettable[LocalDate],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(x.format(dateFormatter)),
          Some(changeUrl),
          name
        )
      }
    }

    def ninoQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(formatNino(x)),
          Some(changeUrl),
          name
        )
      }
    }

    def addressQuestion[T <: Address](query: Gettable[T],
                                      labelKey: String,
                                      changeUrl: String)
                                     (implicit reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          addressFormatter(x, countryOptions),
          Some(changeUrl),
          name
        )
      }
    }

    def passportDetailsQuestion(query: Gettable[PassportOrIdCardDetails],
                                labelKey: String,
                                changeUrl: String): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          passportOrIDCard(x, countryOptions),
          Some(changeUrl),
          name
        )
      }
    }

    def enumQuestion[T](query: Gettable[T],
                        labelKey: String,
                        changeUrl: String,
                        enumPrefix: String
                       )(implicit messages:Messages, reads: Reads[T]): Option[AnswerRow] = {
      userAnswers.get(query) map { x =>
        AnswerRow(
          s"$labelKey.checkYourAnswersLabel",
          HtmlFormat.escape(messages(s"$enumPrefix.$x")),
          Some(changeUrl),
          name
        )
      }
    }
  }
}
