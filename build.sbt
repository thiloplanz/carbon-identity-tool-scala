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

name := "carbon-identity-tool-scala"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"


val axis2Version = "1.6.3"
val carbonIdentityVersion = "4.5.6"


resolvers += "WSO2 Releases" at "http://maven.wso2.org/nexus/content/repositories/wso2-public/"

libraryDependencies += "org.apache.axis2" % "axis2-adb" % axis2Version
libraryDependencies += "org.apache.axis2" % "axis2-transport-local" % axis2Version
libraryDependencies += "org.apache.axis2" % "axis2-transport-http" % axis2Version

libraryDependencies += "commons-codec" % "commons-codec" % "1.10"
libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "org.apache.oltu.oauth2" % "org.apache.oltu.oauth2.client" % "1.0.0"


libraryDependencies += ("org.wso2.carbon.identity" % "org.wso2.carbon.identity.oauth.stub" % carbonIdentityVersion
  // exclude the WSO2-modified version of Axis2
  exclude("org.apache.axis2.wso2", "axis2")
  exclude("org.apache.axis2.wso2", "axis2-client")
  )

libraryDependencies += ("org.wso2.carbon" % "org.wso2.carbon.um.ws.api" % "4.2.2"
  // this pulls in much more than a web service client needs, so exclude stuff
  exclude("org.wso2.carbon", "carbon-kernel")
  exclude("org.wso2.carbon", "org.wso2.carbon.logging")
  exclude("org.wso2.carbon", "org.wso2.carbon.core")
  exclude("org.wso2.carbon", "org.wso2.carbon.core.common")
  exclude("org.wso2.carbon", "org.wso2.carbon.user.mgt.common")
  exclude("org.wso2.carbon", "org.wso2.carbon.base")
  exclude("org.wso2.carbon", "org.wso2.carbon.utils")
  exclude("org.wso2.carbon", "org.wso2.carbon.bootstrap")
  exclude("org.wso2.carbon", "org.wso2.carbon.queuing")
  exclude("org.wso2.carbon", "org.wso2.carbon.registry.core")
  exclude("org.wso2.carbon", "org.wso2.carbon.ndatasource.rdbms")
  exclude("org.wso2.carbon", "javax.cache.wso2")
  exclude("org.wso2.carbon", "org.wso2.carbon.um.ws.api.stub")
  exclude("org.wso2.securevault", "org.wso2.securevault")
  exclude("org.eclipse.equinox", "org.apache.log4j")
  exclude("org.eclipse.osgi", "org.eclipse.osgi")
  exclude("commons-dbcp.wso2", "commons-dbcp")
  exclude("commons-collections.wso2", "commons-collections")

  // exclude the WSO2-modified version of Axis2
  exclude("org.apache.axis2.wso2", "axis2")
  exclude("org.apache.axis2.wso2", "axis2-client")
  exclude("wsdl4j.wso2", "wsdl4j")
  )




