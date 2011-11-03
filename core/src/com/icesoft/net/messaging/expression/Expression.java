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
package com.icesoft.net.messaging.expression;

import com.icesoft.net.messaging.Message;

/**
 * <p>
 *   Usage:
 *   <pre>
 *    // Expression:
 *    //     firstName = 'Jim'
 *    new Equal(
 *        new Identifier("firstName"),
 *        new StringLiteral("Jim"));
 *    // Expression:
 *    //     firstName = 'Jim' AND lastName = 'Raynor'
 *    new And(
 *        new Equal(
 *            new Identifier("firstName"),
 *            new StringLiteral("Jim")),
 *        new Equal(
 *            new Identifier("lastName"),
 *            new StringLiteral("Raynor")));
 *    // Expression:
 *    //     firstName = 'Jim' AND
 *    //     (
 *    //         productName = 'ICEfaces' OR
 *    //         productName = 'ICEbrowser'
 *    //     )
 *    new And(
 *        new Equal(
 *            new Identifier("firstName"),
 *            new StringLiteral("Jim")),
 *        new Container(
 *            new Or(
 *                new Equal(
 *                    new Identifier("productName"),
 *                    new StringLiteral("ICEfaces")),
 *                new Equal(
 *                    new Identifier("productName"),
 *                    new StringLiteral("ICEbrowser")))));
 *   </pre>
 * </p>
 */
public abstract class Expression
implements Operand {
    /**
     * <p>
     *   Evaluates the specified <code>message</code> against this Expression.
     * </p>
     *
     * @param      message
     *                 the Message to be evaluated.
     * @return     <code>true</code> if the message matches this Expression,
     *             <code>false</code> if not.
     * @throws     IllegalArgumentException
     *                 if the specified <code>message</code> is
     *                 <code>null</code>.
     */
    public abstract boolean evaluate(final Message message)
    throws IllegalArgumentException;
}
