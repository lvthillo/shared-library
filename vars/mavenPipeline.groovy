def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any

        options {
            buildDiscarder(logRotator(numToKeepStr: '3'))
        }

        environment {
            POM = 'pom.xml'        
        }

        parameters { booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Check if you want to skip tests') }

        stages {
            stage('checkout git') {
	        steps {
                    git branch: pipelineParams.branch, credentialsId: pipelineParams.scmCredentials, url: pipelineParams.scmUrl
                }
            }

            stage('build') {
                steps {	
                    sh 'mvn clean package -f ${POM} -DskipTests=${SKIP_TESTS}'
                }
            }

            /*stage('Upload') {              
                steps {
                    script {
                        def server = Artifactory.server "jfrog-artifactory"
                        def rtMaven = Artifactory.newMavenBuild()
                        rtMaven.deployer server: server, releaseRepo: 'company-release', snapshotRepo: 'company-snapshot'
                        rtMaven.tool = 'Maven 3.5.2'
                        def buildInfo = rtMaven.run pom: env.POM, goals: 'clean install'
                        server.publishBuildInfo buildInfo
                    }
                }
            }*/

            stage('Upload to Artifactory') {
                steps {
                    upload()
                }
            }


            stage('Clean') {
                steps {
                    cleanWs()
                }
            }
        }
    }
}
