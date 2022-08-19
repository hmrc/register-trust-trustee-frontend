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

package controllers.register.trustees.organisation

import base.SpecBase
import config.annotations.TrusteeOrganisation
import controllers.register.IndexValidation
import forms.UtrFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import pages.register.trustees.organisation.{NamePage, UtrPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.InternalServerErrorPageView
import views.html.register.trustees.organisation.UtrView

class UtrControllerSpec extends SpecBase with IndexValidation {

  private val index = 0
  private val fakeName = "Test"
  private val validAnswer = "1234567890"

  private lazy val utrRoute: String = routes.UtrController.onPageLoad(index, fakeDraftId).url

  override val emptyUserAnswers: UserAnswers = super.emptyUserAnswers.set(NamePage(index), fakeName).success.value

  private val formProvider = new UtrFormProvider()
  private val form: Form[String] = formProvider.withConfig("trustee.organisation.utr", emptyUserAnswers, index)

  "Utr Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, utrRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[UtrView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, fakeName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(UtrPage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, utrRoute)

      val view = application.injector.instanceOf[UtrView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, fakeName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrusteeOrganisation]).toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, utrRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, utrRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[UtrView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, fakeName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, utrRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, utrRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "return an Internal Server Error and redirect to error page when set user answers operation fails" in {
      val userAnswers = emptyUserAnswers.set(NamePage(index), fakeName).success.value
      val differentIndex: Int = index + 2
      val onSubmitPath = routes.UtrController.onSubmit(differentIndex, fakeDraftId).url

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[TrusteeOrganisation]).toInstance(new FakeNavigator)
        )
        .build()

      val errorPage = application.injector.instanceOf[InternalServerErrorPageView]

      val request =
        FakeRequest(POST, onSubmitPath)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }
  }
}
