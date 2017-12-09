#!/usr/bin/env groovy

def call(String buildResult) {
	if ( buildResult == "SUCCESS" ) {
	    slackSend color: "good", message: "Job: ${env.JOB_NAME} BuildNumber ${env.BUILD_NUMBER} was successful"
	}
}