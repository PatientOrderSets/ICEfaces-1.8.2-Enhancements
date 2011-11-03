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
package com.icesoft.net.messaging;

import java.io.Serializable;

public class ObjectMessage
extends AbstractMessage
implements Message {
    private Serializable object;

    public ObjectMessage() {
        super();
    }

    public ObjectMessage(final Serializable object) {
        setObject(object);
    }

    public void append(final Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage _objectMessage = (ObjectMessage)message;
            if (object instanceof Object[]) {
                Object[] _oldObject = (Object[])object;
                Object[] _newObject = new Object[_oldObject.length + 1];
                for (int i = 0; i < _oldObject.length; i++) {
                    _newObject[i] = _oldObject[i];
                }
                _newObject[_newObject.length - 1] = _objectMessage.getObject();
                object = _newObject;
            } else {
                object = new Object[] {object, _objectMessage.getObject()};
            }
        }
    }

    public void clearBody() {
        object = null;
    }

    public int getLength() {
        return -1;
    }

    /**
     * <p>
     *   Gets the Object contained in this ObjectMessage as its body.
     * </p>
     *
     * @return     the Object.
     * @see        #setObject(Serializable)
     */
    public Serializable getObject() {
        return object;
    }

    /**
     * <p>
     *   Sets the specified <code>object</code> as the body of this
     *   ObjectMessage.
     * </p>
     *
     * @param      object
     *                 the Object to be set as the body.
     * @see        #getObject()
     */
    public void setObject(final Serializable object) {
        this.object = object;
    }

    public String toString() {
        return
            new StringBuffer(super.toString()).
                append(getObject()).append("\r\n").toString();
    }
}
