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

package mapping.reads

import base.SpecBase
import models.YesNoDontKnow
import models.core.pages.FullName
import org.scalatest.{OptionValues}
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class TrusteeIndividualSpec extends SpecBase with Matchers with OptionValues {

  "TrusteeIndividual reads" must {

    "parse the old mental capacity question" in {
      val json = Json.parse(
        """
          |{
          | "trusteeOrLeadTrustee": "trustee",
          | "individualOrBusiness": "individual",
          | "name": {
          |   "firstName": "John",
          |   "lastName": "Smith"
          | },
          | "mentalCapacityYesNo": true
          |}
          |""".stripMargin)

      json.as[TrusteeIndividual] mustBe TrusteeIndividual(
        isLead = false,
        name = FullName("John", None, "Smith"),
        dateOfBirth = None,
        nino = None,
        address = None,
        passportOrIdCard = None,
        countryOfResidence = None,
        nationality = None,
        mentalCapacityYesNo = Some(YesNoDontKnow.Yes)
      )
    }

    "parse the new mental capacity question" in {
      val json = Json.parse(
        """
          |{
          | "trusteeOrLeadTrustee": "trustee",
          | "individualOrBusiness": "individual",
          | "name": {
          |   "firstName": "John",
          |   "lastName": "Smith"
          | },
          | "mentalCapacityYesNo": "dontKnow"
          |}
          |""".stripMargin)

      json.as[TrusteeIndividual] mustBe TrusteeIndividual(
        isLead = false,
        name = FullName("John", None, "Smith"),
        dateOfBirth = None,
        nino = None,
        address = None,
        passportOrIdCard = None,
        countryOfResidence = None,
        nationality = None,
        mentalCapacityYesNo = Some(YesNoDontKnow.DontKnow)
      )
    }

  }

}
