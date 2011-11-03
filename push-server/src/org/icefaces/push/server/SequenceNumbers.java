/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 */
package org.icefaces.push.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SequenceNumbers {
    private static final Log LOG = LogFactory.getLog(SequenceNumbers.class);

    private final Map sequenceNumberMap = new HashMap();

    public SequenceNumbers(final String[] xWindowCookieValues) {
        if (xWindowCookieValues != null && xWindowCookieValues.length != 0) {
            for (int i = 0; i < xWindowCookieValues.length; i++) {
                StringTokenizer _values =
                    new StringTokenizer(xWindowCookieValues[i], ";");
                while (_values.hasMoreTokens()) {
                    String _value = _values.nextToken().trim();
                    if (_value.startsWith("Sequence_Numbers")) {
                        StringTokenizer _sequenceNumbers =
                            new StringTokenizer(
                                _value.substring(
                                    _value.indexOf("\"") + 1,
                                    _value.lastIndexOf("\"")),
                                ",");
                        while (_sequenceNumbers.hasMoreTokens()) {
                            String _token =
                                _sequenceNumbers.nextToken();
                            int _index = _token.indexOf(':');
                            try {
                                String _iceFacesId =
                                    _token.substring(0, _index);
                                Long _sequenceNumber =
                                    Long.valueOf(
                                        _token.substring(_index + 1));
                                sequenceNumberMap.put(
                                    _iceFacesId, _sequenceNumber);
                            } catch (NumberFormatException exception) {
                                // do nothing.
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public Long get(final String iceFacesId) {
        return (Long)sequenceNumberMap.get(iceFacesId);
    }

    public boolean isEmpty() {
        return sequenceNumberMap.isEmpty();
    }

    public String toString() {
        StringBuffer _string = new StringBuffer();
        Iterator _entries = sequenceNumberMap.entrySet().iterator();
        while (_entries.hasNext()) {
            Map.Entry _entry = (Map.Entry)_entries.next();
            if (_string.length() != 0) {
                _string.append(",");
            }
            _string.
                append(_entry.getKey()).append(":").append(_entry.getValue());
        }
        return _string.toString();
    }
}
