/*
 * Copyright 2022 HM Revenue & Customs
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

package views.register.trustees.organisation

import forms.StringFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.trustees.organisation.NameView

class NameViewSpec extends StringViewBehaviours {

  val prefix = "trustee.organisation.name"
  val index = 0
  override val form: Form[String] = new StringFormProvider().withConfig(prefix, 56)
  val view: NameView = viewFor[NameView](Some(emptyUserAnswers))

  "Name View" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), prefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, prefix, None, controllers.register.trustees.organisation.routes.NameController.onSubmit(index, fakeDraftId).url)

    behave like pageWithASubmitButton(applyView(form))

  }
}
