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

import com.icesoft.net.messaging.expression.Expression;

/**
 * <p>
 *   The MessageSelector class is a convenience class for building message
 *   selectors as defined by the Java Message Service specification. Even though
 *   it is specified by JMS, the message selector mechanism seems abstract
 *   enough to be used for other message services.
 * </p>
 * <p>
 *   Each MessageSelector contains an Expression construct that represents the
 *   actual message selector expression as defined by JMS. The MessageSelector
 *   can determine if a particular Message matches the contained expression by
 *   using the <code>matches(Message)</code> method.
 * </p>
 *
 * @see        Message
 * @see        Expression
 */
public class MessageSelector {
    private Expression expression;

    public MessageSelector(final Expression expression)
    throws IllegalArgumentException {
        if (expression == null) {
            throw new IllegalArgumentException("expression is null");
        }
        this.expression = expression;
    }

    /**
     * <p>
     *   Gets the Expression of this MessageSelector.
     * </p>
     *
     * @return     the Expression.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * <p>
     *   Determines if the specified <code>message</code> matches this
     *   MessageSelector's expression.
     * </p>
     *
     * @param      message
     *                 the Message to be tested.
     * @return     <code>true</code> if there is a match, <code>false</code> if
     *             not.
     */
    public boolean matches(final Message message) {
        return expression.evaluate(message);
    }

    public String toString() {
        return expression.toString();
    }
}
