/*
 * Copyright 2026 HM Revenue & Customs
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

import views.behaviours.ViewBehaviours
import views.html.register.TrusteesInfoView

class TrusteesInfoViewSpec extends ViewBehaviours {

  "TrusteesInfo view" must {

    val view = viewFor[TrusteesInfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView, "trusteesInfo.5mld")

    behave like pageWithTitleAndSectionSubheading(applyView, "trusteesInfo.5mld")

    behave like pageWithGuidance(
      applyView,
      "trusteesInfo.5mld",
      "subheading1",
      "paragraph1",
      "paragraph2",
      "subheading2",
      "paragraph3",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "bulletpoint4",
      "bulletpoint5",
      "bulletpoint6",
      "bulletpoint7",
      "bulletpoint8",
      "paragraph4",
      "paragraph5",
      "paragraph6",
      "paragraph7",
      "bulletpoint9",
      "bulletpoint10",
      "bulletpoint11",
      "bulletpoint12",
      "subheading3",
      "paragraph8",
      "bulletpoint13",
      "bulletpoint14",
      "bulletpoint15",
      "bulletpoint16",
      "bulletpoint17",
      "bulletpoint18",
      "paragraph9",
      "details",
      "details.subheading1",
      "details.paragraph1",
      "details.subheading2",
      "details.paragraph2"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithWarning(applyView)

    behave like pageWithASubmitButton(applyView)
  }

}
