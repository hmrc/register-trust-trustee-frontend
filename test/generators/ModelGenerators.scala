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

package generators

import models.core.pages._
import models.registration.pages._
import models.{Status, YesNoDontKnow}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import java.time.LocalDate

trait ModelGenerators {

  implicit lazy val arbitraryFullName : Arbitrary[FullName] = {
    Arbitrary {
      for {
        str <- arbitrary[String]
      } yield {
        FullName(str, Some(str), str)
      }
    }
  }

  implicit lazy val arbitraryInternationalAddress: Arbitrary[InternationalAddress] =
    Arbitrary {
      for {
        str <- arbitrary[String]
      } yield InternationalAddress(str,str,Some(str),str)
    }

  implicit lazy val arbitraryUkAddress: Arbitrary[UKAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[String]
        line4 <- arbitrary[String]
        postcode <- arbitrary[String]
      } yield UKAddress(line1, line2, Some(line3), Some(line4), postcode)
    }

  implicit lazy val arbitraryAddATrustee: Arbitrary[AddATrustee] =
    Arbitrary {
      Gen.oneOf(AddATrustee.values)
    }

  implicit lazy val arbitraryYesNoDontKnow: Arbitrary[YesNoDontKnow] =
    Arbitrary {
      Gen.oneOf(YesNoDontKnow.values)
    }

  implicit lazy val arbitraryTrusteeOrIndividual: Arbitrary[IndividualOrBusiness] =
    Arbitrary {
      Gen.oneOf(IndividualOrBusiness.values.toSeq)
    }

  implicit lazy val arbitraryTrusteeOrLeadTrustee: Arbitrary[TrusteeOrLeadTrustee] =
    Arbitrary {
      Gen.oneOf(TrusteeOrLeadTrustee.values.toSeq)
    }

  implicit lazy val arbitraryLocalDate : Arbitrary[LocalDate] =
    Arbitrary {
      Gen.const(LocalDate.of(2010, 10, 10))
    }

  implicit lazy val arbitraryStatus: Arbitrary[Status] =
    Arbitrary {
      Gen.oneOf(Status.values.toList)
    }

}
