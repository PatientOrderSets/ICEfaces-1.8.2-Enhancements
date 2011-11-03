/*
 * TODO:
 */

package com.icesoft.faces.mock.test.data;

import javax.faces.event.FacesListener;

/**
 *
 * @author fye
 */
public class MockFacesListener implements FacesListener{

    private String name;
    public MockFacesListener(String name){
        this.name = name;
    }
}
