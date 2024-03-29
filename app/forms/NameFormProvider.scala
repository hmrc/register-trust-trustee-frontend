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

package forms

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings

import javax.inject.Inject
import models.core.pages.FullName
import play.api.data.Form
import play.api.data.Forms._


class NameFormProvider @Inject() extends Mappings {

  def apply(messagePrefix: String): Form[FullName] = Form(
    mapping(
      "firstName" -> text(s"$messagePrefix.error.firstnamerequired")
        .verifying(
          firstError(
            maxLength(35, s"$messagePrefix.error.lengthfirstname"),
            nonEmptyString("firstName", s"$messagePrefix.error.firstnamerequired"),
            regexp(Validation.nameRegex, s"$messagePrefix.error.invalidFirstNameCharacters")
          )
        ),
      "middleName" -> optional(text()
        .transform(trimWhitespace, identity[String])
        .verifying(
          firstError(
            maxLength(35, s"$messagePrefix.error.lengthmiddlename"),
            regexp(Validation.nameRegex, s"$messagePrefix.error.invalidMiddleNameCharacters"))
        )
      ).transform(emptyToNone, identity[Option[String]]),
      "lastName" -> text(s"$messagePrefix.error.lastnamerequired")
        .verifying(
          firstError(
            maxLength(35, s"$messagePrefix.error.lengthlastname"),
            nonEmptyString("lastName", s"$messagePrefix.error.lastnamerequired"),
            regexp(Validation.nameRegex, s"$messagePrefix.error.invalidLastNameCharacters")
          )
        )
    )(FullName.apply)(FullName.unapply)
  )
}

