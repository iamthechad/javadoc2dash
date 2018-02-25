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
package com.megatome.d2d.support.jsdoc;

import static com.megatome.d2d.util.LogUtility.logVerbose;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.support.DocSetParserInterface;
import com.megatome.d2d.support.MatchTypeInterface;
import com.megatome.d2d.util.IndexData;
import com.megatome.d2d.util.SearchIndexValue;

import groovy.json.JsonSlurper;

public class JSDocSupport implements DocSetParserInterface {

    public static final String JSDOC_IMPLEMENTATION = "jsdoc";

    @Override
    public IndexData findIndexFile( File javadocDir ) throws BuilderException {
        final IndexData indexData = new IndexData();

        if( !javadocDir.exists() || !javadocDir.isDirectory() ) {
            throw new BuilderException( String.format( "%s does not exist, or is not a directory", javadocDir.getAbsolutePath() ) );
        }

        logVerbose( "Looking for index.json file" );

        File indexJson = new File( javadocDir, "index.json" );
        if( indexJson.exists() ) {
            indexData.addFileToIndex( new File( javadocDir, "index.json" ) );
        }

        if( new File( javadocDir, "index.html" ).exists() ) {
            indexData.setDocsetIndexFile( "index.html" );
        }

        if( !indexData.hasFilesToIndex() ) {
            throw new BuilderException( String.format( "Did not find any jsdoc files. Make sure that %s is a directory containing jsodc", javadocDir.getAbsolutePath() ) );
        }

        logVerbose( "Found jsdoc files" );

        return indexData;
    }

    @Override
    public List<SearchIndexValue> findSearchIndexValues( List<File> filesToIndex ) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();
        for( final File f : filesToIndex ) {
            final List<SearchIndexValue> indexValues = indexFile( f );
            values.addAll( indexValues );
        }
        return values;
    }

    @SuppressWarnings( "unchecked" )
    private static List<SearchIndexValue> indexFile( File f ) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();

        try {
            Map<String, Object> jsonObject = (Map<String, Object>)new JsonSlurper().parseText( readFileToString( f ) );

            jsonObject.forEach( ( String module, Object entries ) -> {
                MatchTypeInterface matchType = JSDocMatchType.type( module );
                if ( entries instanceof ArrayList ) {
                    ((ArrayList<Map<String, Object>>)entries).forEach( ( Map<String, Object> property ) -> {
                        values.add( new SearchIndexValue( (String)property.get( "name" ), matchType, link( (String)property.get( "link" ) ) ) );
                    } );
                }
            } );
        } catch( IOException e ) {
            throw new BuilderException( e.getLocalizedMessage() );
        }

        return values;
    }

    private static String link( String link ) {
        return link;
    }
}
