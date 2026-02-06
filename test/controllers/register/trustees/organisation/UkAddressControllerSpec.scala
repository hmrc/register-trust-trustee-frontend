/*
 * Copyright 2026 HM Revenue & Customs
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
import forms.UKAddressFormProvider
import models.UserAnswers
import models.core.pages.UKAddress
import navigation.{FakeNavigator, Navigator}
import pages.register.trustees.organisation.{NamePage, UkAddressPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.register.trustees.organisation.UkAddressView

class UkAddressControllerSpec extends SpecBase with IndexValidation {

  val formProvider           = new UKAddressFormProvider()
  val form: Form[UKAddress]  = formProvider()
  val index                  = 0
  val fakeName               = "Test"
  val validAnswer: UKAddress = UKAddress("value 1", "value 2", Some("value 3"), Some("value 4"), "AB1 1AB")

  lazy val ukAddressRoute: String = routes.UkAddressController.onPageLoad(index, fakeDraftId).url

  override val emptyUserAnswers: UserAnswers = super.emptyUserAnswers.set(NamePage(index), fakeName).success.value

  "UkAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, ukAddressRoute)

      val view = application.injector.instanceOf[UkAddressView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, fakeName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(UkAddressPage(index), validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, ukAddressRoute)

      val view = application.injector.instanceOf[UkAddressView]

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
          )
          .build()

      val request =
        FakeRequest(POST, ukAddressRoute)
          .withFormUrlEncodedBody(
            ("line1", validAnswer.line1),
            ("line2", validAnswer.line2),
            ("line3", validAnswer.line3.get),
            ("line4", validAnswer.line4.get),
            ("postcode", validAnswer.postcode)
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, ukAddressRoute)
          .withFormUrlEncodedBody(("line1", "invalid value"))

      val boundForm = form.bind(Map("line1" -> "invalid value"))

      val view = application.injector.instanceOf[UkAddressView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, fakeName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, ukAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, ukAddressRoute)
          .withFormUrlEncodedBody(
            ("line1", validAnswer.line1),
            ("line2", validAnswer.line2)
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
