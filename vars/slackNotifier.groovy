#!/usr/bin/env groovy

def call(String buildResult) {
	println env.JOB_NAME

	if ( buildResult == "SUCCESS" ) {
	    slackSend color: 'good', message: 'Job: ${JOB_NAME} BuildNumber: ${BUILD_NUMBER} was successful.'
	}
}