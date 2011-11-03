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
 * <p>The {@link RenderingException} exception is thrown whenever rendering does
 * not succeed. In this state, the client has not received the recent set of
 * updates but may or may not be able to receive updates in the future. The
 * application should consider different strategies for {@link
 * TransientRenderingException} and {@link FatalRenderingException}
 * subclasses.</p>
 */
public class RenderingException extends Exception {
    /**
     * <p>Construct a new exception with the specified detail message and no
     * root cause.</p>
     *
     * @param message The detail message for this exception
     */
    public RenderingException(String message) {
        super(message);
    }

    /**
     * <p>Construct a new exception with the specified detail message and root
     * cause.</p>
     *
     * @param message The detail message for this exception
     * @param cause   The root cause for this exception
     */
    public RenderingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Construct a new exception with the specified root cause.  The detail
     * message will be set to <code>(cause == null ? null :
     * cause.toString()</code>
     *
     * @param cause The root cause for this exception
     */
    public RenderingException(Throwable cause) {
        super(cause);
    }

}
