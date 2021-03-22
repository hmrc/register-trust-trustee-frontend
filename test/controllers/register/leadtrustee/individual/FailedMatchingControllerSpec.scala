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
import config.annotations.LeadTrusteeIndividual
import navigation.{FakeNavigator, Navigator}
import pages.register.leadtrustee.individual.FailedMatchingPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.leadtrustee.individual.FailedMatchingView

class FailedMatchingControllerSpec extends SpecBase {

  private val index: Int = 0
  private val numberOfFailedAttempts: Int = 1

  private lazy val failedMatchingRoute: String =
    routes.FailedMatchingController.onPageLoad(index, fakeDraftId).url

  "FailedMatching Controller" when {

    ".onPageLoad" when {

      "FailedMatchingPage populated" must {
        "return OK and the correct view for a GET" in {

          val userAnswers = emptyUserAnswers
            .set(FailedMatchingPage(index), numberOfFailedAttempts).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, failedMatchingRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[FailedMatchingView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeDraftId, index, numberOfFailedAttempts)(request, messages).toString

          application.stop()
        }
      }

      "FailedMatchingPage not populated" must {
        "return INTERNAL_SERVER_ERROR" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          val request = FakeRequest(GET, failedMatchingRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(GET, failedMatchingRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

          application.stop()
        }
      }
    }

    ".onSubmit" when {

      "existing data found" must {
        "redirect to the next page" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator())
            ).build()

          val request = FakeRequest(POST, failedMatchingRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(POST, failedMatchingRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

          application.stop()
        }
      }
    }
  }
}
