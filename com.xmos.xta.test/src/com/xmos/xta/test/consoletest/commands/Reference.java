package com.xmos.xta.test.consoletest.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Reference implements Command {

	class RefFile{
		private String m_fileName;
		private int m_endLine;
		private List<String> m_functions;
		private List<String> m_endPoints;
		private List<String> m_calls;
		
		public RefFile(String file, List<String> functions, List<String> endPoints, List<String> calls){
			m_fileName = file;
			m_functions = functions;
			m_endPoints = endPoints;
			m_calls = calls;
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				int i=1;
				while(br.readLine() != null) {
						i++;
				}
				m_endLine = i;
				fr.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public String getLineRef(){
			Random r = new Random();
			return m_fileName + ":" + (r.nextInt(m_endLine)+1) + " ";
		}
		
		public String getFunctionRef(){
			Random r = new Random();
			return m_functions.get(r.nextInt(m_functions.size()))+ " ";
		}
		
		public String getEndPointRef(){
			Random r = new Random();
			return m_endPoints.get(r.nextInt(m_endPoints.size()))+ " ";
		}
		
		public String getCallTrace(){
			Random r = new Random();
			String trace = "";
			int length = r.nextInt(10);
			
			for(int i=0;i<length;i++){

				int s = r.nextInt(m_calls.size());
				trace +=m_calls.get(s) + ",";
	
				if(r.nextBoolean()){
					int p = r.nextInt(m_functions.size());
					trace +=m_functions.get(p) + ",";
				} else {
					int q = r.nextInt(m_calls.size());
					trace +=m_calls.get(q) + ",";
				}
			}
			return trace;
		}
	}
	private List<RefFile> m_refFiles;
	
	
	public Reference(){
		m_refFiles = new LinkedList<RefFile>();
		List<String> functions = new LinkedList<String>();
		functions.add("f");
		functions.add("g");
		functions.add("h");
		functions.add("main");
		List<String> calls = new LinkedList<String>();
		calls.add("callf");
		calls.add("callg");
		calls.add("callh");
		List<String> endPoints = new LinkedList<String>();
		endPoints.add("ep1");
		endPoints.add("ep2");
		endPoints.add("ep3");
		endPoints.add("ep4");
		m_refFiles.add(new RefFile("test.xc", functions, endPoints, calls));
	}
	
	public String getCommand() {
		Random r = new Random();
		switch(r.nextInt(9)){
		case 0:
			return getRefFile().getLineRef();
		case 1:
			return getRefFile().getFunctionRef();
		case 2:
			return getRefFile().getEndPointRef();
		case 3:
			return getRefFile().getCallTrace() + getRefFile().getEndPointRef();
		case 4:
			return "- ";
		case 5:
			return "0x" + Long.toHexString(0x10000 + r.nextInt(0x04000)) +" ";
		case 6:
			return "* ";
		case 7:
			return getRefFile().getCallTrace() + getRefFile().getLineRef();
		case 8:
			return getRefFile().getCallTrace() + getRefFile().getFunctionRef();
		}
		return "";
	}

	private RefFile getRefFile(){
		Random r = new Random();
		return m_refFiles.get(r.nextInt(m_refFiles.size()));
	}
	
}
