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

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.register.leadtrustee.individual.MatchingFailedView

class MatchingFailedViewSpec extends ViewBehaviours {

  val prefix = "leadTrustee.individual.matching.failed"
  val index = 0
  val numberOfFailedAttempts: Int = 1
  val view: MatchingFailedView = viewFor[MatchingFailedView](Some(emptyUserAnswers))

  def applyView: HtmlFormat.Appendable =
    view.apply(fakeDraftId, index, numberOfFailedAttempts)(fakeRequest, messages)

  "FailedMatching View" must {

    behave like normalPageTitleWithCaption(
      view = applyView,
      messageKeyPrefix = prefix,
      messageKeyParam = "",
      captionParam = numberOfFailedAttempts.toString,
      expectedGuidanceKeys = "paragraph1"
    )

    "show number of remaining attempts" in {
      val doc = asDocument(applyView)

      assertContainsText(doc, "You have 2 attempts left to enter the correct details.")
    }

    behave like pageWithASubmitButton(applyView)

  }
}
