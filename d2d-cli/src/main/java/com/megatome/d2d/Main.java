/*
 * Copyright 2018 Megatome Technologies, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.megatome.d2d;

import com.megatome.d2d.exception.BuilderException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static com.megatome.d2d.util.LogUtility.log;
import static com.megatome.d2d.util.LogUtility.setVerbose;

public class Main {
    public static void main(String... args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<String> docsetName = parser.accepts("name", "Name of the generated docset").withRequiredArg().ofType(String.class).required();
        final OptionSpec<File> docRoot = parser.accepts("doc", "Directory containing documentation to bundle in the docset.").withRequiredArg().ofType(File.class).required();
        final OptionSpec<File> outputLocation = parser.accepts("out", "Directory where the docset will be created.").withRequiredArg().ofType(File.class).defaultsTo(FileUtils.getFile("."));
        final OptionSpec<String> displayName = parser.accepts("displayName", "Name to show for the docset in Dash. Defaults to value of 'name' if not specified.").withRequiredArg().ofType(String.class);
        final OptionSpec<String> keyword = parser.accepts("keyword", "Keyword to use for the docset in Dash. Defaults to value of 'name' if not specified.").withRequiredArg().ofType(String.class);
        final OptionSpec<File> iconFile = parser.accepts("icon", "Icon file to use for the docset. No icon will be used if not specified.").withRequiredArg().ofType(File.class).describedAs("32x32 PNG");
        final OptionSpec<String> implementationType = parser.accepts("type", "Converter type to be used. Supports 'javadoc' and 'jsdoc'. (defaults: 'javadoc')").withRequiredArg().ofType(String.class);
        final OptionSpec<Void> verbose = parser.accepts("verbose", "Show more information");
        final OptionSpec<Void> help = parser.acceptsAll(Arrays.asList("h", "?"), "Show help").forHelp();

        final OptionSet options;
        try {
            options = parser.parse(args);
            if (options.has(help)) {
                usage(parser);
                return;
            }
        } catch (OptionException e) {
            usage(parser);
            return;
        }

        setVerbose(options.has(verbose));
        final DocsetCreator.Builder builder = new DocsetCreator.Builder(
                options.valueOf(docsetName),
                options.valueOf(docRoot)
        )
                .displayName(options.valueOf(displayName))
                .displayName(options.valueOf(keyword))
                .iconFile(options.valueOf(iconFile))
                .outputDirectory(options.valueOf(outputLocation))
                .implementation(options.valueOf(implementationType));
        final DocsetCreator docsetCreator = builder.build();
        try {
            docsetCreator.makeDocset();
        } catch (BuilderException e) {
            log("Failed to create docset: {}", e.getMessage());
        }
    }

    private static void usage(OptionParser parser) {
        try {
            parser.printHelpOn(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
