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

import forms.AddATrusteeFormProvider
import models.registration.pages.AddATrustee
import play.api.data.Form
import play.api.mvc.Call
import play.api.test.Helpers.POST
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.AddATrusteeView

class AddATrusteeViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  private val form: Form[AddATrustee] = new AddATrusteeFormProvider()()
  private val messageKeyPrefix: String = "addATrustee.count"

  private val fakeAddRow: AddRow = AddRow("name", "label", "change", "remove")
  private val fakeOnSubmit: Call = Call(POST, "redirectUrl")

  private val numberInProgress = 1
  private val inProgressRows: List[AddRow] = List.fill(numberInProgress)(fakeAddRow)
  private val numberComplete = 15
  private val completeRows: List[AddRow] = List.fill(numberComplete)(fakeAddRow)
  private val total = numberInProgress + numberComplete

  "AddATrusteeView" when {

    "lead trustee defined" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view: AddATrusteeView = viewFor[AddATrusteeView](Some(emptyUserAnswers))
        view.apply(form, fakeOnSubmit, inProgressRows, completeRows, isLeadTrusteeDefined = true, s"You have added $total trustees")(fakeRequest, messages)
      }

      val view = applyView(form)

      behave like normalPage(view, messageKeyPrefix, total.toString)

      behave like pageWithTitle(view, messageKeyPrefix, total.toString)

      behave like pageWithBackLink(view)

      behave like pageWithTabularData(view, inProgressRows, completeRows)

      behave like pageWithOptions(form, applyView, AddATrustee.options)

      behave like pageWithASubmitButton(view)

      "not show lead trustee required content" in {
        val doc = asDocument(view)

        assertDoesNotContainText(doc, messages(s"addATrustee.lead-trustee.required"))
      }
    }

    "lead trustee undefined" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view: AddATrusteeView = viewFor[AddATrusteeView](Some(emptyUserAnswers))
        view.apply(form, fakeOnSubmit, inProgressRows, completeRows, isLeadTrusteeDefined = false, s"You have added $total trustees")(fakeRequest, messages)
      }

      val view = applyView(form)

      behave like normalPage(view, messageKeyPrefix, total.toString)

      behave like pageWithTitle(view, messageKeyPrefix, total.toString)

      behave like pageWithBackLink(view)

      behave like pageWithTabularData(view, inProgressRows, completeRows)

      behave like pageWithOptions(form, applyView, AddATrustee.options)

      behave like pageWithASubmitButton(view)

      "show lead trustee required content" in {
        val doc = asDocument(view)

        assertContainsText(doc, messages(s"addATrustee.lead-trustee.required"))
      }
    }
  }
}
