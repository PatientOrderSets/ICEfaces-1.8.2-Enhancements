/* This is borrowed Apache code, with only package name changes.
 * It does not and SHOULD NOT carry any ICEsoft copyright notice.
 */


/* Original Copyright
 * Copyright 2004 The Apache Software Foundation.
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
package com.icesoft.faces.component.datapaginator;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

/**
 * @version beta 1.0
 */
public class PaginatorActionEvent extends ActionEvent {
    private static final long serialVersionUID = -5692343289423906802L;

    private final String mScrollerfacet;

    private int mPageIndex;

    /**
     * @param component
     * @param scrollerfacet
     */
    public PaginatorActionEvent(UIComponent component, String scrollerfacet) {
        super(component);
        mScrollerfacet = scrollerfacet;
        mPageIndex = -1;
    }

    /**
     * @param component
     * @param pageIndex
     */
    public PaginatorActionEvent(UIComponent component, int pageIndex) {
        super(component);
        if (pageIndex < 0) {
            throw new IllegalArgumentException("wrong pageindex");
        }
        mPageIndex = pageIndex;
        mScrollerfacet = null;
    }

    /**
     * @return Returns the scrollerfacet.
     */
    public String getScrollerfacet() {
        return mScrollerfacet;
    }
    /**
     * @return int
     */
    public int getPageIndex() {
        return mPageIndex;
    }
    
    void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }
}
