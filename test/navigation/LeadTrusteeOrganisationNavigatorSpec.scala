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

package navigation

import base.SpecBase
import controllers.register.leadtrustee.organisation.{routes => rts}
import controllers.register.leadtrustee.organisation.nonTaxable.{routes => mld5Rts}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.leadtrustee.organisation._
import pages.register.leadtrustee.organisation.nonTaxable.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage}

class LeadTrusteeOrganisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new LeadTrusteeOrganisationNavigator
  val index = 0

  implicit val config = frontendAppConfig

  "LeadTrusteeOrganisation Navigator" when {

    "a 4mld trust" must {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      "UK registered yes no page -> YES -> Name page" in {
        val answers = baseAnswers
          .set(UkRegisteredYesNoPage(index), true).success.value

        navigator.nextPage(UkRegisteredYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.NameController.onPageLoad(index, fakeDraftId))
      }

      "UK registered yes no page -> NO -> Name page" in {
        val answers = baseAnswers
          .set(UkRegisteredYesNoPage(index), false).success.value

        navigator.nextPage(UkRegisteredYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.NameController.onPageLoad(index, fakeDraftId))
      }

      "Name page" when {

        "UK registered" must {
          "-> UTR page" in {
            val answers = baseAnswers
              .set(UkRegisteredYesNoPage(index), true).success.value

            navigator.nextPage(NamePage(index), fakeDraftId, answers)
              .mustBe(rts.UtrController.onPageLoad(index, fakeDraftId))
          }
        }

        "Not UK registered" must {
          "-> Address UK yes no page" in {
            val answers = baseAnswers
              .set(UkRegisteredYesNoPage(index), false).success.value

            navigator.nextPage(NamePage(index), fakeDraftId, answers)
              .mustBe(rts.AddressUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

        "UTR page -> Is address in UK page" in {
          navigator.nextPage(UtrPage(index), fakeDraftId, baseAnswers)
            .mustBe(rts.AddressUkYesNoController.onPageLoad(index, fakeDraftId))
        }

      "Is address in UK page -> YES -> UK address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
      }

      "Is address in UK page -> NO -> International address page" in {
        val answers = baseAnswers
          .set(AddressUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.InternationalAddressController.onPageLoad(index, fakeDraftId))
      }

      "UK address page -> Email address yes no page" in {
        navigator.nextPage(UkAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "International address page -> Email address yes no page" in {
        navigator.nextPage(InternationalAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(rts.EmailAddressYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Email address yes no page -> YES -> Email address page" in {
        val answers = baseAnswers
          .set(EmailAddressYesNoPage(index), true).success.value

        navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.EmailAddressController.onPageLoad(index, fakeDraftId))
      }

      "Email address yes no page -> NO -> Telephone number page" in {
        val answers = baseAnswers
          .set(EmailAddressYesNoPage(index), false).success.value

        navigator.nextPage(EmailAddressYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId))
      }

      "Email address page -> Telephone number page" in {
        navigator.nextPage(EmailAddressPage(index), fakeDraftId, baseAnswers)
          .mustBe(rts.TelephoneNumberController.onPageLoad(index, fakeDraftId))
      }

      "Telephone number page -> Check your answers page" in {
        navigator.nextPage(TelephoneNumberPage(index), fakeDraftId, baseAnswers)
          .mustBe(rts.CheckDetailsController.onPageLoad(index, fakeDraftId))
      }
    }

    "a 5mld trust" must {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

      "Name page" when {

        "Not UK registered" must {
          "-> Address UK yes no page" in {
            val answers = baseAnswers
              .set(UkRegisteredYesNoPage(index), false).success.value

            navigator.nextPage(NamePage(index), fakeDraftId, answers)
              .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "UTR page -> Country Of Residence UK yes no page" in {
        navigator.nextPage(UtrPage(index), fakeDraftId, baseAnswers)
          .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> No -> Country of Residence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence UK yes no page -> Yes -> UK Address page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId, answers)
          .mustBe(rts.UkAddressController.onPageLoad(index, fakeDraftId))
      }

      "Country Of Residence page -> International Address page" in {
        navigator.nextPage(CountryOfResidencePage(index), fakeDraftId, baseAnswers)
          .mustBe(rts.InternationalAddressController.onPageLoad(index, fakeDraftId))
      }
    }
  }

}
