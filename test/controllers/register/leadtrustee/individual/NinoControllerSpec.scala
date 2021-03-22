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
import controllers.register.IndexValidation
import forms.NinoFormProvider
import models._
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.register.leadtrustee.individual.{MatchingFailedPage, TrusteesNamePage, TrusteesNinoPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import services.TrustsIndividualCheckService
import views.html.register.leadtrustee.individual.NinoView

import scala.concurrent.Future

class NinoControllerSpec extends SpecBase with IndexValidation {

  val messagePrefix = "leadTrustee.individual.nino"

  val form = new NinoFormProvider()(messagePrefix)

  val index = 0

  val trusteeName: FullName = FullName("FirstName", None, "LastName")
  val validAnswer = "NH111111A"

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(TrusteesNamePage(index), trusteeName).success.value

  lazy val ninoRoute: String = routes.NinoController.onPageLoad(index, fakeDraftId).url

  "Nino Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, ninoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NinoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(TrusteesNinoPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, ninoRoute)

      val view = application.injector.instanceOf[NinoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "in 4mld mode" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(ServiceNotIn5mldModeResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator()),
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "in 5mld mode and lead trustee matching successful" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(SuccessfulMatchResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator()),
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }
    }

    "redirect to matching failed page" when {

      "1st unsuccessful match" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(UnsuccessfulMatchResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.MatchingFailedController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }

      "2nd unsuccessful match" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(UnsuccessfulMatchResponse))

        val userAnswers = baseAnswers
          .set(MatchingFailedPage(index), 1).success.value
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.MatchingFailedController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }
    }

    "redirect to matching locked page" when {
      "3rd unsuccessful match" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(UnsuccessfulMatchResponse))

        val userAnswers = baseAnswers
          .set(MatchingFailedPage(index), 2).success.value
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.MatchingLockedController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }
    }

    "redirect back to trustee name page" when {
      "issue building payload" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(IssueBuildingPayloadResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.NameController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = baseAnswers
        .set(TrusteesNinoPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, ninoRoute)
        .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[NinoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, ninoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, ninoRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
        FakeRequest(GET, routes.NinoController.onPageLoad(index, fakeDraftId).url)
      }

      validateIndex(
        arbitrary[String],
        TrusteesNinoPage.apply,
        getForIndex
      )

    }

    "for a POST" must {

      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {
        FakeRequest(POST, routes.NinoController.onPageLoad(index, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", validAnswer))
      }

      validateIndex(
        arbitrary[String],
        TrusteesNinoPage.apply,
        postForIndex
      )
    }

  }
}
