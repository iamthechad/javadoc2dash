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
package com.megatome.d2d;

import static com.megatome.d2d.support.DBSupport.createIndex;
import static com.megatome.d2d.support.DocSetSupport.copyFiles;
import static com.megatome.d2d.support.DocSetSupport.copyIconFile;
import static com.megatome.d2d.support.DocSetSupport.createDocSetStructure;
import static com.megatome.d2d.support.DocSetSupport.createPList;
import static com.megatome.d2d.support.DocSetSupport.getDBDir;
import static com.megatome.d2d.util.LogUtility.log;
import static com.megatome.d2d.util.LogUtility.setLogger;
import static org.apache.commons.io.FilenameUtils.concat;

import java.io.File;

import org.slf4j.Logger;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.support.DocSetParserInterface;
import com.megatome.d2d.support.javadoc.JavadocSupport;
import com.megatome.d2d.support.jsdoc.JSDocSupport;
import com.megatome.d2d.util.IndexData;

/**
 * Class responsible for creating the docset.
 */
public class DocsetCreator {
    private final String                docsetName;

    private final String                displayName;

    private final String                keyword;

    private final File                  iconFilePath;

    private final File                  javadocRoot;

    private final File                  outputDirectory;

    private final DocSetParserInterface implementation;

    /**
     * Builder for specifying options used in docset creation
     */
    public static class Builder {
        private final String          docsetName;

        private final File            javadocRoot;

        private String                displayName;

        private String                keyword;

        private File                  iconFilePath    = null;

        private File                  outputDirectory = new File( "." );

        private DocSetParserInterface implementation  = new JavadocSupport();

        /**
         * Ctor
         *
         * @param docsetName File name of docset to create
         * @param javadocRoot Root directory of the Javadoc to create the docset from
         */
        public Builder( String docsetName, File javadocRoot ) {
            if( null == docsetName || docsetName.isEmpty() ) {
                throw new IllegalArgumentException( "The docsetName must be specified" );
            }
            if( null == javadocRoot ) {
                throw new IllegalArgumentException( "The javadocRoot must be specified" );
            }
            this.docsetName = docsetName;
            this.javadocRoot = javadocRoot;
            this.displayName = docsetName;
            this.keyword = docsetName;
        }

        /**
         * Specify the display name
         *
         * @param displayName Name to display in Dash. Defaults to <code>docsetName</code> if unspecified
         * @return Builder instance
         */
        public Builder displayName( String displayName ) {
            if( null != displayName && !displayName.isEmpty() ) {
                this.displayName = displayName;
            }
            return this;
        }

        /**
         * Specify the keyword
         *
         * @param keyword Keyword to associate this docset with. Defaults to <code>docsetName</code> is unspecified
         * @return Builder instance
         */
        public Builder keyword( String keyword ) {
            if( null != keyword && !keyword.isEmpty() ) {
                this.keyword = keyword;
            }
            return this;
        }

        /**
         * Specify the output directory
         *
         * @param outputDirectory Location for the created docset
         * @return Builder instance
         */
        public Builder outputDirectory( File outputDirectory ) {
            if( null != outputDirectory ) {
                this.outputDirectory = outputDirectory;
            }
            return this;
        }

        /**
         * Specify the icon file
         *
         * @param iconFile Path to an icon to include in the docset. Should be a 32x32 PNG. No icon will be used if this is unspecified.
         * @return Builder instance
         */
        public Builder iconFile( File iconFile ) {
            if( null != iconFile ) {
                this.iconFilePath = iconFile;
            }
            return this;
        }

        /**
         * Specify the parser implementation
         *
         * @param implementation of the DocSetParserInterface
         * @return Builder instance
         */
        public Builder implementation( DocSetParserInterface implementation ) {
            if( null != implementation ) {
                this.implementation = implementation;
            }
            return this;
        }

        /**
         * Specify the parser implementation
         *
         * @param input string of the type of the implementation
         * @return Builder instance
         */
        public Builder implementation( String input ) {
            if( null == input ) {
                return this;
            }
            switch( input ) {
                case JSDocSupport.JSDOC_IMPLEMENTATION:
                    this.implementation = new JSDocSupport();
                    break;

                case JavadocSupport.JAVADOC_IMPLEMENTATION:
                    this.implementation = new JavadocSupport();
                    break;
                default:
                    // leave the way it was.
            }

            return this;
        }

        public DocsetCreator build() {
            return new DocsetCreator( this );
        }
    }

    private DocsetCreator( Builder builder ) {
        this.docsetName = builder.docsetName;
        this.displayName = builder.displayName;
        this.keyword = builder.keyword;
        this.iconFilePath = builder.iconFilePath;
        this.javadocRoot = builder.javadocRoot;
        this.outputDirectory = builder.outputDirectory;
        this.implementation = builder.implementation;
    }

    /**
     * Build the docset.
     *
     * @param logger Optional logger to be used during docset creation. If not specified all messages will be directed
     *            to the console.
     * @throws BuilderException If an error occurs creating the docset
     */
    public void makeDocset( Logger logger ) throws BuilderException {
        setLogger( logger );
        final String docsetRoot = concat( outputDirectory.getAbsolutePath(), docsetName );
        createDocSetStructure( docsetRoot );
        copyIconFile( iconFilePath, docsetRoot );
        final IndexData indexData = implementation.findIndexFile( javadocRoot );
        copyFiles( javadocRoot, docsetRoot );
        createPList( docsetName, displayName, keyword, indexData.getDocsetIndexFile(), docsetRoot );
        createIndex( implementation.findSearchIndexValues( indexData.getFilesToIndex() ), getDBDir( docsetRoot ) );
        log( "Finished creating docset: {}", docsetRoot );
    }

    /**
     * Build the docset.
     *
     * @throws BuilderException If an error occurs creating the docset
     */
    public void makeDocset() throws BuilderException {
        makeDocset( null );
    }

    /**
     * Get the docset name
     *
     * @return Docset name
     */
    public String getDocsetName() {
        return docsetName;
    }

    /**
     * Get the display name
     *
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the keyword
     *
     * @return Keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Get the icon file path
     *
     * @return Icon file path
     */
    public File getIconFilePath() {
        return iconFilePath;
    }

    /**
     * Get the Javadoc root directory
     *
     * @return Javadoc directory
     */
    public File getJavadocRoot() {
        return javadocRoot;
    }

    /**
     * Get the output directory
     *
     * @return Output directory
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Get the current implementation
     *
     * @return the implementation
     */
    public DocSetParserInterface getImplementation() {
        return implementation;
    }
}
