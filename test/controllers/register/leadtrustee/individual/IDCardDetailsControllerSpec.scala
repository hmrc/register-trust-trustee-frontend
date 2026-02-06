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

package controllers.register.leadtrustee.individual

import base.SpecBase
import config.annotations.LeadTrusteeIndividual
import forms.PassportOrIdCardFormProvider
import models.core.pages.FullName
import models.registration.pages.PassportOrIdCardDetails
import navigation.{FakeNavigator, Navigator}
import pages.register.leadtrustee.individual.{IDCardDetailsPage, TrusteesNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptions
import views.html.register.leadtrustee.individual.IDCardDetailsView

import java.time.LocalDate

class IDCardDetailsControllerSpec extends SpecBase {

  val messagePrefix                        = "leadTrustee.individual.iDCardDetails"
  val formProvider                         = new PassportOrIdCardFormProvider(frontendAppConfig)
  val form: Form[PassportOrIdCardDetails]  = formProvider(messagePrefix)
  val cardDetails: PassportOrIdCardDetails = PassportOrIdCardDetails("UK", "0987654321234", LocalDate.now())

  val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options()

  val index                 = 0
  val trusteeName: FullName = FullName("FirstName", None, "LastName")

  lazy val idCardDetailsRoute: String = routes.IDCardDetailsController.onPageLoad(index, fakeDraftId).url

  "IDCardDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), trusteeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, idCardDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[IDCardDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, fakeDraftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value
        .set(IDCardDetailsPage(index), cardDetails)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, idCardDetailsRoute)

      val view = application.injector.instanceOf[IDCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(cardDetails), countryOptions, fakeDraftId, index, trusteeName.toString)(
          request,
          messages
        ).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value
        .set(IDCardDetailsPage(index), cardDetails)
        .success
        .value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator())
          )
          .build()

      val request =
        FakeRequest(POST, idCardDetailsRoute)
          .withFormUrlEncodedBody(
            "country"          -> "country",
            "number"           -> "123456",
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
        .set(TrusteesNamePage(index), FullName("FirstName", None, "LastName"))
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, idCardDetailsRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[IDCardDetailsView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, fakeDraftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, idCardDetailsRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, idCardDetailsRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
