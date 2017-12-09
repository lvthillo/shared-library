#!/usr/bin/env groovy

def call(String buildResult) {
	println env.JOB_NAME	
	println currentBuild.currentResult
	println "hey"
	if ( buildResult == "SUCCESS" ) {
	    slackSend color: 'good', message: 'Job: env.JOB_NAME BuildNumber env.BUILD_NUMBER was successful'
	}
}