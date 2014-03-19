package com.xmos.xta.core.engine.sourcemapper;

import java.io.File;

class FileLocation {
    private String m_fileName;
    private File m_file;

    FileLocation(String fileName, File file) {
        m_fileName = fileName;
        m_file = file;
    }

    public String getFileName() {
         return m_fileName;
    }

    public File getFile() {
         return m_file;
    }
}
