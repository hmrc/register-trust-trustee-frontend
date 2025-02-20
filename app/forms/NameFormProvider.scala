/*
 * Copyright 2025 HM Revenue & Customs
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

package forms

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings

import javax.inject.Inject
import models.core.pages.FullName
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Valid}


class NameFormProvider @Inject() extends Mappings {

  private val maxFieldCharacters = 35

  def apply(messagePrefix: String): Form[FullName] = Form(
    mapping(
      "firstName" -> text(s"$messagePrefix.error.firstName.required")
        .verifying(
          firstError(
            nonEmptyString("firstName", s"$messagePrefix.error.firstName.required"),
            regexp(Validation.individualNameRegex, s"$messagePrefix.error.firstName.invalid"),
            maxLength(maxFieldCharacters, s"$messagePrefix.error.firstName.length"),
            startsWithCapitalLetter("firstName", s"$messagePrefix.error.firstName.capitalLetter")
          )
        ),
      "middleName" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
            Constraint[String] { value: String =>
              if (value.nonEmpty) {
                firstError(
                  regexp(Validation.individualNameRegex, s"$messagePrefix.error.middleName.invalid"),
                  maxLength(maxFieldCharacters, s"$messagePrefix.error.middleName.length"),
                  startsWithCapitalLetter("middleName", s"$messagePrefix.error.middleName.capitalLetter")
                )(value)
              } else {
                Valid
              }
            }
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "lastName" -> text(s"$messagePrefix.error.lastName.required")
        .verifying(
          firstError(
            nonEmptyString("lastName", s"$messagePrefix.error.lastName.required"),
            regexp(Validation.individualNameRegex, s"$messagePrefix.error.lastName.invalid"),
            maxLength(maxFieldCharacters, s"$messagePrefix.error.lastName.length"),
            startsWithCapitalLetter("lastName", s"$messagePrefix.error.lastName.capitalLetter")
          )
        )
    )(FullName.apply)(FullName.unapply)
  )
}

