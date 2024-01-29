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

package views.register.leadtrustee.organisation

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.leadtrustee.organisation.EmailAddressYesNoView

class EmailAddressYesNoViewSpec extends YesNoViewBehaviours {

  val prefix = "leadTrustee.organisation.emailYesNo"
  val fakeName = "Test"
  val index = 0

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(prefix)

  "EmailAddressYesNo View" must {

    val view = viewFor[EmailAddressYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, fakeName)(fakeRequest, messages)

    behave like normalPage(applyView(form), prefix)

    behave like pageWithTitle(applyView(form), prefix, fakeName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, prefix, None, Seq(fakeName))

    behave like pageWithASubmitButton(applyView(form))
  }
}
