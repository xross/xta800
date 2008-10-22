package com.xmos.xta.gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public class XFileFilter extends FileFilter
{
    ArrayList<String> m_extensions;
    String m_description;
    
    
    public XFileFilter() {
        super();
        m_extensions = new ArrayList<String> ();
        m_description = "";
    }

    public void registerAcceptableFileTypes(String extension){
        if (m_extensions.size() > 0)
            m_description += "; ";
        m_description += "*." + extension;
        
        m_extensions.add(extension);
    }
    
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        
        if(m_extensions.contains(getExtension(f))){
            return true;
        } else {
            return false;
        }
    }

    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    public String getDescription() {
        return m_description;
    }
}