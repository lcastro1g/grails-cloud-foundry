/* Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.vmware.appcloud.client.CloudApplication
import com.vmware.appcloud.client.CloudApplication.AppState

/**
 * @author Burt Beckwith
 */

includeTargets << new File("$cloudFoundryPluginDir/scripts/_CfCommon.groovy")

USAGE = '''
grails cf-start [--appname]
'''

target(cfStart: 'Start an application') {
	depends cfInit

	doWithTryCatch {

		CloudApplication application = getApplication()

		if (application.state == AppState.STARTED) {
			println "\nApplication '$application.name' is already running.\n"
			return
		}

		client.startApplication application.name

		int count = 0
		int logLinesDisplayed = 0

		println "\nTrying to start Application: '$application.name'."

		while (true) {
			print '.'
			sleep 500
			try {
				if (appStartedProperly(count > 6)) {
					println ''
					break
				}

				if (client.getCrashes(application.name).crashes) {
					println "\n\nERROR - Application '$application.name' failed to start, logs information below.\n\n"

					for (log in CRASH_LOG_NAMES) {
						displayLog log, 0, false
					}

					if (isPush) {
						println ''
						if ('y'.equalsIgnoreCase(ask('Should I delete the application?', 'y,n', 'n'))) {
							deleteApplication false
						}
					}

					return
				}

				if (count > 29) {
					logLinesDisplayed = grabStartupTail(logLinesDisplayed)
				}
			}
			catch (IllegalArgumentException e) {
				throw e
			}
			catch (e) {
				print e.message
			}

			if (++count > 600) { // 5 minutes
				errorAndDie "Application is taking too long to start, check your logs"
			}
		}

		println "\nApplication '$application.name' started.\n"
	}
}

boolean appStartedProperly(boolean errorOnHealth) {
	CloudApplication application = getApplication(getAppName(), true)
	if (!application) {
		errorAndDie "Application '${getAppName()}'s state is undetermined, not enough information available at this time."
	}

	String health = describeHealth(application)
	if ('RUNNING'.equals(health)) {
		boolean test = cfConfig.testStartWithGet instanceof Boolean ? cfConfig.testStartWithGet : true
		if (!test) {
			return true
		}

		String url = cfConfig.testStartGetUrl ?: 'http://' + application.uris[0]
		try {
			new URL(url).text
			return true
		}
		catch (IOException e) {
			return false
		}
	}

	if ('N/A'.equals(health)) {
		// Health manager not running.
		if (errorOnHealth) {
			errorAndDie "\nApplication '$application.name's state is undetermined, not enough information available at this time."
		}
	}

	false
}

int grabStartupTail(int since) {
	try {
		int newLines = 0
		String content = getFile(0, 'logs/startup.log')
		if (content) {
			if (since == 0) {
				println "\n==== displaying startup log ====\n\n"
			}
			def lines = content.readLines()
			def tail = lines[since, lines.size()]
			newLines = tail.size()
			tail.each { println it }
		}

		since + newLines
	}
	catch (e) {
		println "Problem retrieving startup.log: $e.message"
	}
}

setDefaultTarget cfStart
