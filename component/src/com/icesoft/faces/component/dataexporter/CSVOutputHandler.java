package com.icesoft.faces.component.dataexporter;

import java.io.FileWriter;
import java.io.IOException;

public class CSVOutputHandler extends OutputTypeHandler{
	
	StringBuffer buffer;
	int rowIndex = 0;

	public CSVOutputHandler(String path) {
		super(path);
		buffer = new StringBuffer();
		this.mimeType = "text/csv";
	}

	public void flushFile() {
        deleteComma();
		try{
			FileWriter fw = new FileWriter(getFile());
			fw.write(buffer.toString());
			fw.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}

	public void writeCell(Object output, int col, int row) {
		if( row != rowIndex ){
            deleteComma();
			buffer.append("\n");
			rowIndex ++;
		}
		buffer.append(output.toString() + ",");
		
	}

	public void writeHeaderCell(String text, int col) {
		//do nothing, no header to write for csv		
	}

    private void deleteComma() {
        int comma = buffer.lastIndexOf(",");
        if (comma > 0) {
            buffer.deleteCharAt(comma);
        }
    }

}
