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

            /*stage('build') {
                steps {	
                    sh 'mvn clean package -DskipTests=true'
                }
            }*/
        }
    }
}
