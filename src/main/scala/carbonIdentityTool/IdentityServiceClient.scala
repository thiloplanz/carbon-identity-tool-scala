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

import javax.servlet.http.HttpServletRequest

import org.apache.axis2.transport.http.HTTPConstants
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator
import org.apache.commons.httpclient.methods.{GetMethod, PostMethod, StringRequestEntity}
import org.apache.commons.httpclient.{HttpClient, HttpMethod}
import org.apache.oltu.oauth2.client.request.{OAuthBearerClientRequest, OAuthClientRequest}
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.json.JSONObject
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub
import org.wso2.carbon.identity.oauth2.stub.OAuth2TokenValidationServiceStub
import org.wso2.carbon.identity.oauth2.stub.dto.{OAuth2TokenValidationRequestDTO, OAuth2TokenValidationRequestDTO_OAuth2AccessToken, OAuth2TokenValidationResponseDTO}
import org.wso2.carbon.um.ws.api.WSRealmBuilder
import org.wso2.carbon.user.api.UserRealm

import scala.collection.mutable


object IdentityServiceClient {

  /**
   * Uses the Carbon IdentityServer SOAP API to verify an access token.
   *
   * @return either the valid response or an error message
   */


  def verifyAccessToken(credentials: IdentityServiceAdminCredentials, token: OAuthAccessToken) : Either[String, OAuth2TokenValidationResponseDTO] = {

    val stub = new OAuth2TokenValidationServiceStub(credentials.hostUrl + "services/OAuth2TokenValidationService.OAuth2TokenValidationServiceHttpsSoap12Endpoint/")
    val client = stub._getServiceClient();
    val options = client.getOptions();
    options.setCallTransportCleanup(true);
    options.setManageSession(true);

    val accessToken =  new OAuth2TokenValidationRequestDTO_OAuth2AccessToken();
    accessToken.setTokenType("bearer");
    accessToken.setIdentifier(token.token);

    val request = new OAuth2TokenValidationRequestDTO()
    request.setAccessToken(accessToken);

    val basicAuth = new Authenticator
    basicAuth.setUsername(credentials.username)
    basicAuth.setPassword(credentials.password)
    basicAuth.setPreemptiveAuthentication(true)

    val clientOptions = stub._getServiceClient().getOptions();
    clientOptions.setProperty(HTTPConstants.AUTHENTICATE, basicAuth);

    val resp = stub.validate(request)
    if (resp.getValid)
      return Right(resp)


    return Left(resp.getErrorMsg)
  }



  /**
   * start a new session for the Identity Server SOAP API
   */
  def loginToAdminServices(credentials: IdentityServiceAdminCredentials) : UserRealm = {
    val stub = new AuthenticationAdminStub(credentials.hostUrl+"services/AuthenticationAdmin.AuthenticationAdminHttpsSoap12Endpoint/")
    if (!stub.login(credentials.username, credentials.password, null)){
      throw new SecurityException("failed to authenticate as admin user");
    }
    val cookie : String = stub._getServiceClient().getServiceContext.getProperty(HTTPConstants.COOKIE_STRING).toString;
    return WSRealmBuilder.createWSRealm(credentials.hostUrl + "services/", cookie, null);
  }

  /**
   * Uses the Carbon IdentityServer SOAP API to create a new user
   */
  def addUser(realm: UserRealm, user: CarbonIdentityUserInfo, profile: String, password: String, requirePasswordChange: Boolean) {
      import scala.collection.JavaConversions.mapAsJavaMap
      val claims : Map[String, String] = if (user.claims == null) Map.empty else user.claims
      realm.getUserStoreManager.addUser(user.name, password, user.roles, claims , profile, requirePasswordChange )
  }

  /**
   * check a user's login credentials (password)
   */
  def authenticateUser(realm: UserRealm, username: String, password: String): Boolean =
    // TODO: do we really have to use an admin call to do this?
    realm.getUserStoreManager().authenticate(username, password)


  /**
   * get a user's claims and roles (by "username")
   */
  def getUserInfo(realm: UserRealm, username: String, profile: String = null) : Option[CarbonIdentityUserInfo] = {
    // TODO: find out what "profile" does
    val us = realm.getUserStoreManager
    val claims = new mutable.HashMap[String, String]()
    for ( c <- us.getUserClaimValues(username, profile)){
       claims.put(c.getClaimUri, c.getValue)
    }
    if (claims.isEmpty)
      return None
    return Some(new CarbonIdentityUserInfo(username, us.getRoleListOfUser(username), claims.toMap));
  }

  /**
   * get a user's claims and roles (by user's email)
   */
  def getUserInfoByUniqueEmail(realm: UserRealm, email: String, profile: String = null) : Option[CarbonIdentityUserInfo] = {
    val us = realm.getUserStoreManager
    // TODO: This is just horrible. LDAP query injection...
    // There must be a better way
    // http://stackoverflow.com/q/11933831/14955
    val usernames = us.listUsers("*)(mail="+email, 2)
    if (usernames == null || usernames.length == 0) return None
    if (usernames.length == 1) return getUserInfo(realm, usernames(0), profile)
    throw new IllegalArgumentException("there is more than one user with email "+email)
  }


  /**
   * returns the URI that the user needs to go to to authorize the application
   * (create an access token for it).
   *
   * This uses Grant Type "implicit" (for client-side apps, where there is no client secret)
   */

  def getAuthorizationUri_Implicit(clientId: String, scope: String, baseUrl: String = "https://127.0.0.1:9443/oauth2"): String = OAuthClientRequest
    .authorizationLocation(baseUrl+"/authorize")
    .setClientId(clientId)
    .setResponseType("token")
    .setScope(scope)
    .setRedirectURI("about:blank")
    .buildQueryMessage().getLocationUri;


  /**
   * returns the URI that the user needs to go to to authorize the application
   * (create an auth token for it).
   *
   * This uses Grant Type "authCode" (to pass on to the server which holds the "client secret").
   * You can then use exchangeAuthCodeForAccessToken to get the access token.
   */
  def getAuthorizationUri_AuthToken(clientId: String, scope: String, redirectUrl: String, baseUrl: String = "https://127.0.0.1:9443/oauth2"): String = OAuthClientRequest
    .authorizationLocation(baseUrl+"/authorize")
    .setClientId(clientId)
    .setResponseType("code")
    .setScope(scope)
    .setRedirectURI(redirectUrl)
    .buildQueryMessage().getLocationUri;


  def extractAuthCode(request: HttpServletRequest): String = OAuthAuthzResponse.oauthCodeAuthzResponse(request).getCode()

  def exchangeAuthCodeForAccessToken(authCode: String, clientId: String, clientSecret: String, baseUrl: String = "https://127.0.0.1:9443/oauth2"): String = {

    val tokenRequest = OAuthClientRequest
      .tokenLocation(baseUrl+"/token")
      .setGrantType(GrantType.AUTHORIZATION_CODE)
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .setCode(authCode)
      .setRedirectURI("about:blank")
      .buildBodyMessage();

    return getRequiredJSONString(post(tokenRequest), "access_token")
  }


  /**
   * retrieves the OpenID user profile
   */
  // TODO: parse the JSON String into an appropriate structure (Map?)
  def getOpenIdUserProfile(accessToken: OAuthAccessToken) : String
    = get(new OAuthBearerClientRequest(accessToken.baseUrl + "/userinfo?schema=openid").setAccessToken(accessToken.token).buildHeaderMessage())


  private def getRequiredJSONString(json: String, key: String) : String = {
    try {
      val x = new JSONObject(json);
      return x.getString("access_token");
    }
    catch {
      case e: Exception =>
        throw new IllegalArgumentException("invalid response, does not contain JSON key "+key+"\n"+json, e);
    }
  }


  // use Commons HttpClient 3, which is a bit outdated, because Axis2 is using that as well
  private def runRequest(httpMethod: HttpMethod, request: OAuthClientRequest): String ={
    val client = new HttpClient();
    import scala.collection.JavaConversions.mapAsScalaMap

    try {
      for((k,v) <- request.getHeaders){
        httpMethod.setRequestHeader(k, v)
      }
      val statusCode = client.executeMethod(httpMethod);
      if (statusCode != 200) {
        val errorMessage = "failed to access " + request.getLocationUri + ", HTTP status " + statusCode + " " + httpMethod.getStatusLine
        throw new IllegalArgumentException( errorMessage + "\n" + httpMethod.getResponseBodyAsString)
      }
      return httpMethod.getResponseBodyAsString
    }
    finally {
      httpMethod.releaseConnection()
    }
  }

  private def get(request: OAuthClientRequest): String = runRequest(new GetMethod(request.getLocationUri), request)

  private def post(request: OAuthClientRequest): String = {
    val post = new PostMethod(request.getLocationUri)
    post.setRequestEntity(new StringRequestEntity(request.getBody, "application/x-www-form-urlencoded", "UTF-8"))
    return runRequest(post, request)
  }

}

final class IdentityServiceAdminCredentials
  (
    val username: String = "admin",
    val password: String = "admin",
    val hostUrl: String =  "https://127.0.0.1:9443/")


final class OAuthAccessToken
  (
    val token: String,
    val baseUrl: String = "https://127.0.0.1:9443/oauth2" )


final class CarbonIdentityUserInfo
(
    val name: String,
    val roles: Array[String],
    val claims: Map[String, String]

  )