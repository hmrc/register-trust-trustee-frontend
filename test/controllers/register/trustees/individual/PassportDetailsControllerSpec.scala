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

import java.time.LocalDate

import base.SpecBase
import config.annotations.TrusteeIndividual
import forms.PassportOrIdCardFormProvider
import models.core.pages.FullName
import models.registration.pages.PassportOrIdCardDetails
import navigation.{FakeNavigator, Navigator}
import pages.register.trustees.individual.{NamePage, PassportDetailsPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.html.register.trustees.individual.PassportDetailsView

class PassportDetailsControllerSpec extends SpecBase {

  val trusteeMessagePrefix = "trustee.individual.passportDetails"
  val formProvider = new PassportOrIdCardFormProvider(frontendAppConfig)
  val form = formProvider(trusteeMessagePrefix)

  val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

  val passportDetails = PassportOrIdCardDetails("UK", "0987654321234", LocalDate.now())

  val index = 0
  val emptyTrusteeName = ""
  val trusteeName = FullName("FirstName", None, "LastName")

  lazy val passportDetailsRoute = routes.PassportDetailsController.onPageLoad(index, fakeDraftId).url

  "PassportDetailsYes Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[PassportDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, fakeDraftId, index, trusteeName.toString)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName).success.value
        .set(PassportDetailsPage(index), passportDetails).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val view = application.injector.instanceOf[PassportDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(passportDetails), countryOptions, fakeDraftId, index, trusteeName.toString)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName).success.value
        .set(PassportDetailsPage(index), passportDetails).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator]
              .qualifiedWith(classOf[TrusteeIndividual])
              .toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> "1",
            "expiryDate.month" -> "1",
            "expiryDate.year"  -> "1990"
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[PassportDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, fakeDraftId, index, trusteeName.toString)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, passportDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, passportDetailsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}

