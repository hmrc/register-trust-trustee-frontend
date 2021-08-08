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

import play.twirl.api.{Html, HtmlFormat}
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.ViewBehaviours
import views.html.register.leadtrustee.individual.CheckDetailsView

class CheckDetailsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "leadTrustee.individual.checkDetails"
  val index = 0

  val view: CheckDetailsView = viewFor[CheckDetailsView](Some(emptyUserAnswers))

  "CheckDetails view" must {

    def applyView(): HtmlFormat.Appendable =
      view.apply(AnswerSection(None, Seq()), fakeDraftId, index)(fakeRequest, messages)

    behave like normalPage(applyView(), messageKeyPrefix)

    behave like pageWithBackLink(applyView())

    behave like pageWithASubmitButton(applyView())

    "render Verified tag when row cannot be edited" in {

      val verifiedAnswerSection = AnswerSection(
        None,
        Seq(AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", Html("Answer"), None, "Name", canEdit = false))
      )

      val doc = asDocument(view(verifiedAnswerSection, fakeDraftId, index)(fakeRequest, messages))

      assertContainsText(doc, messages("site.verified"))
      assertContainsText(doc, messages("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", "Name"))
    }

    "not render Verified tag when row can be edited" in {

      val verifiedAnswerSection = AnswerSection(
        None,
        Seq(AnswerRow("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", Html("Answer"), None, "Name"))
      )

      val doc = asDocument(view(verifiedAnswerSection, fakeDraftId, index)(fakeRequest, messages))

      assertDoesNotContainText(doc, messages("site.verified"))
      assertContainsText(doc, messages("leadTrustee.individual.ninoYesNo.checkYourAnswersLabel", "Name"))
    }
  }
}
