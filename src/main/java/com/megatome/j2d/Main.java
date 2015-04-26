/**
 * Copyright 2015 Megatome Technologies, LLC
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
package com.megatome.j2d;

public class Main {
    public static void main(String... args) {
        final String docsetName = args[0];
        final String javadocRoot = args[1];
        final String displayName = (args.length >= 3) ? args[2] : null;
        final String keyword = (args.length >= 4) ? args[3] : null;
        final String iconFilePath = (args.length >= 5) ? args[4] : null;
        final Builder builder = new Builder(docsetName, javadocRoot, displayName, keyword, iconFilePath);
        builder.build();
    }
}
