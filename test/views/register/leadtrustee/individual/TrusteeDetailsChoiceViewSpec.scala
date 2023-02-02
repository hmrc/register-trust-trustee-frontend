/*
 * Copyright 2023 HM Revenue & Customs
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

import forms.DetailsChoiceFormProvider
import models.core.pages.FullName
import models.registration.pages.DetailsChoice
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.register.leadtrustee.individual.TrusteeDetailsChoiceView

class TrusteeDetailsChoiceViewSpec extends OptionsViewBehaviours {

  private val messageKeyPrefix = "leadTrustee.individual.trusteeDetailsChoice"

  private val name: FullName = FullName("First", None, "last")
  val index = 0

  lazy val form: Form[DetailsChoice] = injector.instanceOf[DetailsChoiceFormProvider].withPrefix(messageKeyPrefix)

  private val additionalContent: String =
    "If you do not have these details to hand, you can return to this page to complete these details at another stage. These details will be saved for 28 days."

  "TrusteeDetailsChoiceView" when {

    val view = viewFor[TrusteeDetailsChoiceView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, name.toString)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithOptions(form, applyView, DetailsChoice.options)

    behave like pageWithASubmitButton(applyView(form))

    "show additional content" in {
      val doc = asDocument(applyView(form))
      assertContainsText(doc, additionalContent)
    }
  }
}
