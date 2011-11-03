package com.icesoft.faces.component.dataexporter;

import java.io.File;
import java.io.IOException;

public abstract class OutputTypeHandler {

	protected File file;
	protected String mimeType;

	public String getMimeType() {
		return mimeType;
	}

	public OutputTypeHandler(String path) {
		try {
			file = new File(path);
			file.createNewFile();
			file.deleteOnExit();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public abstract void writeHeaderCell(String text, int col);

	public abstract void writeCell(Object output, int col, int row);

	public abstract void flushFile();

	public File getFile() {
		return file;
	}

}
