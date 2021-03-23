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

package controllers.register.leadtrustee.individual

import base.SpecBase
import models.UserAnswers
import models.core.pages.FullName
import pages.register.leadtrustee.individual.TrusteesNamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.leadtrustee.individual.MatchingLockedView

class MatchingLockedControllerSpec extends SpecBase {

  private val index: Int = 0

  private lazy val matchingLockedRoute: String =
    routes.MatchingLockedController.onPageLoad(index, fakeDraftId).url

  private val name: FullName = FullName("Joe", None, "Bloggs")

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(TrusteesNamePage(index), name).success.value

  "MatchingLockedController" when {

    ".onPageLoad" when {

      "existing data found" must {
        "return OK and the correct view for a GET" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, matchingLockedRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[MatchingLockedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeDraftId, index, name.toString)(request, messages).toString

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(GET, matchingLockedRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

          application.stop()
        }
      }
    }
  }
}
