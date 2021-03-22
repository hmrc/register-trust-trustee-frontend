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

import controllers.register.leadtrustee.individual.routes
import play.twirl.api.HtmlFormat
import viewmodels.Link
import views.behaviours.LinkListViewBehaviours
import views.html.register.leadtrustee.individual.MatchingLockedView

class MatchingLockedViewSpec extends LinkListViewBehaviours {

  val prefix = "leadTrustee.individual.matching.locked"
  val index = 0
  val view: MatchingLockedView = viewFor[MatchingLockedView](Some(emptyUserAnswers))

  val links: List[Link] = List(
    Link("Passport and address", routes.PassportDetailsController.onPageLoad(index, fakeDraftId).url),
    Link("ID and address", routes.IDCardDetailsController.onPageLoad(index, fakeDraftId).url)
  )

  def applyView: HtmlFormat.Appendable =
    view.apply(fakeDraftId, index)(fakeRequest, messages)

  "MatchingLockedView" must {

    behave like normalPageTitleWithCaption(
      applyView,
      prefix,
      "",
     "paragraph1", "paragraph2", "subheading1", "paragraph3", "paragraph4", "paragraph5"
    )

    behave like linkList(applyView, links)

    behave like pageWithRegProgressLink(applyView)

  }
}
