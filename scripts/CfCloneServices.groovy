/* Copyright 2011-2012 SpringSource.
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

import org.cloudfoundry.client.lib.CloudApplication

/**
 * @author Burt Beckwith
 */

includeTargets << new File("$cloudFoundryPluginDir/scripts/CfRestart.groovy")

USAGE = '''
grails cf-clone-services <sourceAppName> <destAppName>
'''

target(cfCloneServices: 'Clone service bindings from one application to another') {
	depends cfInit

	doWithTryCatch {

		String sourceName = getRequiredArg(0)
		String destName = getRequiredArg(1)

		CloudApplication src = getApplication(sourceName)

		if (!src.services) {
			errorAndDie 'No services to clone'
		}

		CloudApplication dest = getApplication(destName)

		for (String serviceName : src.services) {
			println "\nCreating new service binding to '$serviceName' for '$dest.name'.\n"
			client.bindService dest.name, serviceName
		}

		argsList.clear()
		argsMap.appname = dest.name
		cfRestart()
	}
}

setDefaultTarget cfCloneServices
