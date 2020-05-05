/*
 * Copyright 2012-2014 the original author or authors.
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

package org.springframework.boot.cli.command.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import joptsimple.OptionSet;

import org.springframework.boot.cli.util.ResourceUtils;
import org.springframework.util.Assert;

/**
 * Extract source file options (anything following '--' in an {@link OptionSet}).
 * 
 * @author Phillip Webb
 * @author Dave Syer
 * @author Greg Turnquist
 * @author Andy Wilkinson
 */
public class SourceOptions {

	private final List<String> sources;

	private final List<?> args;

	/**
	 * Create a new {@link SourceOptions} instance.
	 * @param options the source option set
	 */
	public SourceOptions(OptionSet options) {
		this(options, null);
	}

	/**
	 * Create a new {@link SourceOptions} instance.
	 * @param arguments the source arguments
	 */
	public SourceOptions(List<?> arguments) {
		this(arguments, null);
	}

	/**
	 * Create a new {@link SourceOptions} instance. If it is an error to pass options that
	 * specify non-existent sources, but the default paths are allowed not to exist (the
	 * paths are tested before use). If default paths are provided and the option set
	 * contains no source file arguments it is not an error even if none of the default
	 * paths exist).
	 * @param optionSet the source option set
	 * @param classLoader an optional classloader used to try and load files that are not
	 * found in the local filesystem
	 */
	public SourceOptions(OptionSet optionSet, ClassLoader classLoader) {
		this(optionSet.nonOptionArguments(), classLoader);
	}

	private SourceOptions(List<?> nonOptionArguments, ClassLoader classLoader) {
		List<String> sources = new ArrayList<String>();
		int sourceArgCount = 0;
		for (Object option : nonOptionArguments) {
			if (option instanceof String) {
				String filename = (String) option;
				if ("--".equals(filename)) {
					break;
				}
				List<String> urls = ResourceUtils.getUrls(filename, classLoader);
				for (String url : urls) {
					if (url.endsWith(".groovy") || url.endsWith(".java")) {
						sources.add(url);
					}
				}
				if ((filename.endsWith(".groovy") || filename.endsWith(".java"))) {
					if (urls.isEmpty()) {
						throw new IllegalArgumentException("Can't find " + filename);
					}
					else {
						sourceArgCount++;
					}
				}
			}
		}
		this.args = Collections.unmodifiableList(nonOptionArguments.subList(
				sourceArgCount, nonOptionArguments.size()));
		Assert.isTrue(sources.size() > 0, "Please specify at least one file");
		this.sources = Collections.unmodifiableList(sources);
	}

	public List<?> getArgs() {
		return this.args;
	}

	public String[] getArgsArray() {
		return this.args.toArray(new String[this.args.size()]);
	}

	public List<String> getSources() {
		return this.sources;
	}

	public String[] getSourcesArray() {
		return this.sources.toArray(new String[this.sources.size()]);
	}

}
