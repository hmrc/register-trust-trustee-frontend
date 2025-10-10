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

package views.register.trustees.individual

import forms.NameFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.individual.NameView

class NameViewSpec extends QuestionViewBehaviours[FullName] {

  val prefix = "trustee.individual.name"
  val index = 0
  val form: Form[FullName] = new NameFormProvider()(prefix)
  val view: NameView = viewFor[NameView](Some(emptyUserAnswers))

  "Name View" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), prefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextFields(
      form,
      applyView,
      prefix,
      Seq(("firstName", None), ("middleName", None), ("lastName", None))
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
