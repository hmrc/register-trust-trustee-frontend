/*
 * Copyright 2021 HM Revenue & Customs
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

import forms.YesNoFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.leadtrustee.individual.NinoYesNoView

class NinoYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "leadTrustee.individual.ninoYesNo"

  val index = 0

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix("leadTrustee.individual.ninoYesNo")

  val name: String = FullName("FirstName", None, "LastName").toString

  "NinoYesNoView" when {

    "not read-only" must {

      val view = viewFor[NinoYesNoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, disabled = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(name))

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithoutDisabledInput(applyView(form))
    }

    "read-only" must {

      val view = viewFor[NinoYesNoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, disabled = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(name))

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithDisabledInput(applyView(form))
    }
  }
}
