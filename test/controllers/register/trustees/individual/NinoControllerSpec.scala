/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.register.trustees.individual

import base.SpecBase
import config.annotations.TrusteeIndividual
import controllers.register.IndexValidation
import forms.NinoFormProvider
import models.core.pages.{FullName, IndividualOrBusiness}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.individual.{NamePage, NinoPage}
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import services.DraftRegistrationService
import views.html.register.trustees.individual.NinoView

import scala.concurrent.Future

class NinoControllerSpec extends SpecBase with IndexValidation {

  private val trusteeMessagePrefix = "trustee.individual.nino"
  private val formProvider = new NinoFormProvider()
  private val index = 0
  val existingSettlorNinos: Seq[String] = Seq("")

  private val form = formProvider(trusteeMessagePrefix, emptyUserAnswers, index, existingSettlorNinos)
  private val trusteeName = "FirstName LastName"
  private val validAnswer = "NH111111A"

  private lazy val trusteesNinoRoute = routes.NinoController.onPageLoad(index, fakeDraftId).url

  "TrusteesNino Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

      val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, trusteesNinoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NinoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(NinoPage(index), validAnswer).success.value

      val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, trusteesNinoRoute)

      val view = application.injector.instanceOf[NinoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, trusteeName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

      val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[DraftRegistrationService].toInstance(mockDraftRegistrationService),
            bind[Navigator]
              .qualifiedWith(classOf[TrusteeIndividual])
              .toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, trusteesNinoRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors" when {
      "invalid data is submitted" in {

        val userAnswers = emptyUserAnswers
          .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

        val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

        when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

        val request =
          FakeRequest(POST, trusteesNinoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[NinoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, index, trusteeName)(request, messages).toString

        application.stop()
      }

      "duplicate nino is submitted" when {
        "trustee" in {

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value
            .set(NinoPage(index + 1), validAnswer).success.value

          val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

          when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

          val application = applicationBuilder(userAnswers = Some(userAnswers)).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

          val request =
            FakeRequest(POST, trusteesNinoRoute)
              .withFormUrlEncodedBody(("value", validAnswer))

          val boundForm = form
            .bind(Map("value" -> validAnswer))
            .withError("value", "trustee.individual.nino.error.duplicate")

          val view = application.injector.instanceOf[NinoView]

          val result = route(application, request).value

          status(result) mustEqual BAD_REQUEST

          contentAsString(result) mustEqual
            view(boundForm, fakeDraftId, index, trusteeName)(request, messages).toString

          application.stop()
        }
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

      val application = applicationBuilder(userAnswers = None).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request = FakeRequest(GET, trusteesNinoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val mockDraftRegistrationService = Mockito.mock(classOf[DraftRegistrationService])

      when(mockDraftRegistrationService.retrieveSettlorNinos(any())(any())).thenReturn(Future.successful(""))

      val application = applicationBuilder(userAnswers = None).overrides(bind[DraftRegistrationService].toInstance(mockDraftRegistrationService)).build()

      val request =
        FakeRequest(POST, trusteesNinoRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "for a GET" must {

      def getForIndex(index: Int) : FakeRequest[AnyContentAsEmpty.type] = {
        val route = controllers.register.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(GET, route)
      }

      validateIndex(
        arbitrary[String],
        NinoPage.apply,
        getForIndex
      )

    }

    "for a POST" must {
      def postForIndex(index: Int): FakeRequest[AnyContentAsFormUrlEncoded] = {

        val route =
          controllers.register.routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        FakeRequest(POST, route)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.Individual.toString))
      }

      validateIndex(
        arbitrary[String],
        NinoPage.apply,
        postForIndex
      )
    }

  }
}
