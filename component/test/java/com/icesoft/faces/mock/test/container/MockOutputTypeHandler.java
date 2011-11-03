/*
 * Mock
 */

package com.icesoft.faces.mock.test.container;

import com.icesoft.faces.component.dataexporter.OutputTypeHandler;

/**
 *
 * @author fye
 */
public class MockOutputTypeHandler extends OutputTypeHandler{

    public MockOutputTypeHandler(String path){
        super(path);
    }
    @Override
    public void writeHeaderCell(String text, int col) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeCell(Object output, int col, int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void flushFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
