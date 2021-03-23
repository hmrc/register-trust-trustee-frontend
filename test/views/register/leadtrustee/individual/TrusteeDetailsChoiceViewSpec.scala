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

import forms.DetailsChoiceFormProvider
import models.core.pages.FullName
import models.registration.pages.DetailsChoice
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.OptionsViewBehaviours
import views.html.register.leadtrustee.individual.TrusteeDetailsChoiceView

class TrusteeDetailsChoiceViewSpec extends OptionsViewBehaviours {

  private val messageKeyPrefix = "leadTrustee.individual.trusteeDetailsChoice"

  private val name: FullName = FullName("First", None ,"last")
  val index = 0

  lazy val form: Form[DetailsChoice] = injector.instanceOf[DetailsChoiceFormProvider].withPrefix(messageKeyPrefix)

  "TrusteeDetailsChoiceView" when {

    "4mld" must {

      val is5mldEnabled: Boolean = false

      val view = viewFor[TrusteeDetailsChoiceView](Some(emptyUserAnswers.copy(is5mldEnabled = is5mldEnabled)))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name.toString, is5mldEnabled)(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithOptions(form, applyView, DetailsChoice.options(is5mldEnabled))

      behave like pageWithASubmitButton(applyView(form))
    }

    "5mld" must {

      val is5mldEnabled: Boolean = true

      val view = viewFor[TrusteeDetailsChoiceView](Some(emptyUserAnswers.copy(is5mldEnabled = is5mldEnabled)))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name.toString, is5mldEnabled)(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithOptions(form, applyView, DetailsChoice.options(is5mldEnabled))

      behave like pageWithASubmitButton(applyView(form))

      behave like pageWithRegProgressLink(applyView(form))
    }

  }
}
