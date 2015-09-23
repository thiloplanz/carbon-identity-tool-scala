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


libraryDependencies += ("org.wso2.carbon.identity" % "org.wso2.carbon.identity.oauth.stub" % carbonIdentityVersion
  // exclude the WSO2-modified version of Axis2
  exclude("org.apache.axis2.wso2", "axis2")
  exclude("org.apache.axis2.wso2", "axis2-client")
  )

