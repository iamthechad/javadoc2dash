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
package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.io.FilenameUtils.concat;
import static com.megatome.j2d.util.LogUtility.*;

/**
 * Utility class for operations on the docset.
 */
public class DocSetSupport {
    private DocSetSupport() {}

    private static final String CONTENTS = "Contents";
    private static final String RESOURCES = "Resources";
    private static final String DOCUMENTS = "Documents";

    private static final String PLIST_FILE = "Info.plist";
    private static final String ICON_FILE = "icon.png";
    private static final String DOCSET_SUFFIX = ".docset";

    /**
     * Create the docset package. Will delete an existing docset if one already exists at the specified location.
     * @param docsetDir Location of the docset to create
     * @throws BuilderException
     */
    public static void createDocSetStructure(String docsetDir) throws BuilderException {
        final File docsetRootDir = getFile(getDocsetRoot(docsetDir));
        // Create dir
        if (docsetRootDir.exists()) {
            logVerbose("A docset named {} already exists. Trying to remove.", docsetDir);
            try {
                deleteDirectory(docsetRootDir);
            } catch (IOException e) {
                throw new BuilderException("Failed to delete existing docset.", e);
            }
        }

        final File documentsDir = getFile(docsetRootDir, CONTENTS, RESOURCES, DOCUMENTS);
        try {
            forceMkdir(documentsDir);
        } catch (IOException e) {
            throw new BuilderException("Failed to create new docset directory.", e);
        }
        logVerbose("Docset directory structure created");
    }

    /**
     * Copy an icon file to the docset. If the file path is not specified, no error happens.
     * @param iconFile File to copy as the docset icon.
     * @param docsetDir Directory of the docset
     * @throws BuilderException
     */
    public static void copyIconFile(File iconFile, String docsetDir) throws BuilderException {
        if (null == iconFile) {
            return;
        }

        try {
            copyFile(iconFile, getFile(getDocsetRoot(docsetDir), ICON_FILE));
            logVerbose("Icon file copied");
        } catch (IOException e) {
            throw new BuilderException("Failed to copy icon file to docset", e);
        }
    }

    /**
     * Copy all files and folders from a source location into the docset.
     * @param sourceDir Source directory to copy from
     * @param docsetDir Directory of the docset
     * @throws BuilderException
     */
    public static void copyFiles(final String sourceDir, String docsetDir) throws BuilderException {
        copyFiles(getFile(sourceDir), docsetDir);
    }

    /**
     * Copy all files and folders from a source location into the docset.
     * @param sourceDir Source directory to copy from
     * @param docsetDir Directory of the docset
     * @throws BuilderException
     */
    public static void copyFiles(final File sourceDir, String docsetDir) throws BuilderException {
        try {
            copyDirectory(sourceDir, getFile(getDocsetRoot(docsetDir), CONTENTS, RESOURCES, DOCUMENTS));
            logVerbose("Copied javadoc files into docset");
        } catch (IOException e) {
            throw new BuilderException("Could not copy files into the docset", e);
        }
    }

    /**
     * Create the plist file in the docset.
     * @param bundleIdentifier Bundle identifier of the docset
     * @param displayName Name used to display the docset in Dash
     * @param keyword Keyword used for the docset in Dash
     * @param indexFile The file to be used as the docset index
     * @param docsetDir Directory of the docset
     * @throws BuilderException
     */
    public static void createPList(String bundleIdentifier, String displayName, String keyword, String indexFile, String docsetDir) throws BuilderException {
        final String plist = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\"><dict><key>CFBundleIdentifier</key><string>%s</string><key>CFBundleName</key><string>%s</string><key>DocSetPlatformFamily</key><string>%s</string><key>dashIndexFilePath</key><string>%s</string><key>DashDocSetFamily</key><string>java</string><key>isDashDocset</key><true/></dict></plist>",
                bundleIdentifier, displayName, keyword, indexFile);
        // CFBundleIdentifier = ?
        // CFBundleName = Display Name
        // DocSetPlatformFamily = keyword
        try {
            write(getFile(getDocsetRoot(docsetDir), CONTENTS, PLIST_FILE), plist);
            logVerbose("Created the plist file in the docset");
        } catch (IOException e) {
            throw new BuilderException("Failed to write plist file into docset", e);
        }
    }

    /**
     * Get the directory within the docset that holds the SQLite DB.
     * @param docsetDir Directory of the docset
     * @return Directory
     */
    public static String getDBDir(String docsetDir) {
        return concat(concat(getDocsetRoot(docsetDir), CONTENTS), RESOURCES);
    }

    private static String getDocsetRoot(String docsetDir) {
        return docsetDir + DOCSET_SUFFIX;
    }
}
