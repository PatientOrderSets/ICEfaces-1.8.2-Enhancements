/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package com.icesoft.jasper.xmlparser;

import com.icesoft.jasper.Constants;
import com.icesoft.jasper.compiler.Localizer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.InputStream;

public class CachedEntityResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {
        for (int i = 0; i < Constants.CACHED_DTD_PUBLIC_IDS.length; i++) {
            String cachedDtdPublicId = Constants.CACHED_DTD_PUBLIC_IDS[i];
            if (cachedDtdPublicId.equals(publicId)) {
                String resourcePath = Constants.CACHED_DTD_RESOURCE_PATHS[i];
                InputStream input =
                        this.getClass().getResourceAsStream(resourcePath);
                if (input == null) {
                    throw new SAXException(
                            Localizer.getMessage(
                                    "jsp.error.internal.filenotfound",
                                    resourcePath));
                }
                InputSource isrc = new InputSource(input);
                return isrc;
            }
        }
        ParserUtils.log.error(Localizer.getMessage(
                "jsp.error.parse.xml.invalidPublicId",
                publicId));
        return null;
    }
}

