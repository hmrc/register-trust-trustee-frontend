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

package views.register.leadtrustee.individual

import forms.UKAddressFormProvider
import models.core.pages.{FullName, UKAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.register.leadtrustee.individual.UkAddressView

class UkAddressViewSpec extends UkAddressViewBehaviours {

  val messageKeyPrefix = "leadTrustee.individual.ukAddress"
  val name: FullName = FullName("First", Some("Middle"), "Last")
  val index = 0

  override val form: Form[UKAddress] = new UKAddressFormProvider().apply()

  "UkAddressView" must {

    val view = viewFor[UkAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, name.toString, index, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like ukAddressPage(
      applyView,
      Some(messageKeyPrefix),
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
