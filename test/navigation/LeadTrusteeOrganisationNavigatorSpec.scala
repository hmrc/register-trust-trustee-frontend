/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.leadtrustee.organisation.mld5.{routes => mld5Rts}
import controllers.register.leadtrustee.organisation.{routes => rts}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.leadtrustee.organisation._
import pages.register.leadtrustee.organisation.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}

class LeadTrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new LeadTrusteeOrganisationNavigator
  val index = 0

  implicit val config: FrontendAppConfig = frontendAppConfig

  "LeadTrusteeOrganisation Navigator" when {

    "Name page" when {

      "Not UK registered" must {
        "-> Address UK yes no page" in {
          val answers = emptyUserAnswers
            .set(UkRegisteredYesNoPage(index), false).success.value

          navigator.nextPage(NamePage(index), fakeDraftId, answers)
            .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "UTR page -> Country Of Residence UK yes no page" in {
      navigator.nextPage(UtrPage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence UK yes no page -> No -> Country of Residence page" in {
      val answers = emptyUserAnswers
        .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

      navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence UK yes no page -> Yes -> UK Address page" in {
      val answers = emptyUserAnswers
        .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

      navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
        .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
    }

    "Country Of Residence page -> International Address page" in {
      navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, emptyUserAnswers)
        .mustBe(rts.InternationalAddressController.onPageLoad(index, fakeDraftId))
    }
  }

}
