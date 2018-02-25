package com.megatome.d2d.support;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.support.DocSetSupport;

import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.megatome.d2d.support.DocSetSupport.*;
import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.*;

public class DocSetSupportTest {
    private static final File javadocLocation = getFile(System.getProperty("d2d-sample-javadoc"));

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createDocsetDir() throws Exception {
        createAndVerifyDocsetStructure("TestDoc");
    }

    @Test
    public void overwriteExistingDocsetDir() throws Exception {
        final String docsetDir = getDocsetRoot(createAndVerifyDocsetStructure("TestDoc"));

        final File testFile = getFile(docsetDir, "foo.txt");
        write(testFile, "Test Data");
        assertTrue(testFile.exists());

        createAndVerifyDocsetStructure("TestDoc");
        assertFalse(testFile.exists());
    }

    @Test
    public void testCopyFiles() throws Exception {
        final String docsetDir = createAndVerifyDocsetStructure("TestDoc");

        final File destDir = getFile(getDocsetRoot(docsetDir), CONTENTS, RESOURCES, DOCUMENTS);
        copyFiles(javadocLocation, docsetDir);
        final Collection<String> originalFiles = buildFileCollectionWithoutPath(javadocLocation);
        final Collection<String> copiedFiles = buildFileCollectionWithoutPath(destDir);

        assertEquals(originalFiles, copiedFiles);
    }

    @Test(expected = BuilderException.class)
    public void testCopyFilesError() throws Exception {
        final File sourceDir = getFile("src", "test", "resources", "javadoc", "index.html");
        final String fakeRoot = FilenameUtils.concat(temporaryFolder.getRoot().getPath(), "Foo");
        copyFiles(sourceDir, fakeRoot);
    }

    @Test
    public void testCreatePlist() throws Exception {
        final String docsetDir = createAndVerifyDocsetStructure("TestDoc");
        createPList("Identifier", "displayName", "keyword", "indexFile", docsetDir);
        final File plist = getFile(getDocsetRoot(docsetDir), CONTENTS, "Info.plist");
        assertNotNull(plist);
        assertTrue(plist.exists());

        final NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plist);
        verifyPlistEntry(rootDict, "CFBundleIdentifier", "Identifier");
        verifyPlistEntry(rootDict, "CFBundleName", "displayName");
        verifyPlistEntry(rootDict, "DocSetPlatformFamily", "keyword");
        verifyPlistEntry(rootDict, "dashIndexFilePath", "indexFile");
        verifyPlistEntry(rootDict, "DashDocSetFamily", "java");
        verifyPlistEntry(rootDict, "isDashDocset", "true");
    }

    @Test
    public void testCopyIconFile() throws Exception {
        final String docsetDir = createAndVerifyDocsetStructure("TestDoc");
        final File iconFile = getFile(getDocsetRoot(docsetDir), "icon.png");
        copyIconFile(null, docsetDir);
        assertFalse(iconFile.exists());
        final File expectedIconFile = getFile("src", "test", "resources", "blank.png");
        copyIconFile(expectedIconFile, docsetDir);
        assertTrue(iconFile.exists());
        assertTrue(contentEquals(expectedIconFile, iconFile));
    }

    private void verifyPlistEntry(NSDictionary rootDict, String keyName, String expectedValue) {
        final NSObject obj = rootDict.objectForKey(keyName);
        assertNotNull(obj);
        final String value = obj.toString();
        assertNotNull(value);
        assertEquals(expectedValue, value);
    }

    private String createAndVerifyDocsetStructure(String docsetName) throws Exception {
        final String docFileRoot = FilenameUtils.concat(temporaryFolder.getRoot().getPath(), docsetName);
        DocSetSupport.createDocSetStructure(docFileRoot);

        final String[] docsetDirs = {getDocsetRoot(docsetName), CONTENTS, RESOURCES, DOCUMENTS};

        File f = temporaryFolder.getRoot();
        for (final String dirName : docsetDirs) {
            f = getFile(f, dirName);
            assertTrue(f.exists());
            assertTrue(f.isDirectory());
        }
        return docFileRoot;
    }

    private Collection<String> buildFileCollectionWithoutPath(final File targetDir) {
        final Set<String> fileCollection = new HashSet<>();
        for (final File f : listFiles(targetDir, null, true)) {
            fileCollection.add(f.getPath().replaceFirst(targetDir.getPath(), ""));
        }

        return fileCollection;
    }
}
