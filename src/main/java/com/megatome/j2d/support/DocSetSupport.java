package com.megatome.j2d.support;

import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.exception.BuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;

public class DocSetSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DocSetSupport.class);

    private DocSetSupport() {}

    private static final String CONTENTS = "Contents";
    private static final String RESOURCES = "Resources";
    private static final String DOCUMENTS = "Documents";

    private static final String PLIST_FILE = "Info.plist";
    private static final String ICON_FILE = "icon.png";

    public static void createDocSetStructure(String docsetRoot, String docsetDir) throws BuilderException {
        // Create dir
        final File docsetRootDir = getFile(docsetDir);
        if (docsetRootDir.exists()) {
            LOG.info("A docset named {} already exists. Trying to remove.", docsetRoot);
            try {
                deleteDirectory(docsetRootDir);
            } catch (IOException e) {
                final String message = "Failed to delete existing docset.";
                LOG.error(message, e);
                throw new BuilderException(message, e);
            }
        }

        final File documentsDir = getFile(docsetRootDir, CONTENTS, RESOURCES, DOCUMENTS);
        try {
            forceMkdir(documentsDir);
        } catch (IOException e) {
            final String message = "Failed to create new docset directory.";
            LOG.error(message, e);
            throw new BuilderException(message, e);
        }
        LOG.info("Docset directory structure created");
    }

    public static void copyIconFile(String iconFilePath, String docsetDir) throws BuilderException {
        if (null == iconFilePath) {
            return;
        }

        try {
            copyFile(getFile(iconFilePath), getFile(docsetDir, ICON_FILE));
            LOG.info("Icon file copied");
        } catch (IOException e) {
            final String message = "Failed to copy icon file to docset";
            LOG.error(message, e);
            throw new BuilderException(message, e);
        }
    }

    public static void copyFiles(final String javadocDir, String docsetDir) throws BuilderException {
        try {
            copyDirectory(getFile(javadocDir), getFile(docsetDir, CONTENTS, RESOURCES, DOCUMENTS));
            LOG.info("Copied javadoc files into docset");
        } catch (IOException e) {
            throw new BuilderException("Could not copy files into the docset", e);
        }
    }

    public static void createPList(String bundleIdentifier, String displayName, String keyword, String docsetDir, IndexData indexData) throws BuilderException {
        final String plist = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\"><dict><key>CFBundleIdentifier</key><string>%s</string><key>CFBundleName</key><string>%s</string><key>DocSetPlatformFamily</key><string>%s</string><key>dashIndexFilePath</key><string>%s</string><key>DashDocSetFamily</key><string>java</string><key>isDashDocset</key><true/></dict></plist>",
                bundleIdentifier, displayName, keyword, indexData.getDocsetIndexFile());
        // CFBundleIdentifier = ?
        // CFBundleName = Display Name
        // DocSetPlatformFamily = keyword
        try {
            write(getFile(docsetDir, CONTENTS, PLIST_FILE), plist);
            LOG.info("Created the plist file in the docset");
        } catch (IOException e) {
            throw new BuilderException("Failed to write plist file into docset", e);
        }
    }

    public static File getDBDir(String docsetDir) {
        return getFile(docsetDir, CONTENTS, RESOURCES);
    }
}
