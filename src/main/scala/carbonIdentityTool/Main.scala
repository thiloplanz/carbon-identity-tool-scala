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
      val askForAccessToken = new Subcommand("askForAccessToken") with Runnable{
         val clientId = trailArg[String]()
         val scope = trailArg[String]()
         override def run() = _askForAccessToken(clientId(), scope())
       }
      val askForAuthCode = new Subcommand("askForAuthCode") with Runnable{
        val clientId = trailArg[String]()
        val scope = trailArg[String]()
        override def run() = _askForAuthCode(clientId(), scope())
      }
      val exchangeAuthCode = new Subcommand("exchangeAuthCode") with Runnable{
        val clientId = trailArg[String]()
        val clientSecret = trailArg[String]()
        val authCode = trailArg[String]()
        override def run() = _exchangeAuthCode(clientId(), clientSecret(), authCode())
      }
      val refreshAccessToken = new Subcommand("refreshAccessToken") with Runnable{
        val clientId = trailArg[String]()
        val clientSecret = trailArg[String]()
        val refreshToken = trailArg[String]()
        override def run() = _refreshAccessToken(clientId(), clientSecret(), refreshToken())
      }
      val verifyAccessToken = new Subcommand("verifyAccessToken") with Runnable{
        val token = trailArg[String]()
        override def run() = _verifyAccessToken(token())
      }
      val addUser = new Subcommand("addUser") with Runnable{
        val name = trailArg[String]()
        val email = opt[String]()
        override def run() = _addUser(name(), email.get)
      }
      val showUserInfo = new Subcommand("showUserInfo") with Runnable{
        val name = trailArg[String]()
        override def run() = _showUserInfo(name())
      }
      val checkPassword = new Subcommand("checkPassword") with Runnable{
        val name = trailArg[String]()
        val password = trailArg[String]()
        override def run() = _checkPassword(name(), password())
      }
    }

    Args.subcommand match{
      case Some(command: Runnable) => command.run()
      case _ => Args.printHelp()
    }

  }

  /**
   * opens the default web browser with the authorization page
   * when granted, the access token will show up in the URL
   */
  private def _askForAccessToken(clientId: String, scope:String) =
    Desktop.getDesktop.browse(new URI(IdentityServiceClient.getAuthorizationUri_Implicit(clientId, scope, oauthUrl)))

  private def _askForAuthCode(clientId: String, scope: String) =
    Desktop.getDesktop.browse(new URI(IdentityServiceClient.getAuthorizationUri_AuthToken(clientId, scope, "about:blank")))

  private def _exchangeAuthCode(clientId: String, clientSecret: String, authCode: String): Unit ={
    val token = IdentityServiceClient.exchangeAuthCodeForAccessToken(authCode, clientId, clientSecret);
    println("Access token: "+token.token)
    if (token.refreshToken != null) println("Refresh token: "+token.refreshToken)
  }

  private def _refreshAccessToken(clientId: String, clientSecret: String, refreshToken: String): Unit = {
    val token = IdentityServiceClient.refreshAccessToken(refreshToken, clientId, clientSecret);
    println("Access token: "+token.token)
    if (token.refreshToken != null) println("Refresh token: "+token.refreshToken)
  }

  private def _verifyAccessToken(token: String) {
    val accessToken = new OAuthAccessToken(token, null, oauthUrl)
    IdentityServiceClient.verifyAccessToken(admin, accessToken) match {
      case Right(tokenInfo) => println("token for "+ tokenInfo.getAuthorizedUser+" accepted with scope "+ tokenInfo.getScope.mkString(", "))
      case Left(error) => println("token rejected: "+error)
    }
  }

  private def _addUser(name: String, email: Option[String]): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    IdentityServiceClient.addUser(realm,
    // TODO: other claims, not just --email
      new CarbonIdentityClaimBuilder().withEmail(email)
        .buildUserInfo(name),
      null, "password", true)
  }

  private def _showUserInfo(name: String): Unit ={
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



  private def _checkPassword(name: String, password: String): Unit ={
    val realm = IdentityServiceClient.loginToAdminServices(admin);
    if (IdentityServiceClient.authenticateUser(realm, name, password)){
      println("password accepted")
    }else{
      println("password rejected")
    }
  }



}
