/*
 * 
 */

package com.icesoft.faces.mock.test.data;

import java.io.Serializable;

/**
 *
 * @author fye
 */
public class MockDataObject implements Serializable{

    private String test;

    public MockDataObject(){
        
    }

    public MockDataObject(String tmp){
        test = tmp;
    }

    public String getTest(){
        return test;
    }
}
