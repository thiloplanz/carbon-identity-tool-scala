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

import java.util

import org.apache.axis2.transport.http.HTTPConstants
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator
import org.apache.commons.httpclient.{HttpContentTooLargeException, HttpClient}
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.oltu.oauth2.client.request.{OAuthBearerClientRequest, OAuthClientRequest}
import org.wso2.carbon.identity.oauth2.stub.OAuth2TokenValidationServiceStub
import org.wso2.carbon.identity.oauth2.stub.dto.{OAuth2TokenValidationResponseDTO, OAuth2TokenValidationRequestDTO, OAuth2TokenValidationRequestDTO_OAuth2AccessToken}

object IdentityServiceClient {

  /**
   * Uses the Carbon IdentityServer SOAP API to verify an access token.
   *
   * @return either the valid response or an error message
   */


  def verifyAccessToken(token: String) : Either[String, OAuth2TokenValidationResponseDTO] = {

    // TODO: make URL configurable
    val stub = new OAuth2TokenValidationServiceStub()
    val client = stub._getServiceClient();
    val options = client.getOptions();
    options.setCallTransportCleanup(true);
    options.setManageSession(true);

    val accessToken =  new OAuth2TokenValidationRequestDTO_OAuth2AccessToken();
    accessToken.setTokenType("bearer");
    accessToken.setIdentifier(token);

    val request = new OAuth2TokenValidationRequestDTO()
    request.setAccessToken(accessToken);

    // TODO: make basic auth configurable
    val basicAuth = new Authenticator
    basicAuth.setUsername("admin")
    basicAuth.setPassword("admin")
    basicAuth.setPreemptiveAuthentication(true)

    val clientOptions = stub._getServiceClient().getOptions();
    clientOptions.setProperty(HTTPConstants.AUTHENTICATE, basicAuth);

    val resp = stub.validate(request)
    if (resp.getValid)
      return Right(resp)


    return Left(resp.getErrorMsg)
  }


  val defaultBaseUrl = "https://127.0.0.1:9443/oauth2"

  /**
   * returns the URI that the user needs to go to to authorize the application
   * (create an access token for it)
   */

  def getAuthorizationUri(clientId: String, scope: String, baseUrl: String = defaultBaseUrl): String = OAuthClientRequest
    .authorizationLocation(baseUrl+"/authorize")
    .setClientId(clientId)
    .setResponseType("token")
    .setScope(scope)
    .setRedirectURI("about:blank")
    .buildQueryMessage().getLocationUri;

  /**
   * retrieves the OpenID user profile
   */
  // TODO: parse the JSON String into an appropriate structure (Map?)
  def getOpenIdUserProfile(accessToken: String, baseUrl: String = defaultBaseUrl) : String
    = get(new OAuthBearerClientRequest(baseUrl + "/userinfo?schema=openid").setAccessToken(accessToken).buildHeaderMessage())


  // use Commons HttpClient 3, which is a bit outdated, because Axis2 is using that as well
  private def get(request: OAuthClientRequest): String = {
    val client = new HttpClient();
    val get = new GetMethod(request.getLocationUri)


    import scala.collection.JavaConversions.mapAsScalaMap

    try {
      for((k,v) <- request.getHeaders){
         get.setRequestHeader(k, v)
      }
      val statusCode = client.executeMethod(get);
      if (statusCode != 200) {
        val errorMessage = "failed to get " + request.getLocationUri + ", HTTP status " + statusCode + " " + get.getStatusLine
        try {
          throw new IllegalArgumentException( errorMessage + "\n" + get.getResponseBodyAsString(2000))
        }
        catch {
          case e: HttpContentTooLargeException => throw new IllegalArgumentException(errorMessage)
          case e: Exception => throw e
        }
      }
      return get.getResponseBodyAsString
    }
    finally {
      get.releaseConnection()
    }
  }


}
