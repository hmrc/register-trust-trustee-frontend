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

package controllers.register.trustees.individual.mld5

import base.SpecBase
import config.annotations.TrusteeIndividual
import forms.YesNoDontKnowFormProvider
import models.YesNoDontKnow
import models.core.pages.FullName
import navigation.{FakeNavigator, Navigator}
import pages.register.trustees.individual.NamePage
import pages.register.trustees.individual.mld5.MentalCapacityYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.InternalServerErrorPageView
import views.html.register.trustees.individual.mld5.MentalCapacityYesNoView

class MentalCapacityYesNoControllerSpec extends SpecBase {

  private val formProvider              = new YesNoDontKnowFormProvider()
  private val form: Form[YesNoDontKnow] = formProvider.withPrefix("trustee.individual.5mld.mentalCapacityYesNo")
  private val index: Int                = 0
  private val trusteeName               = FullName("FirstName", None, "LastName")

  private lazy val mentalCapacityYesNo: String = routes.MentalCapacityYesNoController.onPageLoad(index, draftId).url

  "MentalCapacityYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, mentalCapacityYesNo)

      val result = route(application, request).value

      val view = application.injector.instanceOf[MentalCapacityYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName)
        .success
        .value
        .set(MentalCapacityYesNoPage(index), YesNoDontKnow.Yes)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, mentalCapacityYesNo)

      val view = application.injector.instanceOf[MentalCapacityYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(YesNoDontKnow.Yes), draftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[TrusteeIndividual]).toInstance(new FakeNavigator)
        )
        .build()

      val request =
        FakeRequest(POST, mentalCapacityYesNo)
          .withFormUrlEncodedBody(("value", "yes"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), trusteeName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, mentalCapacityYesNo)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[MentalCapacityYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, index, trusteeName.toString)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, mentalCapacityYesNo)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, mentalCapacityYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "return an Internal Server Error and redirect to error page when set user answers operation fails" in {
      val userAnswers         = emptyUserAnswers.set(NamePage(index), trusteeName).success.value
      val differentIndex: Int = index + 2
      val onSubmitPath        = routes.MentalCapacityYesNoController.onSubmit(differentIndex, draftId).url

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[TrusteeIndividual]).toInstance(new FakeNavigator)
        )
        .build()

      val errorPage = application.injector.instanceOf[InternalServerErrorPageView]

      val request =
        FakeRequest(POST, onSubmitPath)
          .withFormUrlEncodedBody(("value", "yes"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }
  }

}
