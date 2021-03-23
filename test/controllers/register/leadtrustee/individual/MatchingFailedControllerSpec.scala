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
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsIndividualCheckService
import views.html.register.leadtrustee.individual.MatchingFailedView

import scala.concurrent.Future

class MatchingFailedControllerSpec extends SpecBase {

  private val index: Int = 0
  private val numberOfFailedAttempts: Int = 1

  private lazy val matchingFailedRoute: String =
    routes.MatchingFailedController.onPageLoad(index, fakeDraftId).url

  private val mockService = mock[TrustsIndividualCheckService]

  "MatchingFailedController" when {

    ".onPageLoad" when {

      "successful call to retrieve number of failed matching attempts" must {
        "return OK and the correct view for a GET" in {

          when(mockService.failedAttempts(any())(any(), any()))
            .thenReturn(Future.successful(numberOfFailedAttempts))

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[TrustsIndividualCheckService].toInstance(mockService))
            .build()

          val request = FakeRequest(GET, matchingFailedRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[MatchingFailedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(fakeDraftId, index, numberOfFailedAttempts)(request, messages).toString

          application.stop()
        }
      }

      "unsuccessful call to retrieve number of failed matching attempts" must {
        "return INTERNAL_SERVER_ERROR" in {

          when(mockService.failedAttempts(any())(any(), any()))
            .thenReturn(Future.failed(new Throwable("Failed to extract session ID from header carrier.")))

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[TrustsIndividualCheckService].toInstance(mockService))
            .build()

          val request = FakeRequest(GET, matchingFailedRoute)

          val result = route(application, request).value

          status(result) mustEqual INTERNAL_SERVER_ERROR

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(GET, matchingFailedRoute)

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

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          val request = FakeRequest(POST, matchingFailedRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.NameController.onPageLoad(index, fakeDraftId).url

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(POST, matchingFailedRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

          application.stop()
        }
      }
    }
  }
}
