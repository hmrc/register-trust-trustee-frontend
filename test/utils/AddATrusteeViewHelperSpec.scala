/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.register.leadtrustee.individual.{routes => ltiRts}
import controllers.register.leadtrustee.organisation.{routes => ltoRts}
import controllers.register.routes.TrusteeOrLeadTrusteeController
import controllers.register.trustees.individual.{routes => tiRts}
import controllers.register.trustees.organisation.{routes => toRts}
import models.Status._
import models.core.pages.IndividualOrBusiness._
import models.core.pages.TrusteeOrLeadTrustee._
import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.DetailsChoice._
import models.registration.pages.PassportOrIdCardDetails
import pages.entitystatus.TrusteeStatus
import pages.register.leadtrustee.{individual => ltind, organisation => ltorg}
import pages.register.trustees.{individual => tind, organisation => torg}
import pages.register.{TrusteeIndividualOrBusinessPage, TrusteeOrLeadTrusteePage}
import viewmodels.{AddRow, AddToRows}

import java.time.LocalDate

class AddATrusteeViewHelperSpec extends SpecBase {

  private val defaultName: String = "No name added"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "COUNTRY")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val passportOrIdCardDetails: PassportOrIdCardDetails =
    PassportOrIdCardDetails("country", "number", date)

  private def removeRoute(index: Int): String =
    controllers.register.routes.RemoveIndexController.onPageLoad(index, draftId).url

  "Add A Trustee View Helper" must {

    "return the add to rows" when {

      "lead trustee type unknown" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value

        val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = List(
            AddRow(
              name = defaultName,
              typeLabel = "Lead Trustee",
              changeUrl = TrusteeOrLeadTrusteeController.onPageLoad(0, fakeDraftId).url,
              removeUrl = removeRoute(0)
            )
          ),
          complete = Nil
        )
      }

      "trustee type unknown" in {

        val userAnswers = emptyUserAnswers
          .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value

        val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = List(
            AddRow(
              name = defaultName,
              typeLabel = "Trustee",
              changeUrl = TrusteeOrLeadTrusteeController.onPageLoad(0, fakeDraftId).url,
              removeUrl = removeRoute(0)
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
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
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
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "no name added" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = defaultName,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }

        "completed" when {

          "UK registered with UK address and email address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), true).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.UtrPage(0), "utr").success.value
              .set(ltorg.AddressUkYesNoPage(0), true).success.value
              .set(ltorg.UkAddressPage(0), ukAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(0), true).success.value
              .set(ltorg.EmailAddressPage(0), "email").success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "Non-UK registered with non-UK address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.AddressUkYesNoPage(0), false).success.value
              .set(ltorg.InternationalAddressPage(0), internationalAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(0), false).success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }
        }

        "in progress but previously completed" when {

          "IsUkBusiness is true but Utr is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), true).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.AddressUkYesNoPage(0), true).success.value
              .set(ltorg.UkAddressPage(0), ukAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(0), false).success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is true but UkAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.AddressUkYesNoPage(0), true).success.value
              .set(ltorg.EmailAddressYesNoPage(0), false).success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is false but InternationalAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.AddressUkYesNoPage(0), false).success.value
              .set(ltorg.EmailAddressYesNoPage(0), false).success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "EmailAddressYesNo is true but EmailAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(ltorg.UkRegisteredYesNoPage(0), false).success.value
              .set(ltorg.NamePage(0), name).success.value
              .set(ltorg.AddressUkYesNoPage(0), true).success.value
              .set(ltorg.UkAddressPage(0), ukAddress).success.value
              .set(ltorg.EmailAddressYesNoPage(0), true).success.value
              .set(ltorg.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = ltoRts.UkRegisteredYesNoController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }
      }

      "trustee org" when {

        val name: String = "Trustee Org"
        val typeLabel = "Trustee Company"

        "in progress" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(torg.NamePage(0), name).success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeRoute(0)
              )
            ),
            complete = Nil
          )
        }

        "completed" when {

          "no UTR and no address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(torg.AddressYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "UTR" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), true).success.value
              .set(torg.UtrPage(0), "utr").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "UK address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(torg.AddressYesNoPage(0), true).success.value
              .set(torg.AddressUkYesNoPage(0), true).success.value
              .set(torg.UkAddressPage(0), ukAddress).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "Non-UK address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(torg.AddressYesNoPage(0), true).success.value
              .set(torg.AddressUkYesNoPage(0), false).success.value
              .set(torg.InternationalAddressPage(0), internationalAddress).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }
        }

        "in progress but previously completed" when {

          "UtrYesNo is true but Utr is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "UtrYesNo is false but AddressYesNo is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressYesNo is true but AddressUkYesNoPage is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(torg.AddressYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is answered but Address is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
              .set(torg.NamePage(0), name).success.value
              .set(torg.UtrYesNoPage(0), false).success.value
              .set(torg.AddressYesNoPage(0), true).success.value
              .set(torg.AddressUkYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name,
                  typeLabel = typeLabel,
                  changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }
        
        "multiple" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Business).success.value
            .set(torg.NamePage(0), name).success.value

            .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(1), Business).success.value
            .set(torg.NamePage(1), name).success.value
            .set(torg.UtrYesNoPage(1), true).success.value
            .set(torg.UtrPage(1), "utr").success.value
            .set(TrusteeStatus(1), Completed).success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeRoute(0)
              )
            ),
            complete = List(
              AddRow(
                name = name,
                typeLabel = typeLabel,
                changeUrl = toRts.CheckDetailsController.onPageLoad(1, fakeDraftId).url,
                removeUrl = removeRoute(1)
              )
            )
          )
        }
      }

      "lead trustee ind" when {

        val name: FullName = FullName("Lead", Some("Trustee"), "Ind")
        val typeLabel = "Lead Trustee Individual"

        "in progress" when {

          "name added" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "no name added" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = defaultName,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }

        "completed" when {

          "NINO, UK address and email address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), true).success.value
              .set(ltind.TrusteesNinoPage(0), "nino").success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), true).success.value
              .set(ltind.EmailAddressPage(0), "email").success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "Passport and UK address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.TrusteeDetailsChoicePage(0), Passport).success.value
              .set(ltind.PassportDetailsPage(0), passportOrIdCardDetails).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "ID Card and international address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.TrusteeDetailsChoicePage(0), IdCard).success.value
              .set(ltind.IDCardDetailsPage(0), passportOrIdCardDetails).success.value
              .set(ltind.AddressUkYesNoPage(0), false).success.value
              .set(ltind.InternationalAddressPage(0), internationalAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }
        }

        "in progress but previously completed" when {

          "NinoYesNo is true but Nino is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), true).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "NinoYesNo is false but DetailsChoice is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "DetailsChoice is Passport but PassportDetails is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.TrusteeDetailsChoicePage(0), Passport).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "DetailsChoice is IdCard but IdCardDetails is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.TrusteeDetailsChoicePage(0), IdCard).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is true but UkAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), true).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is false but InternationalAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), true).success.value
              .set(ltind.AddressUkYesNoPage(0), false).success.value
              .set(ltind.EmailAddressYesNoPage(0), false).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "EmailAddressYesNo is true but EmailAddress is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), LeadTrustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(ltind.TrusteesNamePage(0), name).success.value
              .set(ltind.TrusteesDateOfBirthPage(0), date).success.value
              .set(ltind.TrusteeNinoYesNoPage(0), false).success.value
              .set(ltind.TrusteeDetailsChoicePage(0), Passport).success.value
              .set(ltind.PassportDetailsPage(0), passportOrIdCardDetails).success.value
              .set(ltind.AddressUkYesNoPage(0), true).success.value
              .set(ltind.UkAddressPage(0), ukAddress).success.value
              .set(ltind.EmailAddressYesNoPage(0), true).success.value
              .set(ltind.TelephoneNumberPage(0), "tel").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = ltiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }
      }

      "trustee ind" when {

        val name: FullName = FullName("Normal", Some("Trustee"), "Ind")
        val typeLabel = "Trustee Individual"

        "in progress" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(tind.NamePage(0), name).success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name.toString,
                typeLabel = typeLabel,
                changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeRoute(0)
              )
            ),
            complete = Nil
          )
        }

        "completed" when {

          "No NINO and no address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "Date of birth and NINO" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), true).success.value
              .set(tind.DateOfBirthPage(0), date).success.value
              .set(tind.NinoYesNoPage(0), true).success.value
              .set(tind.NinoPage(0), "nino").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "UK address" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), true).success.value
              .set(tind.UkAddressPage(0), ukAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), false).success.value
              .set(tind.IDCardDetailsYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "UK address and passport details" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), true).success.value
              .set(tind.UkAddressPage(0), ukAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), true).success.value
              .set(tind.PassportDetailsPage(0), passportOrIdCardDetails).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }

          "Non-UK address and ID card details" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), false).success.value
              .set(tind.InternationalAddressPage(0), internationalAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), false).success.value
              .set(tind.IDCardDetailsYesNoPage(0), true).success.value
              .set(tind.IDCardDetailsPage(0), passportOrIdCardDetails).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = Nil,
              complete = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.CheckDetailsController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              )
            )
          }
        }

        "in progress but previously completed" when {

          "DateOfBirthYesNo is true but DateOfBirth is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), true).success.value
              .set(tind.NinoYesNoPage(0), true).success.value
              .set(tind.NinoPage(0), "nino").success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "NinoYesNo is true but Nino is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "NinoYesNo is false but AddressYesNo is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressYesNo is true but AddressUkYesNo is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "AddressUkYesNo is answered but Address is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "PassportDetailsYesNo is true but PassportDetails is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), true).success.value
              .set(tind.UkAddressPage(0), ukAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "PassportDetailsYesNo is false but IdCardDetailsYesNo is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), true).success.value
              .set(tind.UkAddressPage(0), ukAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), false).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }

          "IdCardDetailsYesNo is true but IdCardDetails is missing" in {

            val userAnswers = emptyUserAnswers
              .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
              .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
              .set(tind.NamePage(0), name).success.value
              .set(tind.DateOfBirthYesNoPage(0), false).success.value
              .set(tind.NinoYesNoPage(0), false).success.value
              .set(tind.AddressYesNoPage(0), true).success.value
              .set(tind.AddressUkYesNoPage(0), true).success.value
              .set(tind.UkAddressPage(0), ukAddress).success.value
              .set(tind.PassportDetailsYesNoPage(0), true).success.value
              .set(tind.PassportDetailsPage(0), passportOrIdCardDetails).success.value
              .set(tind.IDCardDetailsYesNoPage(0), true).success.value
              .set(TrusteeStatus(0), Completed).success.value

            val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

            helper.rows mustBe AddToRows(
              inProgress = List(
                AddRow(
                  name = name.toString,
                  typeLabel = typeLabel,
                  changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                  removeUrl = removeRoute(0)
                )
              ),
              complete = Nil
            )
          }
        }

        "multiple" in {

          val userAnswers = emptyUserAnswers
            .set(TrusteeOrLeadTrusteePage(0), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(0), Individual).success.value
            .set(tind.NamePage(0), name).success.value

            .set(TrusteeOrLeadTrusteePage(1), Trustee).success.value
            .set(TrusteeIndividualOrBusinessPage(1), Individual).success.value
            .set(tind.NamePage(1), name).success.value
            .set(tind.DateOfBirthYesNoPage(1), false).success.value
            .set(tind.NinoYesNoPage(1), true).success.value
            .set(tind.NinoPage(1), "nino").success.value
            .set(TrusteeStatus(1), Completed).success.value

          val helper = new AddATrusteeViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = name.toString,
                typeLabel = typeLabel,
                changeUrl = tiRts.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = removeRoute(0)
              )
            ),
            complete = List(
              AddRow(
                name = name.toString,
                typeLabel = typeLabel,
                changeUrl = tiRts.CheckDetailsController.onPageLoad(1, fakeDraftId).url,
                removeUrl = removeRoute(1)
              )
            )
          )
        }
      }
    }
  }
}
