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

  private def set(key: String, value: Option[String]) : this.type = value match{
    case Some(v) => set(key, v)
    case _ => map -= (key); this
  }

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

  def withGivenName(name: Option[String]): this.type = set("http://wso2.org/claims/givenname" , name)

  def withLastName(name: Option[String]): this.type = set("http://wso2.org/claims/lastname", name)

  def withOrganization(name: Option[String]): this.type = set("http://wso2.org/claims/organization", name)

  def withStreetAddress(address: Option[String]): this.type = set("http://wso2.org/claims/streetaddress", address)

  def withCountry(name: Option[String]): this.type = set("http://wso2.org/claims/country", name)

  def withEmail(email: Option[String]): this.type = set("http://wso2.org/claims/emailaddress", email  )

  def withTelephoneNumber(number: Option[String]): this.type = set("http://wso2.org/claims/telephone", number)

  def withMobileNumber(number: Option[String]): this.type = set("http://wso2.org/claims/mobile", number)

  def withIM(number: Option[String]): this.type = set("http://wso2.org/claims/im", number)

  def withURL(url: Option[String]) : this.type = set("http://wso2.org/claims/url", url)

  def withGender(gender: Option[String]) : this.type = set("http://wso2.org/claims/gender", gender)

  def withTitle(title: Option[String]) : this.type = set("http://wso2.org/claims/title", title)

  def withRole(role: Option[String]) : this.type = set("http://wso2.org/claims/role", role)

  def withPostalCode(code: Option[String]) : this.type = set("http://wso2.org/claims/postalcode", code)

  def withLocality(locality: Option[String]) : this.type = set("http://wso2.org/claims/locality", locality)

  def withRegion(name: Option[String]): this.type = set("http://wso2.org/claims/region" , name)

  def withBirthDate(name: Option[String]): this.type = set("http://wso2.org/claims/dob" , name)

  def withNickname(name: Option[String]): this.type = set("http://wso2.org/claims/nickname" , name)

  def withStateOrProvince(name: Option[String]): this.type = set("http://wso2.org/claims/stateorprovince" , name)

  def withOtherPhone(number: Option[String]): this.type = set("http://wso2.org/claims/otherphone" , number)

  def withFullName(name: Option[String]): this.type = set("http://wso2.org/claims/fullname" , name)

  def withPrimaryChallengeQuestion(question: Option[String]): this.type = set("http://wso2.org/claims/primaryChallengeQuestion" , question)

  def withChallengeQuestionUris(question: Option[String]): this.type = set("http://wso2.org/claims/challengeQuestionUris" , question)

  def withOneTimePassword(otp: Option[String]): this.type = set("http://wso2.org/claims/oneTimePassword" , otp)

  def withPasswordTimestamp(stamp: Option[String]): this.type = set("http://wso2.org/claims/passwordTimestamp" , stamp)

  def withAccountLocked(locked: Option[String]): this.type = set("http://wso2.org/claims/accountLocked" , locked)

  def withChallengeQuestion1(question: Option[String]): this.type = set("http://wso2.org/claims/challengeQuestion1" , question)

  def withChallengeQuestion2(question: Option[String]): this.type = set("http://wso2.org/claims/challengeQuestion2" , question)

  def buildUserInfo(name: String, roles : String*) : CarbonIdentityUserInfo =
      new CarbonIdentityUserInfo(name, roles.toArray, map.toMap)

}


/**
 * getters for extracting "carbon schema" claim values
 */

object CarbonIdentityClaims{
  
  private def get(claims: Map[String, String], claim: String) : Option[String] = claims.get(claim)
  
  def getGivenName(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/givenname" ) 

  def getLastName(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/lastname")

  def getOrganization(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/organization")

  def getStreetAddress(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/streetaddress")

  def getCountry(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/country")

  def getEmail(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/emailaddress")

  def getTelephoneNumber(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/telephone")

  def getMobileNumber(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/mobile")

  def getIM(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/im")

  def getURL(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/url")

  def getGender(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/gender")

  def getTitle(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/title")

  def getRole(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/role")

  def getPostalCode(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/postalcode")

  def getLocality(user: CarbonIdentityUserInfo) : Option[String] = get(user.claims, "http://wso2.org/claims/locality")

  def getRegion(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/region" )

  def getBirthDate(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/dob" )

  def getNickname(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/nickname" )

  def getStateOrProvince(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/stateorprovince" )

  def getOtherPhone(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/otherphone" )

  def getFullName(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/fullname" )

  def getPrimaryChallengeQuestion(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/primaryChallengeQuestion")

  def getChallengeQuestionUris(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/challengeQuestionUris")

  def getOneTimePassword(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/oneTimePassword")

  def getPasswordTimestamp(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/passwordTimestamp")

  def getAccountLocked(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/accountLocked")

  def getChallengeQuestion1(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/challengeQuestion1" )

  def getChallengeQuestion2(user: CarbonIdentityUserInfo): Option[String] = get(user.claims, "http://wso2.org/claims/challengeQuestion2" )

}