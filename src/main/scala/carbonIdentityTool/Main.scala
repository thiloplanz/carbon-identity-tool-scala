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

import java.awt.Desktop
import java.net.URI
import java.util

import com.typesafe.config.{ConfigFactory, Config}

object Main {

  private val config = ConfigFactory.load()

  private val host = config.getString("identityServer.hostUrl")

  private val oauthUrl = host + "oauth2"

  private val admin = new IdentityServiceAdminCredentials(
     config.getString("identityServer.admin.username"),
     config.getString("identityServer.admin.password"),
     host
  )

  def main(args: Array[String]) {

    args match{
      case Array("askForAccessToken", clientId, scope) => askForAccessToken(clientId, scope)
      case Array("askForAuthCode", clientId, scope) => askForAuthCode(clientId, scope)
      case Array("exchangeAuthCode", clientId, clientSecret, authCode) => exchangeAuthCode(clientId, clientSecret, authCode)
      case Array("verifyAccessToken", token) => verifyAccessToken(token)
      case Array("addUser", name) => addUser(name)
      case Array("showUserInfo", name) => showUserInfo(name)
      case Array("checkPassword", name, pass) => checkPassword(name, pass)
      case _ => usage()
    }

  }

  /**
   * opens the default web browser with the authorization page
   * when granted, the access token will show up in the URL
   */
  private def askForAccessToken(clientId: String, scope:String) =
    Desktop.getDesktop.browse(new URI(IdentityServiceClient.getAuthorizationUri_Implicit(clientId, scope, oauthUrl)))

  private def askForAuthCode(clientId: String, scope: String) =
    Desktop.getDesktop.browse(new URI(IdentityServiceClient.getAuthorizationUri_AuthToken(clientId, scope, "about:blank")))

  private def exchangeAuthCode(clientId: String, clientSecret: String, authCode: String): Unit ={
    println(IdentityServiceClient.exchangeAuthCodeForAccessToken(authCode, clientId, clientSecret));
  }

  private def verifyAccessToken(token: String) {
    val accessToken = new OAuthAccessToken(token, oauthUrl)
    IdentityServiceClient.verifyAccessToken(admin, accessToken) match {
      case Right(tokenInfo) => println("token for "+ tokenInfo.getAuthorizedUser+" accepted")
      case Left(error) => println("token rejected: "+error)
    }
  }

  private def addUser(name: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    IdentityServiceClient.addUser(realm,
      new CarbonIdentityClaimBuilder().withEmail("test@test.com")
        .buildUserInfo(name),
      null, "password", true)
  }

  private def showUserInfo(name: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);

    val user = IdentityServiceClient.getUserInfo(realm, name)
    println(name);
    println(user.claims)
    println(user.roles.toList)
  }

  private def checkPassword(name: String, password: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    if (IdentityServiceClient.authenticateUser(realm, name, password)){
      println("password accepted")
    }else{
      println("password rejected")
    }
  }

  private def usage() {
    println("usage: ")
    println("    carbon-identity-tool <command> [<args>]")
    println()
    println("Commands")
    println("    askForAccessToken <clientId> <scope>")
    println("    askForAuthCode <clientId>")
    println("    verifyAccessToken <token>")
    println("    addUser <name>")
    println("    showUserInfo <name>")
    println("    checkPassword <name> <password>")
    System.exit(1)
  }

}
