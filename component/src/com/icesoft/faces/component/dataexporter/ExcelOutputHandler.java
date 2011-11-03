package com.icesoft.faces.component.dataexporter;

import java.io.IOException;

import javax.faces.context.FacesContext;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelOutputHandler extends OutputTypeHandler{
	
	WritableSheet sheet = null;
	WritableWorkbook workbook = null;
	

	public ExcelOutputHandler(String path, FacesContext fc, String title) {
		super(path);
		try{
			WorkbookSettings settings = new WorkbookSettings();
			settings.setLocale(fc.getViewRoot().getLocale());
			workbook = Workbook.createWorkbook(super.getFile());
			sheet = workbook.createSheet(title, 0);
			
			this.mimeType = "application/vnd.ms-excel";
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}

	
	public void flushFile() {
		try{
			workbook.write();
			workbook.close();
		}
		catch( WriteException ioe){
			ioe.printStackTrace();
		}
		catch( IOException ioe){
			ioe.printStackTrace();
		}
	}

	public void writeCell(Object output, int col, int row) {
		WritableCell cell = null;
		if( output instanceof String ){
			cell = new Label(col, row + 1, (String)output);
		}
		else if( output instanceof Double ){
			cell = new Number(col, row + 1, ((Double)output).doubleValue()); 
		}
		try{
			sheet.addCell(cell);
		}
		catch(WriteException e){
			System.out.println("Could not write excel cell");
			e.printStackTrace();
		}			
		
	}

	public void writeHeaderCell(String text, int col) {
		try{
			WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 10); 
			WritableCellFormat arial10format = new WritableCellFormat (arial10font); 
			arial10font.setBoldStyle(WritableFont.BOLD);
			Label label = new Label(col, 0, text, arial10format);
			sheet.addCell(label);
		}
		catch(WriteException we){
			we.printStackTrace();
		}
		
	}

}
