/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.YesNoFormProvider
import models.core.pages.FullName
import pages.register.trustees.IsThisLeadTrusteePage
import pages.register.trustees.individual.{AddressUkYesNoPage, NamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.trustees.individual.LiveInTheUKYesNoView

class LiveInTheUKYesNoControllerSpec extends SpecBase {

  val leadTrusteeMessagePrefix = "leadTrusteeLiveInTheUK"
  val trusteeMessagePrefix = "trusteeLiveInTheUK"
  val formProvider = new YesNoFormProvider()
  val form = formProvider.withPrefix(trusteeMessagePrefix)

  val index = 0
  val emptyTrusteeName = ""
  val trusteeName = "FirstName LastName"

  lazy val trusteeLiveInTheUKRoute = routes.LiveInTheUKYesNoController.onPageLoad(index, fakeDraftId).url

  "TrusteeLiveInTheUK Controller" must {

    "return OK and the correct view (lead trustee) for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[LiveInTheUKYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, leadTrusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "return OK and the correct view (trustee) for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[LiveInTheUKYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), true).success.value
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(AddressUkYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val view = application.injector.instanceOf[LiveInTheUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), fakeDraftId, index, leadTrusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to IsThisLeadTrustee when IsThisLeadTrustee is not answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(AddressUkYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.register.trustees.routes.IsThisLeadTrusteeController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value
        .set(AddressUkYesNoPage(index), true).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to trusteeName must"  when{

      "a GET when no name is found" in {

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(AddressUkYesNoPage(index), true).success.value

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.NameController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }
      "a POST when no name is found" in {

        val userAnswers = emptyUserAnswers
          .set(IsThisLeadTrusteePage(index), false).success.value
          .set(AddressUkYesNoPage(index), true).success.value

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, trusteeLiveInTheUKRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.NameController.onPageLoad(index, fakeDraftId).url

        application.stop()

      }
    }


    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(IsThisLeadTrusteePage(index), false).success.value
        .set(NamePage(index), FullName("FirstName", None, "LastName")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[LiveInTheUKYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, trusteeMessagePrefix, trusteeName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeLiveInTheUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeLiveInTheUKRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

