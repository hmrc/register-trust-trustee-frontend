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

package controllers.register

import base.SpecBase
import controllers.register.routes.RemoveIndexController
import controllers.register.trustees.individual.routes.CheckDetailsController
import forms.{AddATrusteeFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.UserAnswers
import models.core.pages.TrusteeOrLeadTrustee._
import models.core.pages.{FullName, IndividualOrBusiness}
import models.registration.pages.AddATrustee
import models.registration.pages.AddATrustee.YesNow
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.entitystatus.TrusteeStatus
import pages.register.trustees.individual.NamePage
import pages.register.leadtrustee.{individual => ltind}
import pages.register.{AddATrusteePage, TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.AddATrusteeViewHelper
import viewmodels.AddRow
import views.html.register.{AddATrusteeView, AddATrusteeYesNoView, MaxedOutView}

import scala.concurrent.Future

class AddATrusteeControllerSpec extends SpecBase {

  private lazy val getRoute : String = routes.AddATrusteeController.onPageLoad(fakeDraftId).url
  private lazy val submitAnotherRoute : String = routes.AddATrusteeController.submitAnother(fakeDraftId).url
  private lazy val submitLeadRoute : String = routes.AddATrusteeController.submitLead(fakeDraftId).url
  private lazy val submitYesNoRoute : String = routes.AddATrusteeController.submitOne(fakeDraftId).url
  private lazy val submitCompleteRoute : String = routes.AddATrusteeController.submitComplete(fakeDraftId).url

  private def changeLink(index: Int): String = CheckDetailsController.onPageLoad(index, fakeDraftId).url
  private def removeLink(index: Int): String = RemoveIndexController.onPageLoad(index, fakeDraftId).url

  private val addTrusteeForm = new AddATrusteeFormProvider()()
  private val yesNoForm: Form[Boolean] = new YesNoFormProvider().withPrefix("addATrusteeYesNo")

  private lazy val trustee = List(
    AddRow("First 0 Last 0", typeLabel = "Trustee Individual", changeLink(0), removeLink(0)),
    AddRow("First 1 Last 1", typeLabel = "Trustee Individual", changeLink(1), removeLink(1))
  )

  private val userAnswersWithTrusteesComplete: UserAnswers = emptyUserAnswers
    .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
    .set(TrusteeIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
    .set(NamePage(0), FullName("First 0", None, "Last 0")).success.value
    .set(TrusteeStatus(0), Completed).success.value

    .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
    .set(TrusteeIndividualOrBusinessPage(1), IndividualOrBusiness.Individual).success.value
    .set(NamePage(1), FullName("First 1", None, "Last 1")).success.value
    .set(TrusteeStatus(1), Completed).success.value

  private def generateTrustees(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) =>
        ua
          .set(TrusteeOrLeadTrusteePage(index), Trustee).success.value
          .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
          .set(NamePage(index), FullName(s"First $index", None, s"Last $index")).success.value
          .set(TrusteeStatus(index), Completed).success.value
      )
  }

  "AddATrustee Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(yesNoForm, fakeDraftId)(request, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, submitYesNoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId)(request, messages).toString

        application.stop()
      }
    }

    "there are trustees" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(
            form = addTrusteeForm,
            onSubmit = routes.AddATrusteeController.submitAnother(fakeDraftId),
            inProgressTrustees = Nil,
            completeTrustees = trustee,
            isLeadTrusteeDefined = false,
            heading = "You have added 2 trustees"
          )(request, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithTrusteesComplete)).build()

        val request =
          FakeRequest(POST, submitAnotherRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            form = boundForm,
            onSubmit = routes.AddATrusteeController.submitAnother(fakeDraftId),
            inProgressTrustees = Nil,
            completeTrustees = trustee,
            isLeadTrusteeDefined = false,
            heading = "You have added 2 trustees"
          )(request, messages).toString

        application.stop()
      }

    }

    "there are 24 trustees and no lead trustee" must {

      val numberOfTrustees = 24

      val userAnswers = generateTrustees(numberOfTrustees)

      lazy val trusteeRows = new AddATrusteeViewHelper(userAnswers, fakeDraftId).rows

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddATrusteeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(
            form = addTrusteeForm,
            onSubmit = routes.AddATrusteeController.submitLead(fakeDraftId),
            inProgressTrustees = trusteeRows.inProgress,
            completeTrustees = trusteeRows.complete,
            isLeadTrusteeDefined = false,
            heading = "You have added 24 trustees"
          )(request, messages).toString

        application.stop()
      }

      "redirect to Is Lead Trustee an Individual or Business page when YesNow is submitted" in {

        reset(registrationsRepository)
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))

        val index = 24

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, submitLeadRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.YesNow.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.TrusteeIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(AddATrusteePage).get mustBe YesNow
        uaCaptor.getValue.get(TrusteeOrLeadTrusteePage(index)).get mustBe LeadTrustee

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, submitLeadRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addTrusteeForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddATrusteeView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            form = boundForm,
            onSubmit = routes.AddATrusteeController.submitLead(fakeDraftId),
            inProgressTrustees = trusteeRows.inProgress,
            completeTrustees = trusteeRows.complete,
            isLeadTrusteeDefined = false,
            heading = "You have added 24 trustees"
          )(request, messages).toString

        application.stop()
      }
    }

    "there are 25 trustees total (including the lead trustee)" must {

      val numberOfTrustees = 24
      val index = numberOfTrustees

      val baseAnswers = generateTrustees(numberOfTrustees)

      val userAnswers = baseAnswers
        .set(TrusteeOrLeadTrusteePage(index), LeadTrustee).success.value
        .set(TrusteeIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(ltind.TrusteesNamePage(index), FullName(s"First $index", None, s"Last $index")).success.value
        .set(TrusteeStatus(index), Completed).success.value

      lazy val trusteeRows = new AddATrusteeViewHelper(userAnswers, fakeDraftId).rows

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MaxedOutView]

        status(result) mustEqual OK

        val content = contentAsString(result)

        content mustEqual
          view(
            draftId = fakeDraftId,
            inProgressTrustees = trusteeRows.inProgress,
            completeTrustees = trusteeRows.complete,
            heading = "You have added 25 trustees"
          )(request, messages).toString

        content must include("You cannot add another trustee as you have entered a maximum of 25.")
        content must include("You can add another trustee by removing an existing one, or write to HMRC with details of any additional trustees.")

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, submitCompleteRoute)
            .withFormUrlEncodedBody(("value", AddATrustee.YesNow.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }
    }
  }
}
