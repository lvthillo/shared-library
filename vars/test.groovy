def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline { 
        agent any  
    
        parameters {
            choice(choices: 'miox\nmama', description: 'Which app?', name: 'APP')
        }
    
        stages { 
            stage('Checkout') { 
                steps { 
                   git branch: pipelineParams.branch, url: pipelineParams.scmURL
                }
            }
            
            stage('Prepare script'){
                steps{
                    withCredentials([[
                        $class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: 'aws-credentials',
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    ],
                    usernamePassword(credentialsId: 'creds-'+env.APP+'-id', usernameVariable: 'USER_NAME_2', passwordVariable: 'PASSWORD_2'),
                        ]){
                            sh './script.sh'
                    }   
                }
            }
        }
    }
}
