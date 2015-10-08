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

  private lazy val session = IdentityServiceClient.loginToAdminServices(admin)

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
      val deleteUser = new Subcommand("deleteUser") with Runnable {
        val name = trailArg[String]()
        override def run() = _deleteUser(name())
      }
      val deleteClaims = new Subcommand("deleteClaims") with Runnable {
        val name = trailArg[String]()
        val claims = trailArg[List[String]]()
        override def run() = _deleteClaims(name(), claims())
      }
      val updateClaim = new Subcommand("updateClaim") with Runnable {
        val name = trailArg[String]()
        val claim = trailArg[String]()
        val value = trailArg[String]()
        override def run() = _updateClaim(name(), claim(), value())
      }

      val showUserInfo = new Subcommand("showUserInfo") with Runnable{
        val name = trailArg[String](required = false)
        val email = opt[String]()
        override def run() = _showUserInfo(name.get, email.get)
      }
      val checkPassword = new Subcommand("checkPassword") with Runnable{
        val name = trailArg[String]()
        val password = trailArg[String]()
        override def run() = _checkPassword(name(), password())
      }
      val changePassword = new Subcommand("changePassword") with Runnable {
        val name = trailArg[String]()
        val password = trailArg[String]()
        override def run() = _changePassword(name(), password())
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

  private def _addUser(name: String, email: Option[String]) =
    IdentityServiceClient.addUser(session,
    // TODO: other claims, not just --email
      new CarbonIdentityClaimBuilder().withEmail(email)
        .buildUserInfo(name),
      null, "password", true)


  private def _deleteUser(name: String) =
    IdentityServiceClient.deleteUser(session, name)

  private def _deleteClaims(name: String, claims: List[String]) =
    IdentityServiceClient.deleteClaims(session, name, null, claims:_*)

  private def _updateClaim(name: String, claim: String, value: String) =
    IdentityServiceClient.updateClaims(session, name, null, Map(claim -> value))

  private def _showUserInfo(name: Option[String], email: Option[String]) {
    val userInfo = name match {
      case Some(name) => IdentityServiceClient.getUserInfo(session, name)
      case None => IdentityServiceClient.getUserInfoByUniqueEmail(session, email.get)
    }

    userInfo match {
      case Some(user) => {
        println(user.name);
        println(user.claims)
        println(user.roles.toList)
      }
      case None =>
        println("no such user")
    }

  }



  private def _checkPassword(name: String, password: String) =
    if (IdentityServiceClient.authenticateUser(session, name, password)){
      println("password accepted")
    }else{
      println("password rejected")
    }


  private def _changePassword(name: String, password: String) =
    IdentityServiceClient.changePassword(session, name, password)



}
