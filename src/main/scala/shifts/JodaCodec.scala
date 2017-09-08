/*
 * Copyright 2017 Christophe Ribeiro
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
package io.circe.joda

import io.circe._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.language.implicitConversions

/**
  * Circe codec for Joda-Time.
  *
  * @author christophe ribeiro
  */
trait JodaCodec {

  val DefaultJodaDatePattern = "yyyy-MM-dd"

  implicit val DefaultJodaDateEncoder: Encoder[DateTime] = Encoder.instance[DateTime] { dateTime =>
    Json.fromString(dateTime.toString("yyyy-MM-dd"))
  }
  implicit val DefaultJodaDateDecoder: Decoder[DateTime] = jodaDateDecoder(DefaultJodaDatePattern)

  /**
    * Decoder for the `org.joda.time.DateTime` type.
    *
    * @param pattern a pattern datetime
    * @return a Datetime decoded
    *
    * @see pattern at http://joda-time.sourceforge.net/apidocs/org/joda/time/format/ISODateTimeFormat.html
    */
  def jodaDateDecoder(pattern: String): Decoder[DateTime] = Decoder.instance { cursor =>
    cursor.focus
      .map {
        // String
        case json if json.isString => {
          tryParserDatetime(json.asString.get, pattern, DecodingFailure("DateTime", cursor.history))
        }
        // Number
        case json if json.isNumber =>
          json.asNumber match {
            // Long
            case Some(num) if num.toLong.isDefined => Right(new DateTime(num.toLong.get))
            // unknown
            case _ => Left(DecodingFailure("DateTime", cursor.history))
          }
      }
      .getOrElse {
        // focus return None
        Left(DecodingFailure("DateTime", cursor.history))
      }
  }

  /**
    * Try to parse a datetime as string through a pattern.
    *
    * @param input a string datetime
    * @param pattern a pattern datetime (e.g 'yyyy-MM-dd')
    * @return a DecodingFailure or a Datetime
    *
    * @see pattern at http://joda-time.sourceforge.net/apidocs/org/joda/time/format/ISODateTimeFormat.html
    */
  private[joda] def tryParserDatetime(input: String,
                                      pattern: String,
                                      error: DecodingFailure): Either[DecodingFailure, DateTime] =
    try {
      val format   = DateTimeFormat.forPattern(pattern)
      val datetime = DateTime.parse(input, format)
      Right(datetime)
    } catch {
      case _: Exception => Left(error)
    }
}

object JodaCodec extends JodaCodec
