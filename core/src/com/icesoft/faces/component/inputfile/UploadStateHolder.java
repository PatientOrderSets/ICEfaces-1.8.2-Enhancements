package com.icesoft.faces.component.inputfile;

/**
 * The UploadServer can start a JSF lifecycle either in its own thread, or on 
 * another one. So there has to be a way of passing the state to the 
 * appropriate thread, where it will be put into a thread local field, so that 
 * the FileUploadPhaseListener can find it and
 * 
 * 
 * @author mcollette
 * @since 1.8
 */
public class UploadStateHolder implements Runnable {
    private static ThreadLocal holder = new ThreadLocal();
    
    private UploadConfig uploadConfig;
    private FileInfo fileInfo;
    private boolean asyncLifecycle;
    private String iframeContent;
    
    public UploadStateHolder(UploadConfig uploadConfig, FileInfo fileInfo) {
        this.uploadConfig = uploadConfig;
        this.fileInfo = fileInfo;
    }
    
    public UploadConfig getUploadConfig() {
        return uploadConfig;
    }
    
    public FileInfo getFileInfo() {
        return fileInfo;
    }
    
    public boolean isAsyncLifecycle() {
        return asyncLifecycle;
    }
    
    public void setAsyncLifecycle(boolean asyncLifecycle) {
        this.asyncLifecycle = asyncLifecycle;
    }
        
    public String getIframeContent() {
        return iframeContent;
    }
    
    public void setIframeContent(String iframeContent) {
        this.iframeContent = iframeContent;
    }

    /**
     * Save this state into the current thread's local storage
     */
    public void install() {
        holder.set(this);
    }

    /**
     * Retrieve the state from the current thread's local storage
     */
    public static UploadStateHolder take() {
        UploadStateHolder current = (UploadStateHolder) holder.get();
        holder.set(null);
        return current;
    }

    /**
     * Deferred install, for when we'll be running on another thread
     */
    public void run() {
        install();
    }
}
