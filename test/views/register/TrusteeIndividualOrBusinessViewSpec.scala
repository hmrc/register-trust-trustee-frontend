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

package views.register

import forms.IndividualOrBusinessFormProvider
import models.core.pages.IndividualOrBusiness
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.register.TrusteeIndividualOrBusinessView

class TrusteeIndividualOrBusinessViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String         = "leadTrustee.individualOrBusiness"
  private val form: Form[IndividualOrBusiness] = new IndividualOrBusinessFormProvider()(messageKeyPrefix)
  private val index                            = 0
  private val heading                          = messages(s"$messageKeyPrefix.heading")

  "TrusteeIndividualOrBusinessView" when {

    "not read-only" must {

      val view = viewFor[TrusteeIndividualOrBusinessView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, heading, disabled = false)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithoutDisabledInput(applyView(form))
    }

    "read-only" must {

      val view = viewFor[TrusteeIndividualOrBusinessView](Some(emptyUserAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, heading, disabled = true)(fakeRequest, messages)

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithDisabledInput(applyView(form))
    }
  }

}
