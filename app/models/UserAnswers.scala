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

package models

import pages.register.leadtrustee.individual.MatchedYesNoPage
import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json._
import queries.{Gettable, Settable}

import scala.util.{Failure, Success, Try}

trait ReadableUserAnswers {

  val data: JsObject

  val isTaxable: Boolean = true
  val existingTrustUtr: Option[String] = None

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] = {
    getAtPath(page.path)
  }

  def getAtPath[A](path: JsPath)(implicit rds: Reads[A]): Option[A] = {
    Reads.at(path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_) => None
    }
  }
}

case class ReadOnlyUserAnswers(data: JsObject) extends ReadableUserAnswers

object ReadOnlyUserAnswers {
  implicit lazy val formats: OFormat[ReadOnlyUserAnswers] = Json.format[ReadOnlyUserAnswers]
}

final case class UserAnswers(draftId: String,
                             data: JsObject = Json.obj(),
                             internalAuthId :String,
                             override val isTaxable: Boolean = true,
                             override val existingTrustUtr: Option[String] = None) extends ReadableUserAnswers with Logging {

  def isLeadTrusteeMatched(index: Int): Boolean = this.get(MatchedYesNoPage(index)).contains(true)

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A], reads: Reads[A]): Try[UserAnswers] = {

    val hasValueChanged: Boolean = !getAtPath(page.path).contains(value)

    val updatedData = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        val errorPaths = errors.collectFirst{ case (path, e) => s"$path $e"}
        logger.warn(s"Unable to set path ${page.path} due to errors $errorPaths")
        Failure(JsResultException(errors))
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        if (hasValueChanged) page.cleanup(Some(value), updatedAnswers) else Success(updatedAnswers)
    }
  }

  def remove[A](query: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(query.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.flatMap {
      d =>
        val updatedAnswers = copy (data = d)
        query.cleanup(None, updatedAnswers)
    }
  }

  def deleteAtPath(path: JsPath): Try[UserAnswers] = {
    data.removeObject(path).map(obj => copy(data = obj)).fold(
      _ => Success(this),
      result => Success(result)
    )
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = (
    (__ \ "_id").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "internalId").read[String] and
      (__ \ "isTaxable").readWithDefault[Boolean](true) and
      (__ \ "existingTrustUtr").readNullable[String]
    ) (UserAnswers.apply _)

  implicit lazy val writes: OWrites[UserAnswers] = (
    (__ \ "_id").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "internalId").write[String] and
      (__ \ "isTaxable").write[Boolean] and
      (__ \ "existingTrustUtr").writeNullable[String]
    ) (unlift(UserAnswers.unapply))
}
