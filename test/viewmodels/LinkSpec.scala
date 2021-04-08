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

package viewmodels

import base.SpecBase

class LinkSpec extends SpecBase {

  "Link" when {

    val url = "url"

    ".cssText" must {
      "be in lower case and replace spaces with hyphens" when {

        "Passport and address" in {
          val link = Link("Passport and address", url)
          link.cssText mustBe "passport-and-address"
        }

        "ID card and address" in {
          val link = Link("ID card and address", url)
          link.cssText mustBe "id-card-and-address"
        }
      }
    }
  }
}
