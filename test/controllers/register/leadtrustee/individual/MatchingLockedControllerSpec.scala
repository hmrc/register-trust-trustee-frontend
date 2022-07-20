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

package controllers.register.leadtrustee.individual

import base.SpecBase
import models.UserAnswers
import models.core.pages.FullName
import models.registration.pages.DetailsChoice._
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.register.leadtrustee.individual._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.leadtrustee.individual.MatchingLockedView

import scala.concurrent.Future

class MatchingLockedControllerSpec extends SpecBase {

  private val index: Int = 0

  private val name: FullName = FullName("Joe", None, "Bloggs")

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(TrusteesNamePage(index), name).success.value
    .set(TrusteeNinoYesNoPage(index), true).success.value
    .set(TrusteesNinoPage(index), "AA000000A").success.value

  "MatchingLockedController" when {

    ".onPageLoad" when {

      lazy val onPageLoadRoute = routes.MatchingLockedController.onPageLoad(index, fakeDraftId).url

      "existing data found" must {
        "return OK and the correct view for a GET" in {

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, onPageLoadRoute)

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

          val request = FakeRequest(GET, onPageLoadRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

          application.stop()
        }
      }
    }

    ".continueWithPassport" when {

      lazy val continueWithPassportRoute = routes.MatchingLockedController.continueWithPassport(index, fakeDraftId).url

      "existing data found" must {

        "amend user answers and redirect to passport page" in {

          reset(registrationsRepository)
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, continueWithPassportRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.PassportDetailsController.onPageLoad(index, fakeDraftId).url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

          uaCaptor.getValue.get(TrusteeNinoYesNoPage(index)).get mustBe false
          uaCaptor.getValue.get(TrusteesNinoPage(index)) mustBe None
          uaCaptor.getValue.get(TrusteeDetailsChoicePage(index)).get mustBe Passport

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(GET, continueWithPassportRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

          application.stop()
        }
      }
    }

    ".continueWithIdCard" when {

      lazy val continueWithIdCardRoute = routes.MatchingLockedController.continueWithIdCard(index, fakeDraftId).url

      "existing data found" must {

        "amend user answers and redirect to id card page" in {

          reset(registrationsRepository)
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))

          val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

          val request = FakeRequest(GET, continueWithIdCardRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.IDCardDetailsController.onPageLoad(index, fakeDraftId).url

          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

          uaCaptor.getValue.get(TrusteeNinoYesNoPage(index)).get mustBe false
          uaCaptor.getValue.get(TrusteesNinoPage(index)) mustBe None
          uaCaptor.getValue.get(TrusteeDetailsChoicePage(index)).get mustBe IdCard

          application.stop()
        }
      }

      "no existing data found" must {
        "redirect to Session Expired" in {

          val application = applicationBuilder(userAnswers = None).build()

          val request = FakeRequest(GET, continueWithIdCardRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

          application.stop()
        }
      }
    }
  }
}
