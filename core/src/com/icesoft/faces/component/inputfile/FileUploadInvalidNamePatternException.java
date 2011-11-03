package com.icesoft.faces.component.inputfile;

import java.io.IOException;

/**
 * The Exception thrown when the uploaded file's name does not match with the
 * InputFile's fileNamePattern regular expression property.
 * 
 * @author mcollette
 * @since 1.8
 */
public class FileUploadInvalidNamePatternException extends IOException {
    public FileUploadInvalidNamePatternException() {
        super();
    }
    
    public FileUploadInvalidNamePatternException(String msg) {
        super(msg);
    }
}
