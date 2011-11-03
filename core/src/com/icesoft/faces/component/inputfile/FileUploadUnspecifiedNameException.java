package com.icesoft.faces.component.inputfile;

import java.io.IOException;

/**
 * The Exception thrown when the user does not specify a file name, and
 * then clicks the upload button.
 * 
 * @author mcollette
 * @since 1.8
 */
public class FileUploadUnspecifiedNameException extends IOException {
    public FileUploadUnspecifiedNameException() {
        super();
    }
    
    public FileUploadUnspecifiedNameException(String msg) {
        super(msg);
    }
}
