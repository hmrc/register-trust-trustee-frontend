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

package views.register.leadtrustee.individual

import forms.DateFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.leadtrustee.individual.DateOfBirthView

import java.time.LocalDate

class DateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "leadTrustee.individual.dateOfBirth"
  val name: String = FullName("FirstName", None, "LastName").toString
  val index = 0

  val form: Form[LocalDate] = new DateFormProvider(frontendAppConfig).withConfig(messageKeyPrefix)

  "DateOfBirthView" when {

    "not read-only" must {

      val view = viewFor[DateOfBirthView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, readOnly = false)(fakeRequest, messages)

      val applyViewF = (form : Form[_]) => applyView(form)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithGuidance(applyView(form), messageKeyPrefix, "hint")

      behave like pageWithBackLink(applyView(form))

      behave like pageWithDateFields(form, applyViewF,
        messageKeyPrefix,
        "value",
        name
      )

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithoutReadOnlyInput(applyView(form))
    }

    "read-only" must {

      val view = viewFor[DateOfBirthView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, readOnly = true)(fakeRequest, messages)

      val applyViewF = (form : Form[_]) => applyView(form)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithGuidance(applyView(form), messageKeyPrefix, "hint")

      behave like pageWithBackLink(applyView(form))

      behave like pageWithDateFields(form, applyViewF,
        messageKeyPrefix,
        "value",
        name
      )

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithReadOnlyInput(applyView(form))
    }
  }
}
