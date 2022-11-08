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

package controllers.register.leadtrustee.individual

import base.SpecBase
import config.annotations.LeadTrusteeIndividual
import models.UserAnswers
import navigation.{FakeNavigator, Navigator}
import org.scalatest.concurrent.ScalaFutures
import pages.register.trustees.organisation.NamePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.LeadTrusteeIndividualPrintHelper
import viewmodels.AnswerSection
import views.html.InternalServerErrorPageView
import views.html.register.leadtrustee.individual.CheckDetailsView

class CheckDetailsControllerSpec extends SpecBase with ScalaFutures {

  private val index: Int = 0
  private val name = "Test"

  private lazy val checkDetailsRoute = routes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  override val emptyUserAnswers: UserAnswers = super.emptyUserAnswers
    .set(NamePage(index), name).success.value

  "CheckDetails Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, checkDetailsRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckDetailsView]
      val printHelper = application.injector.instanceOf[LeadTrusteeIndividualPrintHelper]
      val answerSection: AnswerSection = printHelper.checkDetailsSection(emptyUserAnswers, name, index, fakeDraftId)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(answerSection, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val onSubmitPath = routes.CheckDetailsController.onSubmit(index, draftId).url

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator)
        )
        .build()

      val request =
        FakeRequest(POST, onSubmitPath)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return an Internal Server Error and redirect to error page when set user answers operation fails" in {

      val differentIndex: Int = index + 2
      val onSubmitPath = routes.CheckDetailsController.onSubmit(differentIndex, draftId).url

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[LeadTrusteeIndividual]).toInstance(new FakeNavigator)
        )
        .build()

      val errorPage = application.injector.instanceOf[InternalServerErrorPageView]

      val request =
        FakeRequest(POST, onSubmitPath)

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR
      contentType(result) mustBe Some("text/html")
      contentAsString(result) mustEqual errorPage()(request, messages).toString

      application.stop()
    }

  }
}
