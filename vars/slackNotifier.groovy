#!/usr/bin/env groovy

def call() {
    def server = Artifactory.server "jfrog-artifactory"
    def rtMaven = Artifactory.newMavenBuild()
    rtMaven.deployer server: server, releaseRepo: 'company-release', snapshotRepo: 'company-snapshot'
    rtMaven.tool = 'Maven 3.5.2'
    def buildInfo = rtMaven.run pom: env.POM, goals: 'clean install'
    server.publishBuildInfo buildInfo
}