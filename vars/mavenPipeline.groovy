def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent any

        environment {
            VAR = 'test'        
        }

        parameters { booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Check if you want to skip tests') }

        stages {
            stage('checkout git') {
	        steps {
                    git branch: pipelineParams.branch, credentialsId: pipelineParams.scmCredentials, url: pipelineParams.scmUrl
                }
            }
 
            stage('test') {
                steps {
                    echo env.VAR
                } 
            }

            stage('build') {
                steps {	
                    sh 'mvn clean package -DskipTests=${EXECUTE_TESTS}'
                }
            }

            stage('Upload') {              
                steps {
                    script {
                        def server = Artifactory.server "jfrog-artifactory"
                        def rtMaven = Artifactory.newMavenBuild()
                        rtMaven.deployer server: server, releaseRepo: 'company-release', snapshotRepo: 'company-snapshot'
                        rtMaven.tool = 'Maven 3.3.9'
                        def buildInfo = rtMaven.run pom: '${POMPATH}', goals: 'clean install'
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
