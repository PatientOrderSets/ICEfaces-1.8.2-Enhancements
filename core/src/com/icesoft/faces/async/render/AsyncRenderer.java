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

package com.icesoft.faces.async.render;

public interface AsyncRenderer extends Disposable {
    public boolean isBroadcasted();

    /**
     * Get the current name of this AsyncRenderer.  Names are used by the {@link
     * RenderManager} to manage the various Renderers so that they can be easily
     * accessed from different parts of the application.
     *
     * @return The current name of the AsyncRenderer.
     */
    public String getName();

    public void setBroadcasted(final boolean broadcasted);

    /**
     * Set the current name of this AsyncRenderer.  Names are used by the {@link
     * RenderManager} to manage the various Renderers so that they can be easily
     * accessed from different parts of the application.
     *
     * @param name The desired name of the renderer.
     */
    public void setName(final String name);

    /**
     * Used by the {@link RenderManager} to set the {@link RenderHub} for this
     * AsyncRenderer.
     *
     * @param renderHub The RenderHub to use for this AsyncRenderer
     */
//    public void setRenderHub(RenderHub renderHub);

    /**
     * Used by the {@link RenderManager} to provide a reference to itself for
     * this AsyncRenderer.
     *
     * @param renderManager The RenderManager to use for this AsyncRenderer
     * @deprecated
     */
    public void setRenderManager(final RenderManager renderManager);

    /**
     * Called by the RenderManager when the the application is shutting down to
     * allow the AsyncRenderer to clean up any resources (threads, collections,
     * etc).
     */
    public void dispose();

    /**
     * The method called by the application when it wants to requrest a render
     * pass for the one or more {@link Renderable}s handled by this
     * AsyncRenderer.
     */
    public void requestRender();

    /**
     * The method called by dispose to halt a render pass at the current {@link
     * Renderable}s.
     */
    public void requestStop();

}
