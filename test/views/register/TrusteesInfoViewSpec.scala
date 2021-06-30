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

package views.register

import views.behaviours.ViewBehaviours
import views.html.register.TrusteesInfoView

class TrusteesInfoViewSpec extends ViewBehaviours {

  "TrusteesInfo view" must {

    val view = viewFor[TrusteesInfoView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPageTitleWithCaption(
      view = applyView,
      messageKeyPrefix = "trusteesInfo",
      captionParam = "",
      expectedGuidanceKeys = "subheading1",
      "paragraph1",
      "paragraph2",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "paragraph3",
      "paragraph4",
      "subheading2",
      "paragraph5",
      "bulletpoint4",
      "bulletpoint5",
      "bulletpoint6",
      "bulletpoint7",
      "bulletpoint8",
      "paragraph6"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithWarning(applyView)

    behave like pageWithASubmitButton(applyView)
  }
}
