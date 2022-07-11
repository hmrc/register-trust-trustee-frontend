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
import config.annotations.LeadTrusteeIndividual
import controllers.register.IndexValidation
import forms.NinoFormProvider
import models._
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import pages.register.leadtrustee.individual.{MatchedYesNoPage, TrusteesNamePage, TrusteesNinoPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import services.TrustsIndividualCheckService
import views.html.register.leadtrustee.individual.NinoView

import scala.concurrent.Future

class NinoControllerSpec extends SpecBase with IndexValidation with BeforeAndAfterEach {

  val messagePrefix = "leadTrustee.individual.nino"

  val index = 0

  val form = new NinoFormProvider()(messagePrefix, emptyUserAnswers, index)

  val trusteeName: FullName = FullName("FirstName", None, "LastName")
  val validAnswer = "NH111111A"

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(TrusteesNamePage(index), trusteeName).success.value

  lazy val ninoRoute: String = routes.NinoController.onPageLoad(index, fakeDraftId).url

  override def beforeEach(): Unit = {
    reset(registrationsRepository)
    when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
  }

  "Nino Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, ninoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NinoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeName.toString, readOnly = false)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET when lead trustee matched" in {

      val userAnswers = baseAnswers
        .set(MatchedYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, ninoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NinoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeName.toString, readOnly = true)(request, messages).toString

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
        view(form.fill(validAnswer), fakeDraftId, index, trusteeName.toString, readOnly = false)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {
      "SuccessfulMatchResponse" in {

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

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe true

        application.stop()
      }
    }

    "redirect to matching failed page" when {

      "UnsuccessfulMatchResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(UnsuccessfulMatchResponse))

        when(mockService.failedAttempts(any())(any(), any()))
          .thenReturn(Future.successful(1))

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

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe false

        application.stop()
      }
    }

    "redirect to matching locked page" when {
      "LockedMatchResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(LockedMatchResponse))

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
          routes.MatchingLockedController.onPageLoad(index, fakeDraftId).url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe false

        application.stop()
      }
    }

    "return INTERNAL_SERVER_ERROR" when {

      "IssueBuildingPayloadResponse" in {

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

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe false

        application.stop()
      }

      "ServiceUnavailableErrorResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(ServiceUnavailableErrorResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe false

        application.stop()
      }

      "TechnicalDifficultiesErrorResponse" in {

        val mockService = mock[TrustsIndividualCheckService]

        when(mockService.matchLeadTrustee(any(), any())(any(), any()))
          .thenReturn(Future.successful(TechnicalDifficultiesErrorResponse))

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsIndividualCheckService].toInstance(mockService)
          ).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(MatchedYesNoPage(index)).get mustBe false

        application.stop()
      }
    }

    "return a Bad Request and errors" when {
      "invalid data is submitted" in {

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
          view(boundForm, fakeDraftId, index, trusteeName.toString, readOnly = false)(request, messages).toString

        application.stop()
      }

      "duplicate nino is submitted" in {

        val userAnswers = baseAnswers
          .set(TrusteesNinoPage(index + 1), validAnswer).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(POST, ninoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

        val boundForm = form
          .bind(Map("value" -> validAnswer))
          .withError("value", "leadTrustee.individual.nino.error.duplicate")

        val view = application.injector.instanceOf[NinoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, index, trusteeName.toString, readOnly = false)(request, messages).toString

        application.stop()
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, ninoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, ninoRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routes.SessionExpiredController.onPageLoad.url

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
