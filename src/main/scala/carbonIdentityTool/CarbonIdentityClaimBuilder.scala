// Copyright (c) 2015, Thilo Planz.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the Apache License, Version 2.0
// as published by the Apache Software Foundation (the "License").
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//
// You should have received a copy of the License along with this program.
// If not, see <http://www.apache.org/licenses/LICENSE-2.0>.


package carbonIdentityTool

import scala.collection.mutable

/**
 * A builder to construct the "claims" object for
 * the CarbonIdentityUserInfo, using the default "carbon schema"
 * (http://wso2.org/claims namespace)
 */

class CarbonIdentityClaimBuilder {

  private val map = new mutable.HashMap[String, String]

  private def set(key: String, value: String) : this.type = { map += (key -> value); this}

  def withGivenName(name: String): this.type = set("http://wso2.org/claims/givenname" , name)

  def withLastName(name: String): this.type = set("http://wso2.org/claims/lastname", name)

  def withOrganization(name: String): this.type = set("http://wso2.org/claims/organization", name)

  def withStreetAddress(address: String): this.type = set("http://wso2.org/claims/streetaddress", address)

  def withCountry(name: String): this.type = set("http://wso2.org/claims/country", name)

  def withEmail(email: String): this.type = set("http://wso2.org/claims/emailaddress", email)

  def withTelephoneNumber(number: String): this.type = set("http://wso2.org/claims/telephone", number)

  def withMobileNumber(number: String): this.type = set("http://wso2.org/claims/mobile", number)

  def withIM(number: String): this.type = set("http://wso2.org/claims/im", number)

  def withURL(url: String) : this.type = set("http://wso2.org/claims/url", url)

  def withGender(gender: String) : this.type = set("http://wso2.org/claims/gender", gender)

  def withTitle(title: String) : this.type = set("http://wso2.org/claims/title", title)

  def withRole(role: String) : this.type = set("http://wso2.org/claims/role", role)

  def withPostalCode(code: String) : this.type = set("http://wso2.org/claims/postalcode", code)

  def withLocality(locality: String) : this.type = set("http://wso2.org/claims/locality", locality)

  def withRegion(name: String): this.type = set("http://wso2.org/claims/region" , name)

  def withBirthDate(name: String): this.type = set("http://wso2.org/claims/dob" , name)

  def withNickname(name: String): this.type = set("http://wso2.org/claims/nickname" , name)

  def withStateOrProvince(name: String): this.type = set("http://wso2.org/claims/stateorprovince" , name)

  def withOtherPhone(number: String): this.type = set("http://wso2.org/claims/otherphone" , number)

  def withFullName(name: String): this.type = set("http://wso2.org/claims/fullname" , name)

  def withPrimaryChallengeQuestion(question: String): this.type = set("http://wso2.org/claims/primaryChallengeQuestion" , question)

  def withChallengeQuestionUris(question: String): this.type = set("http://wso2.org/claims/challengeQuestionUris" , question)

  def withOneTimePassword(otp: String): this.type = set("http://wso2.org/claims/oneTimePassword" , otp)

  def withPasswordTimestamp(stamp: String): this.type = set("http://wso2.org/claims/passwordTimestamp" , stamp)

  def withAccountLocked(locked: String): this.type = set("http://wso2.org/claims/accountLocked" , locked)

  def withChallengeQuestion1(question: String): this.type = set("http://wso2.org/claims/challengeQuestion1" , question)

  def withChallengeQuestion2(question: String): this.type = set("http://wso2.org/claims/challengeQuestion2" , question)


  def buildUserInfo(name: String, roles : String*) : CarbonIdentityUserInfo =
      new CarbonIdentityUserInfo(name, roles.toArray, map.toMap)

}
