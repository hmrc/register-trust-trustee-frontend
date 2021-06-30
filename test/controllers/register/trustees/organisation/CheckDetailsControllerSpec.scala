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

package controllers.register.trustees.organisation

import base.SpecBase
import models.UserAnswers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.register.trustees.organisation.NamePage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.print.TrusteeOrganisationPrintHelper
import viewmodels.Section
import views.html.register.trustees.organisation.CheckDetailsView

class CheckDetailsControllerSpec extends SpecBase with MockitoSugar with ScalaFutures {

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
      val printHelper = application.injector.instanceOf[TrusteeOrganisationPrintHelper]
      val answerSection: Section = printHelper.checkDetailsSection(emptyUserAnswers, name, index, fakeDraftId)

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(Seq(answerSection), index, fakeDraftId)(request, messages).toString
    }

  }
}
