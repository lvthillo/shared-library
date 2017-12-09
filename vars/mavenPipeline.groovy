def call(body) {
	def rtMaven = ''
    def buildInfo = ''

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
            TEST = 'ok'      
        }

        parameters { booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Check if you want to skip tests') }

        stages {
            stage('Checkout git repository') {
	            steps {
                    git branch: pipelineParams.branch, credentialsId: pipelineParams.scmCredentials, url: pipelineParams.scmUrl
                }
            }

            stage('Maven Build') {
                steps {	
                    script {
                	    rtMaven = Artifactory.newMavenBuild()
                	    rtMaven.tool = 'Maven 3.5.2'
                        buildInfo = rtMaven.run pom: pipelineParams.pom goals: 'clean install --DskipTests=$SKIP_TESTS'
                    }
                }
            }

            stage('Upload') {              
                steps {
                    script {
                        def server = Artifactory.server "jfrog-artifactory"
                        rtMaven.deployer server: server, releaseRepo: 'company-release', snapshotRepo: 'company-snapshot'
                        server.publishBuildInfo buildInfo
                    }
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
