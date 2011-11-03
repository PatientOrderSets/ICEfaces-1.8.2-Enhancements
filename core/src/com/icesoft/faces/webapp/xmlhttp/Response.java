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
 *
 */

package com.icesoft.faces.webapp.xmlhttp;

/**
 * The <code>Response</code> class represents a (pseudo-)response ready to be
 * send to the client. </p>
 * <p/>
 * Please note that this <code>Response</code> does not represent an HTTP
 * Response message! </p>
 */
public class Response
        implements Comparable {
    private String iceFacesId;
    private String viewNumber;
    private long sequenceNumber;
    private String entityBody;

    /**
     * Constructs an "empty" <code>Response</code> object. That is a Response
     * without an Entity-Body. </p>
     *
     * @param iceFacesId     the new ICEfaces ID.
     * @param viewNumber     the new view number.
     * @param sequenceNumber the new sequence number.
     * @throws IllegalArgumentException if the specified
     *                                  <code>iceFacesId</code> is either
     *                                  <code>null</code> or empty.
     * @throws IllegalArgumentException if the specified
     *                                  <code>sequenceNumber</code> is lesser
     *                                  than or equal to <code>0</code>.
     * @throws IllegalArgumentException if the specified <code>viewNumber</code>
     *                                  is either <code>null</code> or empty.
     * @see #Response(String,String,long,String)
     * @see #getICEfacesID()
     * @see #getViewNumber()
     * @see #getSequenceNumber()
     * @see #getEntityBody()
     */
    public Response(String iceFacesId, String viewNumber, long sequenceNumber)
            throws IllegalArgumentException {
        this(iceFacesId, viewNumber, sequenceNumber, "");
    }

    /**
     * Constructs a <code>Response</code> object with the specified
     * <code>entityBody</code>. </p>
     *
     * @param iceFacesId     the new ICEfaces ID.
     * @param viewNumber     the new view number.
     * @param sequenceNumber the new sequence number.
     * @param entityBody     the new Entity-Body.
     * @throws IllegalArgumentException if the specified <code>iceFacesId</code>
     *                                  is either <code>null</code> or empty.
     * @throws IllegalArgumentException if the specified
     *                                  <code>sequenceNumber</code> is lesser
     *                                  than or equal to <code>0</code>.
     * @throws IllegalArgumentException if the specified <code>viewNumber</code>
     *                                  is either <code>null</code> or empty.
     * @see #Response(String,String,long)
     * @see #getICEfacesID()
     * @see #getViewNumber()
     * @see #getSequenceNumber()
     * @see #getEntityBody()
     */
    public Response(
            String iceFacesId, String viewNumber, long sequenceNumber,
            String entityBody)
            throws IllegalArgumentException {
        if (iceFacesId == null) {
            throw new IllegalArgumentException("iceFacesId is null");
        }
        if (iceFacesId.trim().length() == 0) {
            throw new IllegalArgumentException("iceFacesId is empty");
        }
        if (viewNumber == null) {
            throw new IllegalArgumentException("viewNumber is null");
        }
        if (viewNumber.trim().length() == 0) {
            throw new IllegalArgumentException("viewNumber is empty");
        }
        if (sequenceNumber <= 0) {
            throw new IllegalArgumentException("sequenceNumber <= 0");
        }
        this.iceFacesId = iceFacesId;
        this.viewNumber = viewNumber;
        this.sequenceNumber = sequenceNumber;
        this.entityBody = entityBody != null ? entityBody : "";
    }

    public int compareTo(Object object)
            throws ClassCastException {
        if (!(object instanceof Response)) {
            throw new ClassCastException("object is not a Response");
        }
        Response _response = (Response) object;
        int _result;
        if ((_result = iceFacesId.compareTo(_response.iceFacesId)) != 0) {
            return _result;
        }
        if ((_result = viewNumber.compareTo(_response.viewNumber)) != 0) {
            return _result;
        }
        if (sequenceNumber < _response.sequenceNumber) {
            return -1;
        } else if (sequenceNumber > _response.sequenceNumber) {
            return 1;
        }
        return 0;
    }

    /**
     * Gets the Entity-Body of this <code>Response</code>. </p>
     *
     * @return the Entity-Body.
     */
    public String getEntityBody() {
        return entityBody;
    }

    /**
     * Gets the ICEfaces ID of this <code>Response</code>. </p>
     *
     * @return the ICEfaces ID.
     */
    public String getICEfacesID() {
        return iceFacesId;
    }

    /**
     * Gets the sequence number of this <code>Response</code>. </p>
     *
     * @return the sequence number.
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Gets the view number of this <code>Response</code>. </p>
     *
     * @return the view number.
     */
    public String getViewNumber() {
        return viewNumber;
    }

    /**
     * Checks to see if this <code>Response</code> is an empty response. That
     * is, if the Entity-Body is empty. </p>
     *
     * @return <code>true</code> if this <code>Response</code> is empty.
     */
    public boolean isEmpty() {
        return entityBody.trim().length() == 0;
    }
}
