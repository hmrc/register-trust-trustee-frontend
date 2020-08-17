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

package utils

import base.SpecBase
import controllers.register.leadtrustee.organisation.{routes => ltoRts}
import controllers.register.trustees.organisation.{routes => toRts}
import controllers.register.trustees.{routes => tRts}
import models.Status._
import models.core.pages.IndividualOrBusiness._
import models.core.pages.UKAddress
import pages.entitystatus.TrusteeStatus
import pages.register.leadtrustee.{organisation => ltorg}
import pages.register.trustees.{IsThisLeadTrusteePage, TrusteeIndividualOrBusinessPage, organisation => torg}
import viewmodels.{AddRow, AddToRows}

class AddATrusteeViewHelperSpec extends SpecBase {

  private val defaultName: String = "No name added"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")

  private def removeOrgRoute(index: Int): String =
    controllers.register.trustees.organisation.routes.RemoveTrusteeOrgController.onPageLoad(index, draftId).url

  private def removeIndRoute(index: Int): String =
    controllers.register.trustees.individual.routes.RemoveTrusteeController.onPageLoad(index, draftId).url

  "Add A Trustee View Helper" must {

    "return the add to rows" when {

      "lead trustee type unknown" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeStatus(0), InProgress).success.value
          .set(IsThisLeadTrusteePage(0), true).success.value

        val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = List(
            AddRow(
              name = defaultName,
              typeLabel = "Lead Trustee",
              changeUrl = tRts.IsThisLeadTrusteeController.onPageLoad(0, fakeDraftId).url,
              removeUrl = removeIndRoute(0)
            )
          ),
          complete = Nil
        )
      }

      "trustee type unknown" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeStatus(0), InProgress).success.value
          .set(IsThisLeadTrusteePage(0), false).success.value

        val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = List(
            AddRow(
              name = defaultName,
              typeLabel = "Trustee",
              changeUrl = tRts.IsThisLeadTrusteeController.onPageLoad(0, fakeDraftId).url,
              removeUrl = removeIndRoute(0)
            )
          ),
          complete = Nil
        )
      }

      "lead trustee org" when {

        val name: String = "Lead Trustee Org"
        val typeLabel = "Lead Trustee Company"

        "in progress" when {

          "name added" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeStatus(0), InProgress).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
              .set(ltorg.NamePage(0), name).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeOrgRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "no name added" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeStatus(0), InProgress).success.value
              .set(IsThisLeadTrusteePage(0), true).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = defaultName,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeOrgRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }

        "completed" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeStatus(0), Completed).success.value
            .set(IsThisLeadTrusteePage(0), true).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
            .set(ltorg.NamePage(0), name).success.value
            .set(ltorg.AddressUkYesNoPage(0), true).success.value
            .set(ltorg.UkAddressPage(0), ukAddress).success.value
            .set(ltorg.EmailAddressYesNoPage(0), false).success.value
            .set(ltorg.TelephoneNumberPage(0), "tel").success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = ltoRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeOrgRoute(0)
              )
            )
          )
        }
      }

      "trustee org" when {

        val name: String = "Trustee Org"
        val typeLabel = "Trustee Company"

        "in progress" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeStatus(0), InProgress).success.value
            .set(IsThisLeadTrusteePage(0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(torg.NamePage(0), name).success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeOrgRoute(0)
              )
            ),
            complete = Nil
          )
        }

        "completed" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeStatus(0), Completed).success.value
            .set(IsThisLeadTrusteePage(0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(torg.NamePage(0), name).success.value
            .set(torg.UtrYesNoPage(0), true).success.value
            .set(torg.UtrPage(0), "utr").success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeOrgRoute(0)
              )
            )
          )
        }
        
        "multiple" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeStatus(0), InProgress).success.value
            .set(IsThisLeadTrusteePage(0), false).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(torg.NamePage(0), name).success.value

            .set(TrusteeStatus(1), Completed).success.value
            .set(IsThisLeadTrusteePage(1), false).success.value
            .set(TrusteeIndividualOrBusinessPage(1), Business).success.value
            .set(torg.NamePage(1), name).success.value
            .set(torg.UtrYesNoPage(1), true).success.value
            .set(torg.UtrPage(1), "utr").success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeOrgRoute(0)
              )
            ),
            complete = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.CheckDetailsController.onPageLoad(1, fakeDraftId).url,
                removeUrl = removeOrgRoute(1)
              )
            )
          )
        }
      }
    }
  }
}
