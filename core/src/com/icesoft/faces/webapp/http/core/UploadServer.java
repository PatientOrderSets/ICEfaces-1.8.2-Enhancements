package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.component.inputfile.UploadConfig;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.UploadStateHolder;
import com.icesoft.faces.component.inputfile.FileUploadUnspecifiedNameException;
import com.icesoft.faces.component.inputfile.FileUploadInvalidNamePatternException;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.StringContentHandler;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class UploadServer implements Server {
    private static final Log log = LogFactory.getLog(UploadServer.class);
    private Map views;
    private long maxSize;
    private boolean uniqueFolder;
    private String uploadDirectory;
    private boolean uploadDirectoryAbsolute;
    private boolean lifecycleOnCallingThread;

    public UploadServer(Map views, Configuration configuration) {
        this.views = views;
        this.maxSize = configuration.getAttributeAsLong("uploadMaxFileSize", 3 * 1024 * 1024); // 3MB
        this.uniqueFolder = configuration.getAttributeAsBoolean("uniqueFolder", false);
        //Partial fix for http://jira.icefaces.org/browse/ICE-1600
        this.uploadDirectory = configuration.getAttribute("uploadDirectory", "");
        this.uploadDirectoryAbsolute = configuration.getAttributeAsBoolean("uploadDirectoryAbsolute", false);
        this.lifecycleOnCallingThread = configuration.getAttributeAsBoolean("forceLifecycleOnCallingThread", false);
    }

    public void service(final Request request) throws Exception {
        final ServletFileUpload uploader = new ServletFileUpload();
        final ProgressCalculator progressCalculator = new ProgressCalculator(
                lifecycleOnCallingThread);
        uploader.setFileSizeMax(maxSize);
        uploader.setProgressListener(new ProgressListener() {
            public void update(long read, long total, int chunkIndex) {
                progressCalculator.progress(read, total);
            }
        });
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object req, Object resp) throws Exception {
                final HttpServletRequest servletRequest = (HttpServletRequest) req;
                FileItemIterator iter = uploader.getItemIterator(servletRequest);
                String viewIdentifier = null;
                String componentID = null;
                try {
                    while (iter.hasNext()) {
                        FileItemStream item = iter.next();
                        if (item.isFormField()) {
                            String name = item.getFieldName();
                            if ("ice.component".equals(name)) {
                                componentID = Streams.asString(item.openStream());
                            } else if ("ice.view".equals(name)) {
                                viewIdentifier = Streams.asString(item.openStream());
                            }
                        } else {
                            final View view = (View) views.get(viewIdentifier);
                            view.installThreadLocals();
                            final PersistentFacesState state = view.getPersistentFacesState();
                            final BridgeFacesContext context = view.getFacesContext();

                            if (log.isDebugEnabled()) {
                                log.debug("UploadServer");
                                log.debug("  viewIdentifier: " + viewIdentifier);
                                log.debug("  componentID: " + componentID);
                            }

                            UploadConfig componentUploadConfig = null;
                            String key = viewIdentifier + " " + componentID;
                            Object sessionObj = context.getExternalContext().getSession(false);
                            if (sessionObj != null) {
                                synchronized(sessionObj) {
                                    Map map = context.getExternalContext().getSessionMap();
                                    componentUploadConfig = (UploadConfig) map.get(key);
                                }
                            }
                            UploadConfig uploadConfig = new UploadConfig(
                                componentUploadConfig, componentID,
                                maxSize, uniqueFolder,
                                uploadDirectory, uploadDirectoryAbsolute);
                            if (log.isDebugEnabled()) {
                                log.debug("  session map key: " + key);
                                log.debug("  componentUploadConfig: " + componentUploadConfig);
                                log.debug("  uploadConfig: " + uploadConfig);
                            }
                            FileInfo fileInfo = new FileInfo();
                            fileInfo.setStatus(FileInfo.UPLOADING);
                            progressCalculator.setLifecycleState(
                                state, uploadConfig, fileInfo);
                            String iframeContent = null;
                            try {
                                upload(
                                    item,
                                    fileInfo,
                                    uploadConfig,
                                    servletRequest.getSession().getServletContext(),
                                    servletRequest.getRequestedSessionId());
                                UploadStateHolder stateHolder = progressCalculator.doLifecycle();
                                iframeContent = getResultingIframeContent(context, stateHolder);
                            } catch (IOException e) {
                                log.warn("File upload problem", e);
                            } catch (Throwable t) {
                                log.warn("File upload issue", t);
                            } finally {
                                request.respondWith(new StringContentHandler(
                                        "text/html", "UTF-8", iframeContent));
                            }
                        }
                    }
                } catch (MultipartStream.MalformedStreamException exception) {
                    if (log.isTraceEnabled()) {
                        log.trace("Connection broken by client.", exception);
                    } else if (log.isDebugEnabled()) {
                        log.debug("Connection broken by client: " + exception.getMessage());
                    }
                }
            }

            protected void upload(
                FileItemStream stream,
                FileInfo fileInfo,
                UploadConfig uploadConfig,
                ServletContext servletContext,
                String sessionId)
                throws IOException
            {
                // InputFile uploadDirectory attribute takes precedence,
                //  but if it's not given, then default to the
                //  com.icesoft.faces.uploadDirectory context-param
                String folder = uploadConfig.getUploadDirectory();
                // InputFile uploadDirectoryAbsolute attribute takes precedence,
                //  but if it's not given, then default to the
                //  com.icesoft.faces.uploadDirectoryAbsolute context-param
                Boolean folderAbs = uploadConfig.getUploadDirectoryAbsolute();
                if (!folderAbs.booleanValue()) {
                    folder = servletContext.getRealPath(folder);
                }
                if (uploadConfig.getUniqueFolder().booleanValue()) {
                    String FILE_SEPARATOR = System.getProperty("file.separator");
                    folder = folder + FILE_SEPARATOR + sessionId;
                }

                String namePattern = uploadConfig.getFileNamePattern().trim();
                String fileName = stream.getName();
                File file = null;
                // If server is Unix and file name has full Windows path info. (JIRA ICE-1868)
                if (File.separatorChar == '/' && fileName.matches("^[a-zA-Z]:\\\\.*?|^\\\\\\\\.*?")) {
                    // Strip the path info. Keep the base file name
                    fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                }
                try {
                    if (fileName != null && fileName.length() > 0) {
                        // IE gives us the whole path on the client, but we just
                        //  want the client end file name, not the path
                        File tempFileName = new File(fileName);
                        fileName = tempFileName.getName();
                    } else {
                        throw new FileUploadUnspecifiedNameException();
                    }

                    fileInfo.setFileName(fileName);
                    fileInfo.setContentType(stream.getContentType());
                    if (log.isDebugEnabled()) {
                        log.debug("fileNamePattern: " + namePattern);
                        log.debug("fileName: " + fileName);
                        log.debug("Matches: " + (fileName != null && fileName.trim().matches(namePattern)));
                    }
                    if (fileName != null && fileName.trim().matches(namePattern)) {
                        File folderFile = new File(folder);
                        if (!folderFile.exists())
                            folderFile.mkdirs();
                        file = new File(folder, fileName);
                        OutputStream output = new FileOutputStream(file);
                        Streams.copy(stream.openStream(), output, true);
                        long fileLength = file.length();
                        if (uploadConfig.isFailOnEmptyFile()) {
                            if (fileLength == 0) {
                                throw new FileUploadBase.FileUploadIOException(
                                        new FileUploadBase.UnknownSizeException()); 
                            }
                        }                        
                        if (log.isDebugEnabled())
                            log.debug("fileLength: " + fileLength + ((fileLength > uploadConfig.getSizeMax().longValue()) ? "  TOO LARGE" : ""));
                        fileInfo.setStatus(FileInfo.SAVED);
                        fileInfo.setPercent(100);
                        fileInfo.setFile(file);
                        fileInfo.setSize( fileLength );
                    } else {
                        throw new FileUploadInvalidNamePatternException("The file name '"+fileName+"' does not match with the file name pattern '"+namePattern+"'");
                    }
                } catch (FileUploadBase.FileUploadIOException uploadException) {
                    Throwable cause = uploadException.getCause();
                    if (cause instanceof Exception) {
                        fileInfo.setException((Exception)cause);
                    }
                    else {
                        fileInfo.setException(uploadException);
                    }

                    try {
                        throw cause;
                    } catch (FileUploadBase.FileSizeLimitExceededException e) {
                        fileInfo.setStatus(FileInfo.SIZE_LIMIT_EXCEEDED);
                    } catch (FileUploadBase.UnknownSizeException e) {
                        fileInfo.setStatus(FileInfo.UNKNOWN_SIZE);
                    } catch (FileUploadBase.InvalidContentTypeException e) {
                        fileInfo.setStatus(FileInfo.INVALID_CONTENT_TYPE);
                    } catch (Throwable t) {
                        fileInfo.setStatus(FileInfo.INVALID);
                    }
                    fileInfo.setPercent(0);
                    if (file != null) {
                        file.delete();
                        file = null;
                    }
                }
                catch (FileUploadUnspecifiedNameException e) {
                    fileInfo.setException(e);
                    fileInfo.setStatus(FileInfo.UNSPECIFIED_NAME);
                    fileInfo.setPercent(0);
                }
                catch (FileUploadInvalidNamePatternException e) {
                    fileInfo.setException(e);
                    fileInfo.setStatus(FileInfo.INVALID_NAME_PATTERN);
                    fileInfo.setPercent(0);
                }
                catch (IOException e) { // Eg: If creating the saved file fails
                    fileInfo.setException(e);
                    fileInfo.setStatus(FileInfo.INVALID);
                    fileInfo.setPercent(0);
                    if (file != null) {
                        file.delete();
                        file = null;
                    }
                }
                
                log.debug("upload(-)  Method bottom");
            }
            
            protected String getResultingIframeContent(BridgeFacesContext context, UploadStateHolder stateHolder) {
                String iframeContent = null;
                long startTime = System.currentTimeMillis();
                do {
                    iframeContent = stateHolder.getIframeContent();
                    if (log.isDebugEnabled()) {
                        log.debug("getResultingIframeContent()");
                        log.debug("vvvvvvvvvvvvvvvvvvvvvvvvvvv");
                        log.debug(iframeContent);
                        log.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                    }
                    if (iframeContent != null) {
                        break;
                    }
                    
                    long now = System.currentTimeMillis();
                    if ((now - startTime) >= 5000L) {
                        break;
                    }
                    try { Thread.sleep(100L); } catch(InterruptedException e) {}
                } while(stateHolder.isAsyncLifecycle());
                return iframeContent;
            }
            
            public void portlet(Object request, Object response, Object config) {
                throw new IllegalAccessError("Cannot upload using a portlet request/response.");
            }
        });
    }

    public void shutdown() {
    }

    private static class ProgressCalculator {
        private final int GRANULARITY = 10;
        private final long TIME_MILLISECONDS = 1000L;
        private PersistentFacesState state;
        private UploadConfig uploadConfig;
        private FileInfo fileInfo;
        private int lastGranularlyNotifiablePercent = -1;
        private long lastTime = -1;
        private boolean lifecycleOnCallingThread;

        public ProgressCalculator(boolean lifecycleOnCallingThread) {
            this.lifecycleOnCallingThread = lifecycleOnCallingThread;
        } 

        public void progress(long read, long total) {
            if (total > 0) {
                int percentage = (int) ((read * 100L) / total);
                int percentageAboveGranularity = percentage % GRANULARITY;
                int granularNotifiablePercentage = percentage - percentageAboveGranularity;
                boolean shouldNotify = granularNotifiablePercentage > lastGranularlyNotifiablePercent;
                if (shouldNotify && (lastTime > 0) &&
                    (granularNotifiablePercentage != 0) &&
                    (granularNotifiablePercentage != 100))
                {
                    long now = System.currentTimeMillis();
                    if ( (now - lastTime) < TIME_MILLISECONDS ) {
                        shouldNotify = false;
                    }
                }
                if (shouldNotify) {
                    lastGranularlyNotifiablePercent = granularNotifiablePercentage;
                    potentiallyNotify();
                }
            }
        }

        public void setLifecycleState(
                PersistentFacesState state,
                UploadConfig uploadConfig,
                FileInfo fileInfo) {
            this.state = state;
            this.uploadConfig = uploadConfig;
            this.fileInfo = fileInfo;
            // Always setAllCurrentInstances() right away, in case we never 
            //  get progress, notifications, and are immediately done, in case
            //  InputFile.upload(-) needs things setup
            if(state != null) {
                state.setAllCurrentInstances();
            }
            potentiallyNotify();
        }
        
        protected void potentiallyNotify() {
            if (state != null &&
                uploadConfig != null &&
                fileInfo != null &&
                // If finished because bad fileNamePattern, can still get more
                // progress, which we should discard, since already reset 
                // progress to 0. Progress seems to come in when sending 
                // response, possibly as side effect of buffer flushing?
                !fileInfo.isFinished() &&
                lastGranularlyNotifiablePercent >= 0 &&
                lastGranularlyNotifiablePercent < 100 &&
                !state.isSynchronousMode() &&
                uploadConfig.isProgressListener() &&
                uploadConfig.isProgressRender())
            {
                if (log.isDebugEnabled())
                    log.debug("UploadServer  progress :: " + lastGranularlyNotifiablePercent);
                fileInfo.setPercent(lastGranularlyNotifiablePercent);
                doLifecycle();
                lastTime = System.currentTimeMillis();
            }
        }

        /**
         * @return UploadStateHolder encapsulating whether the lifecycle will 
         * happen on another thread, asynchronously, as well as the rendered 
         * IFRAME content, which is necessary for the Servlet Response
         */
        public UploadStateHolder doLifecycle() {
            UploadStateHolder stateHolder = null;
            try {
                if (log.isDebugEnabled())
                    log.debug("UploadServer  doLifecycle :: " + uploadConfig.getClientId() + " in form '"+uploadConfig.getFormClientId()+"'" + " -> " + fileInfo);
                // Pass a copy of the FileInfo into the InputFile Component,
                // since it might be asynchronously passed in, and the file
                // have completed uploading by the time it's used. On the 
                // surface it might sound good to have the latest progress,
                // but that can cause the SAVED actionListener to be called
                // more than once, which can corrupt the applications's
                // data model.
                stateHolder = new UploadStateHolder(
                    uploadConfig, (FileInfo) fileInfo.clone());

                // Seam throws spurious exceptions with PFS.renderLater
                //  so we'll work-around that for now. Fix later.
                if (lifecycleOnCallingThread ||
                      SeamUtilities.isSeamEnvironment() ||
                      SeamUtilities.isSpringSecurityEnvironment()  ||
                      SeamUtilities.isSpringEnvironment()) {
                    
                    stateHolder.setAsyncLifecycle(false);
                    stateHolder.install();
                    state.setupAndExecuteAndRender();
                    state.setAllCurrentInstances();
                }
                else {
                    stateHolder.setAsyncLifecycle(true);
                    state.renderLater(stateHolder, false);
                    Thread.yield();
                }
            }
            catch(Exception e) {
                log.warn("Problem rendering view during file upload", e);
            }
            return stateHolder;
        }
    }
}
