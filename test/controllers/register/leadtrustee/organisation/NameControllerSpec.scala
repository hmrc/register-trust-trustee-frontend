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

package controllers.register.leadtrustee.organisation

import base.SpecBase
import config.annotations.LeadTrusteeOrganisation
import controllers.register.IndexValidation
import forms.StringFormProvider
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.register.leadtrustee.organisation.{NamePage, UkRegisteredYesNoPage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import services.FeatureFlagService
import views.html.register.leadtrustee.organisation.NameView

import scala.concurrent.Future

class NameControllerSpec extends SpecBase  with IndexValidation {

  val formProvider = new StringFormProvider()
  val form: Form[String] = formProvider.withConfig("trustee.organisation.name", 56)

  val index = 0
  val validAnswer = "Name"

  lazy val nameRoute: String = routes.NameController.onPageLoad(index, fakeDraftId).url

  "Name Controller" when {

    "UK registered" must {

      val isUkRegistered: Boolean = true

      val baseAnswers: UserAnswers = super.emptyUserAnswers
        .set(UkRegisteredYesNoPage(index), isUkRegistered).success.value

      returnOkAndCorrectViewForAGet(isUkRegistered, baseAnswers)
      populateViewCorrectlyOnGetWhenQuestionPreviouslyAnswered(isUkRegistered, baseAnswers)
      redirectToNextPageWhenValidDataSubmitted(baseAnswers)
      returnBadRequestAndErrorsWhenInvalidDataSubmitted(isUkRegistered, baseAnswers)
      redirectToSessionExpiredForAGetIfNoExistingDataFound()
      redirectToSessionExpiredForAPostIfNoExistingDataFound()
    }

    "Not UK registered" must {

      val isUkRegistered: Boolean = false

      val baseAnswers: UserAnswers = super.emptyUserAnswers
        .set(UkRegisteredYesNoPage(index), isUkRegistered).success.value

      returnOkAndCorrectViewForAGet(isUkRegistered, baseAnswers)
      populateViewCorrectlyOnGetWhenQuestionPreviouslyAnswered(isUkRegistered, baseAnswers)
      redirectToNextPageWhenValidDataSubmitted(baseAnswers)
      returnBadRequestAndErrorsWhenInvalidDataSubmitted(isUkRegistered, baseAnswers)
      redirectToSessionExpiredForAGetIfNoExistingDataFound()
      redirectToSessionExpiredForAPostIfNoExistingDataFound()
    }
  }

  private def returnOkAndCorrectViewForAGet(isUkRegistered: Boolean, baseAnswers: UserAnswers): Unit = {
    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, nameRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NameView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, isUkRegistered)(request, messages).toString

      application.stop()
    }
  }

  private def populateViewCorrectlyOnGetWhenQuestionPreviouslyAnswered(isUkRegistered: Boolean, baseAnswers: UserAnswers): Unit = {
    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(NamePage(index), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, nameRoute)

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, index, isUkRegistered)(request, messages).toString

      application.stop()
    }
  }

  private def redirectToNextPageWhenValidDataSubmitted(baseAnswers: UserAnswers): Unit = {
    "redirect to the next page when valid data is submitted" in {

      val mockFeatureFlagService = mock[FeatureFlagService]

      when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[FeatureFlagService].toInstance(mockFeatureFlagService),
            bind[Navigator].qualifiedWith(classOf[LeadTrusteeOrganisation]).toInstance(new FakeNavigator())
          ).build()

      val request =
        FakeRequest(POST, nameRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }
  }

  private def returnBadRequestAndErrorsWhenInvalidDataSubmitted(isUkRegistered: Boolean, baseAnswers: UserAnswers): Unit = {
    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, nameRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[NameView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, isUkRegistered)(request, messages).toString

      application.stop()
    }
  }

  private def redirectToSessionExpiredForAGetIfNoExistingDataFound(): Unit = {
    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, nameRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }

  private def redirectToSessionExpiredForAPostIfNoExistingDataFound(): Unit = {
    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, nameRoute)
          .withFormUrlEncodedBody(("value", validAnswer))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
