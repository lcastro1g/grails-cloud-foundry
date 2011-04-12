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

/**
 * @author Burt Beckwith
 */

includeTargets << new File("$cloudFoundryPluginDir/scripts/_CfCommon.groovy")

USAGE = '''
grails cf-get-file <path> [destination] [--appname] [--instance]
'''

target(cfGetFile: 'Download a file') {
	depends cfInit

	doWithTryCatch {
		String path = getRequiredArg()
		String destination = argsList[1]
		int instanceIndex = (argsMap.instance ?: 0).toInteger()

		if (path.startsWith('/')) {
			path = path[1..-1]
		}

		String content = getFile(instanceIndex, path)
		if (destination) {
			new File(destination).withWriter { it.write content }
			println "\nWrote $path to $destination\n"
		}
		else {
			println ''
			println content
			println ''
		}
	}
}

setDefaultTarget cfGetFile
