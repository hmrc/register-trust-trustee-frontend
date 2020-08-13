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

package controllers.register.trustees.organisation

import base.SpecBase
import config.annotations.TrusteeOrganisation
import controllers.register.IndexValidation
import forms.InternationalAddressFormProvider
import models.core.pages.InternationalAddress
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import pages.register.trustees.organisation.{TrusteeOrgAddressInternationalPage, TrusteeOrgAddressUkYesNoPage, TrusteeOrgNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.register.trustees.organisation.TrusteeOrgAddressInternationalView

class TrusteeOrgAddressInternationalControllerSpec extends SpecBase with IndexValidation {

  val formProvider = new InternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider()

  val index = 0
  val orgName = "Test"

  private lazy val trusteeOrgAddressInternationalRoute = routes.TrusteeOrgAddressInternationalController.onPageLoad(index, fakeDraftId).url
  private lazy val trusteeOrgAddressInternationalPOST = routes.TrusteeOrgAddressInternationalController.onSubmit(index, fakeDraftId).url

  "TrusteeOrgAddressInternational Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value
        .set(TrusteeOrgAddressInternationalPage(index), InternationalAddress("line 1", "line 2", Some("line 3"), "country")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(InternationalAddress("line 1", "line 2", Some("line 3"), "country")), countryOptions, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[TrusteeOrganisation]).toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "IN"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteeOrgNamePage(index), "Test").success.value
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[TrusteeOrgAddressInternationalView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, index, fakeDraftId, orgName)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trusteeOrgAddressInternationalRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trusteeOrgAddressInternationalPOST)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}