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

package views.register.trustees.individual.mld5

import forms.YesNoDontKnowFormProvider
import models.YesNoDontKnow
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioOption
import views.behaviours.{OptionsViewBehaviours, QuestionViewBehaviours}
import views.html.register.trustees.individual.mld5.MentalCapacityYesNoView

class MentalCapacityYesNoViewSpec extends QuestionViewBehaviours[YesNoDontKnow] with OptionsViewBehaviours {

  val prefix = "trustee.individual.5mld.mentalCapacityYesNo"
  val index = 0
  val name: String = FullName("FirstName", None, "LastName").toString

  val form: Form[YesNoDontKnow] = new YesNoDontKnowFormProvider().withPrefix(prefix)

  "legallyIncapableYesNoView view" must {

    val view = viewFor[MentalCapacityYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, name)(fakeRequest, messages)

    behave like normalPage(applyView(form), prefix)

    behave like pageWithTitle(applyView(form), prefix, name)

    behave like pageWithGuidance(applyView(form), prefix, "p1", "p2", "bulletpoint1", "bulletpoint2", "bulletpoint3", "bulletpoint4", "p3", "p4", "heading2", "p5", "p6")

    behave like pageWithBackLink(applyView(form))

    val options = List(
      RadioOption(id = "value-yes", value = YesNoDontKnow.Yes.toString, messageKey = "site.yes"),
      RadioOption(id = "value-no", value = YesNoDontKnow.No.toString, messageKey = "site.no"),
      RadioOption(id = "value-dontKnow", value = YesNoDontKnow.DontKnow.toString, messageKey = "site.dontKnow")
    )

    behave like pageWithOptions[YesNoDontKnow](form, applyView, options)

    behave like pageWithASubmitButton(applyView(form))
  }
}