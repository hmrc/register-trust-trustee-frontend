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

package views.register

import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.MaxedOutView

class MaxedOutViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  private val messageKeyPrefix: String = "addATrustee"

  private val view: MaxedOutView = viewFor[MaxedOutView](Some(emptyUserAnswers))

  private val fakeAddRow: AddRow = AddRow("name", "label", "change", "remove")

  private val numberInProgress = 1
  private val inProgressRows: List[AddRow] = List.fill(numberInProgress)(fakeAddRow)
  private val numberComplete = 15
  private val completeRows: List[AddRow] = List.fill(numberComplete)(fakeAddRow)
  private val total = numberInProgress + numberComplete

  private def applyView(): HtmlFormat.Appendable =
    view.apply(fakeDraftId, inProgressRows, completeRows, s"You have added $total trustees")(fakeRequest, messages)

  "MaxedOutView" must {

    val view = applyView()

    behave like normalPage(view, s"$messageKeyPrefix.count", total.toString)

    behave like pageWithTitle(view, s"$messageKeyPrefix.count", total.toString)

    behave like pageWithBackLink(view)

    behave like pageWithTabularData(view, inProgressRows, completeRows)

    behave like pageWithASubmitButton(view)

    "show maxed out trustees content" in {
      val doc = asDocument(view)

      assertContainsText(doc, "You cannot add another trustee as you have entered a maximum of 25.")
      assertContainsText(doc, "You can add another trustee by removing an existing one, or write to HMRC with details of any additional trustees.")
    }
  }
}
