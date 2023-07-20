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

import forms.NinoFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.leadtrustee.individual.NinoView

class NinoViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "leadTrustee.individual.nino"

  val index = 0

  val existingSettlorNinos: Seq[String] = Seq("")

  val form: Form[String] = new NinoFormProvider().apply(messageKeyPrefix, emptyUserAnswers, index, existingSettlorNinos)

  val name: String = FullName("FirstName", None, "LastName").toString

  "NinoView" when {

    "not read-only" must {

      val view = viewFor[NinoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, readOnly = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithBackLink(applyView(form))

      behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, name, Some(s"$messageKeyPrefix.hint"))

      behave like pageWithASubmitButton(applyView(form))
    }

    "read-only" must {

      val view = viewFor[NinoView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, readOnly = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithTitle(applyView(form), messageKeyPrefix, name)

      behave like pageWithBackLink(applyView(form))

      behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, name, Some(s"$messageKeyPrefix.hint"))

      behave like pageWithASubmitButton(applyView(form))
    }
  }
}
