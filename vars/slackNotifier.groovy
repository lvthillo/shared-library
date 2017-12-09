#!/usr/bin/env groovy

def call(String buildResult) {
	println buildResult
	if ( buildResult == "SUCCES" ) {
	    slackSend channel: '#jenkins-latest', color: 'good', message: 'Job: ${env.JOB_NAME} BuildNumber ${env.BUILD_NUMBER} was succesful'
	}
}