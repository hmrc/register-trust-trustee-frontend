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

package navigation

import controllers.register.trustees.organisation.routes._
import base.SpecBase
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.trustees.organisation._

class TrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new TrusteeOrganisationNavigator
  val index = 0

  "TrusteeOrganisation Navigator" must {

    "Name page -> UTR yes no page" in {
      navigator.nextPage(TrusteeOrgNamePage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(TrusteeUtrYesNoController.onPageLoad(index, fakeDraftId))
    }

    "UTR yes no page -> YES -> UTR page" in {
      val answers = emptyUserAnswers
        .set(TrusteeUtrYesNoPage(index), true).success.value

      navigator.nextPage(TrusteeUtrYesNoPage(index), fakeDraftId, answers)
        .mustBe(TrusteeUtrController.onPageLoad(index, fakeDraftId))
    }

    "UTR yes no page -> NO -> Do you know address page" ignore {
      val answers = emptyUserAnswers
        .set(TrusteeUtrYesNoPage(index), false).success.value

      navigator.nextPage(TrusteeUtrYesNoPage(index), fakeDraftId, answers)
        .mustBe(???)
    }

    "UTR page -> Check your answers page" ignore {
      navigator.nextPage(TrusteesUtrPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(???)
    }

    "Do you know address page -> YES -> Is address in UK page" ignore {
      val answers = emptyUserAnswers
        .set(???, true).success.value

      navigator.nextPage(???, fakeDraftId, answers)
        .mustBe(???)
    }

    "Do you know address page -> NO -> Check your answers page" ignore {
      val answers = emptyUserAnswers
        .set(???, true).success.value

      navigator.nextPage(???, fakeDraftId, answers)
        .mustBe(???)
    }

    "Is address in UK page -> YES -> UK address page" ignore {
      val answers = emptyUserAnswers
        .set(TrusteeOrgAddressUkYesNoPage(index), true).success.value

      navigator.nextPage(TrusteeOrgAddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(TrusteesOrgUkAddressController.onPageLoad(index, fakeDraftId))
    }

    "Is address in UK page -> NO -> International address page" ignore {
      val answers = emptyUserAnswers
        .set(TrusteeOrgAddressUkYesNoPage(index), false).success.value

      navigator.nextPage(TrusteeOrgAddressUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(TrusteeOrgAddressInternationalController.onPageLoad(index, fakeDraftId))
    }

    "UK address page -> Check your answers page" ignore {
      navigator.nextPage(TrusteeOrgAddressUkPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(???)
    }

    "International address page -> Check your answers page" ignore {
      navigator.nextPage(TrusteeOrgAddressInternationalPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(???)
    }
  }

}
