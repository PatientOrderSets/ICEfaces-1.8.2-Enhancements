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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UpdatedViews
implements Comparable {
    private final String iceFacesId;
    private final long sequenceNumber;
    private final Set updatedViewsSet;

    public UpdatedViews(
        final String iceFacesId, final long sequenceNumber,
        final Set updatedViewsSet) {

        this.iceFacesId = iceFacesId;
        this.sequenceNumber = sequenceNumber;
        this.updatedViewsSet = updatedViewsSet;
    }

    public int compareTo(final Object object)
    throws ClassCastException {
        if (!(object instanceof UpdatedViews)) {
            throw
                new ClassCastException(
                    "object is not an instance of UpdatedViews");
        }
        UpdatedViews _updatedViews = (UpdatedViews)object;
        int _result = iceFacesId.compareTo(_updatedViews.iceFacesId);
        if (_result != 0) {
            return _result;
        } else if (sequenceNumber < _updatedViews.sequenceNumber) {
            return -1;
        } else if (sequenceNumber > _updatedViews.sequenceNumber) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean contains(final String viewNumber) {
        return updatedViewsSet.contains(viewNumber);
    }

    public String getICEfacesID() {
        return iceFacesId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Set getUpdatedViewsSet() {
        return Collections.unmodifiableSet(updatedViewsSet);
    }

    public static UpdatedViews merge(
        final UpdatedViews updatedViews1, final UpdatedViews updatedViews2) {

        UpdatedViews _updatedViews;
        if (!updatedViews1.iceFacesId.equals(updatedViews2.iceFacesId)) {
            _updatedViews = null;
        } else {
            Set _updatedViewsSet = new HashSet(updatedViews1.updatedViewsSet);
            _updatedViewsSet.addAll(updatedViews2.updatedViewsSet);
            _updatedViews =
                new UpdatedViews(
                    updatedViews1.iceFacesId,
                    Math.max(
                        updatedViews1.sequenceNumber,
                        updatedViews2.sequenceNumber),
                    _updatedViewsSet);
        }
        return _updatedViews;
    }

    public boolean remove(final String viewNumber) {
        return updatedViewsSet.remove(viewNumber);
    }

    public int size() {
        return updatedViewsSet.size();
    }

    public String toString() {
        StringBuffer _string = new StringBuffer();
        _string.
            append("UpdatedViews [ICEfaces ID: ").append(iceFacesId).
            append(", Sequence Number: ").append(sequenceNumber).
            append(", View Numbers: ");
        int _size = updatedViewsSet.size();
        Iterator _updatedViewsIterator = updatedViewsSet.iterator();
        for (int i = 0; i < _size; i++) {
            if (i != 0) {
                _string.append(", ");
            }
            _string.append(_updatedViewsIterator.next());
        }
        _string.append("]");
        return _string.toString();
    }
}
