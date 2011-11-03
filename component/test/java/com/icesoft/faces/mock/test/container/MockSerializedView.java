/*
 * tree structure and component state
 */

package com.icesoft.faces.mock.test.container;

import java.io.Serializable;

/**
 *
 * @author fye
 */
public class MockSerializedView extends Object implements Serializable{

    private Object treeStructure;
    private Object state;

    public MockSerializedView(Object treeStructure, Object state){
        this.treeStructure = treeStructure;
        this.state = state;
    }

    public Object getStructure(){
        return treeStructure;
    }

    public Object getState(){
        return state;
    }
}
