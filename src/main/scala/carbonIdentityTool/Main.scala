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

import com.typesafe.config.ConfigFactory
import org.rogach.scallop.{ScallopConf, Subcommand}

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
    if (config.hasPath("identityServer.sslCert")) { new SSLKeyPinning(config.getString("identityServer.sslCert")).setDefaultSSLContext() }


    object Args extends ScallopConf(args){
      val askForAccessToken = new Subcommand("askForAccessToken"){
         val clientId = trailArg[String]()
         val scope = trailArg[String]()
       }
      val askForAuthCode = new Subcommand("askForAuthCode"){
        val clientId = trailArg[String]()
        val scope = trailArg[String]()
      }
      val exchangeAuthCode = new Subcommand("exchangeAuthCode"){
        val clientId = trailArg[String]()
        val clientSecret = trailArg[String]()
        val authCode = trailArg[String]()
      }
      val verifyAccessToken = new Subcommand("verifyAccessToken"){
        val token = trailArg[String]()
      }
      val addUser = new Subcommand("addUser"){
        val name = trailArg[String]()
        val email = opt[String]()
      }
      val showUserInfo = new Subcommand("showUserInfo"){
        val name = trailArg[String]()
      }
      val checkPassword = new Subcommand("checkPassword"){
        val name = trailArg[String]()
        val password = trailArg[String]()
      }
    }


    import scala.language.reflectiveCalls
    Args.subcommand match{
      case Some(Args.askForAccessToken) => askForAccessToken(Args.askForAccessToken.clientId(), Args.askForAccessToken.scope())
      case Some(Args.askForAuthCode) => askForAuthCode(Args.askForAuthCode.clientId(), Args.askForAuthCode.scope())
      case Some(Args.exchangeAuthCode) => exchangeAuthCode(Args.exchangeAuthCode.clientId(), Args.exchangeAuthCode.clientSecret(), Args.exchangeAuthCode.authCode())
      case Some(Args.verifyAccessToken) => verifyAccessToken(Args.verifyAccessToken.token())
      case Some(Args.addUser) => addUser(Args.addUser.name(), Args.addUser.email.get)
      case Some(Args.showUserInfo) => showUserInfo(Args.showUserInfo.name())
      case Some(Args.checkPassword) => checkPassword(Args.checkPassword.name(), Args.checkPassword.password())
      case _ => Args.printHelp()
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

  private def addUser(name: String, email: Option[String]): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    IdentityServiceClient.addUser(realm,
    // TODO: other claims, not just --email
      new CarbonIdentityClaimBuilder().withEmail(email)
        .buildUserInfo(name),
      null, "password", true)
  }

  private def showUserInfo(name: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);

    IdentityServiceClient.getUserInfo(realm, name) match {
      case Some(user) => {
        println(user.name);
        println(user.claims)
        println(user.roles.toList)
      }
      case None => println("no such user")
    }


  }



  private def checkPassword(name: String, password: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    if (IdentityServiceClient.authenticateUser(realm, name, password)){
      println("password accepted")
    }else{
      println("password rejected")
    }
  }



}
