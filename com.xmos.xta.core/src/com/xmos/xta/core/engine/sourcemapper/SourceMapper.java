package com.xmos.xta.core.engine.sourcemapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.utils.CoreCollection;
import com.xmos.xta.core.engine.utils.CoreId;

public class SourceMapper {
	private long m_xeLastModified;
	private List<SourceFile> m_sourceFiles;
	
	private static Collection<FileLocation> s_userSpecifedFileLocations = new ArrayList<FileLocation>();
	
	public static void clearUserSpecifedFileLocations() {
		s_userSpecifedFileLocations.clear();
	}
	
    public SourceMapper(CommandSource src, Engine engine, XeLoader xeLoader, Sections sections, 
    		CoreCollection<InstructionDecoder> instructionDecoders) throws AbortedException {

        m_xeLastModified = xeLoader.getFile().lastModified();    	
        m_sourceFiles = createSourceFiles(src, engine, xeLoader, instructionDecoders);

		for (FileLocation fileLocation : s_userSpecifedFileLocations) {
			setFile(src, engine, getSourceFile(fileLocation.getFileName()), fileLocation.getFile());
		}
	}

	public void update(CommandSource src, Notifier notifier, String fileName, File file) {
        setFile(src, notifier, getSourceFile(fileName), file);
		s_userSpecifedFileLocations.add(new FileLocation(fileName, file));
	}
    
    public File findFileForSourceFile(SourceFile sourceFile, FileFinder fileFinder) {
    	if (fileFinder == null) {
    		return null;
    	}

    	File file = null;
    	DebugFile debugFile = sourceFile.getDebugFile();
    	boolean done = false;
		while (!done) {
			file = fileFinder.openFileFinder(debugFile.getBaseDir(), debugFile.getFileName());

			if (file == null) {
				done = true;

			} else {
				if (file.exists()) {
					try {
						file = file.getCanonicalFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					done = true;
				}
			}
		}
		return file;
    }

    public void setFile(CommandSource src, Notifier notifier, SourceFile sourceFile, File file) {
    	if ((file == null) || (sourceFile == null))
    		return;

    	DebugFile debugFile = sourceFile.getDebugFile();
		debugFile.setFile(file);
		sourceFile.readFile();

		checkSourceFileDate(src, notifier, file);
    }

    public boolean fileNewerThanBinary(File file) {
    	assert(file != null);
    	long sourceLastModified = file.lastModified();
    	return (sourceLastModified > m_xeLastModified);
	}

    public String getDebugFileName(String diskFileName) {
        for (SourceFile sourceFile : m_sourceFiles) {
        	try {
        		File fileOnDisk = sourceFile.getFile();
        		if (fileOnDisk != null) {
    				if (fileOnDisk.getCanonicalPath().equals(diskFileName)) {
    					return sourceFile.getFullFileName();
    				}
        		}
			} catch (IOException e) {}
        }
        return diskFileName;
    }

	public List<SourceFile> getSourceFiles() {
        return m_sourceFiles;
    }

	public SourceFile getSourceFile(String fileName) {
        for (SourceFile sourceFile : m_sourceFiles){
        	if (sourceFile.fileNameMatches(fileName))
        		return sourceFile;
        }
        return null;
    }

    private void checkSourceFileDate(CommandSource src, Notifier notifier, File file) {
    	if (fileNewerThanBinary(file)) {
    		notifier.notify(src, Notifier.NOTIFY_WARNING,
    				String.format("Source file '%s' newer than binary", file.getName()));
    	}
	}
	
	private SourceFile getSourceFile(DebugFile debugFile, List<SourceFile> sourceFiles){
    	// Every source file added should have been canonicalised as should have the file
        for (SourceFile sourceFile : sourceFiles) {
            if (debugFile == sourceFile.getDebugFile()) {
                return sourceFile;
            }
        }
        return null;
    }

    private List<SourceFile> createSourceFiles(CommandSource src, Engine engine, XeLoader xeLoader,
    		CoreCollection<InstructionDecoder> instructionDecoders) throws AbortedException {

    	List<SourceFile> sourceFiles = new LinkedList<SourceFile>();
    	    	
		Collection<DebugFile> debugFiles = getDebugFiles(xeLoader);
		engine.startAnalysis("Processing sources", debugFiles.size());

      	for (DebugFile debugFile : debugFiles) {
			File file = new File(debugFile.getFullFileName());
    		engine.provideFeedback(file.getName());
    		
			boolean done = false;
			while (!done) {
				Notifier notifier = engine.getNotifier();
				if (file.exists()) {
					try {
						file = file.getCanonicalFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					addSourceFile(src, notifier, xeLoader, debugFile, file, sourceFiles, instructionDecoders);
					done = true;

				} else {
					file = findFileInSrcPaths(engine.getSrcPaths(), debugFile.getFileName());
					addSourceFile(src, notifier, xeLoader, debugFile, file, sourceFiles, instructionDecoders);
					done = true;
				}
			}
			engine.provideWorkDone(1);
			engine.getAbort();			
		}
        return sourceFiles;
    }

	private File findFileInSrcPaths(String srcPathsString, String fileName) {
		String[] srcPaths = srcPathsString.split(";");
		for (String srcPath : srcPaths) {
		    File file = new File(srcPath + File.separator + fileName);
	        if (file.exists()) {
	    	    return file;
	        }
		}
		return null;
	}

	private void addSourceFile(CommandSource src, Notifier notifier,
			XeLoader xeLoader, DebugFile debugFile,
			File file, List<SourceFile> sourceFiles,
			CoreCollection<InstructionDecoder> instructionDecoders) {

    	SourceFile sourceFile = getSourceFile(debugFile, sourceFiles);
    	if (sourceFile == null) {
    		sourceFile = new SourceFile(debugFile, xeLoader, instructionDecoders);
    	}
		sourceFiles.add(sourceFile);
    	setFile(src, notifier, sourceFile, file);
	}	
	
	private Collection<DebugFile> getDebugFiles(XeLoader xeLoader) {
        List<DebugFile> sources = new ArrayList<DebugFile>();
		
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {		
				CoreId coreId = new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber);
				
		    	DebugInfo debugInfo = xeLoader.getDebugInfo(coreId.getNodeChainIndex(), coreId.getCoreNumber());
				Collection<DebugFile> debugFiles = debugInfo.getDebugEngine().getDebugFiles();

		      	for (DebugFile debugFile : debugFiles) {
		      		if (!sources.contains(debugFile)) {
					    sources.add(debugFile);
		      		}
		      	}				
			}
		}
		return sources;
	}
}

