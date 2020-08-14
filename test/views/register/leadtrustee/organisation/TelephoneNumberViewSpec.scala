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

package views.register.leadtrustee.organisation

import forms.TelephoneNumberFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.leadtrustee.organisation.TelephoneNumberView

class TelephoneNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "leadTrustee.organisation.telephoneNumber"
  val fakeName = "Name"

  override val form: Form[String] = new TelephoneNumberFormProvider()(messageKeyPrefix)

  "TelephoneNumber view" must {

    val view = viewFor[TelephoneNumberView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, fakeName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, fakeName, "hint")

    behave like pageWithHint(form, applyView, messageKeyPrefix)

    behave like pageWithTextFields(form, applyView, messageKeyPrefix, Some(fakeName), "value")

    behave like pageWithBackLink(applyView(form))
  }
}
