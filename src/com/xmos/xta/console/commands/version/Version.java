package com.xmos.xta.console.commands.version;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;

public class Version extends Command {
	public Version() {		
	}
	
    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "version";
    }        

    public String commandLine() {
    	return "version";
    }
    
    public String description(Engine engine, boolean detailed) {
        return "version: Displays the version information";      	
    }

    public Command clone(String[] args, int index) {
    	return new Version();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
    	InputStream inputStream = getClass().getResourceAsStream("/plugin.xml");
    	if (inputStream == null) {
        	console.display(String.format("Error: Unable to locate version information (missing plugin.xml)\n"));
        	return;
    	}    	
    	String versionString = getVersionString(inputStream);
    	if (versionString == null) {
        	console.display(String.format("Error: Unable to locate version information\n"));
        	return;    		
    	}
    	console.display(versionString + "\n");    		
    }    
    
    private String getVersionString(InputStream inputStream) {
    	try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			XMLHandler xmlHandler = new XMLHandler();
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.setErrorHandler(xmlHandler);			
			xmlReader.parse(new InputSource(inputStream));			
			return xmlHandler.getVersionString();
		} catch (Exception e) {
			return null;
		} 
    }
    
    static class XMLHandler extends DefaultHandler {
    	String m_versionString;
    	
        public XMLHandler() {
    	    super();
    	    m_versionString = null;
        }

        public void startElement (String uri, String name,
			      String qName, Attributes atts) {        	
        	String nameString = getAttributeValue(atts, "name");
        	if (nameString != null) {
        		if (nameString.equals("aboutText")) {
        			String valueString = getAttributeValue(atts, "value");
        			if (valueString != null) {
        				m_versionString = valueString;
        			}
        		}
        	}
        }
                
        public String getVersionString() {
        	return m_versionString;
        }   
        
        private String getAttributeValue(Attributes atts, String name) {
        	for (int index = 0; index < atts.getLength(); ++index) {
        		if (atts.getLocalName(index).equals(name)) { 
        			return atts.getValue(index);
        		}
        	}        	
        	return null;
        }
    }    
}
