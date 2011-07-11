/* Copyright 2011 SpringSource.
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

import com.vmware.appcloud.client.CloudInfo

/**
 * @author Burt Beckwith
 */

includeTargets << new File("$cloudFoundryPluginDir/scripts/_CfCommon.groovy")

USAGE = '''
grails cf-frameworks
'''

target(cfFrameworks: 'Display the recognized frameworks of the target system') {
	depends cfInit

	doWithTryCatch {

		client.loginIfNeeded()
		CloudInfo cloudInfo = client.cloudInfo

		displayInBanner(['Name'], cloudInfo.frameworks.sort { it.name }, [{ it.name }], false)
	}
}

setDefaultTarget cfFrameworks
