package com.xmos.xta.test.consoletest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PostTestSorter {
	
	private static String m_sourceFile = "test.xc";
	
	public static void main(String[] args) {
		PostTestSorter ct = new PostTestSorter();
		ct.sort();
	}

	private void sort() {
		
		Set<String> files = new HashSet<String>();
		File dir = new File(".");
		String[] children = dir.list();
		if (children == null) { // Either dir does not exist or is not a
								// directory
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];
				if(filename.startsWith("cmds") && filename.endsWith(".txt") && !filename.equals("cmds.txt")){
					files.add(filename);
				}
				
			}
		}
		Map<String, Set<String>> errorToFile = new HashMap<String, Set<String>>();
		for(String file : files){
			try {
				Process p = Runtime.getRuntime().exec(
						"xta -source " + file + " > output.txt 2>&1");
				p.waitFor();
			} catch (Exception err) {
				err.printStackTrace();
			}
			
			String error = "";
			String secondLine = "";
			int line = 0;
	        try {
		        FileReader fr = new FileReader("output.txt");
		        BufferedReader br = new BufferedReader(fr);
		        String s;
		        
		        while((s = br.readLine()) != null) {
		        	
		        	if(s.contains("com.xmos.xta") || s.contains("java.")){
		        		line++;
		        		error += s +"\n";
		        		if(line == 2){
		        			secondLine = s ;
		        		}
		        	}
		        	
		        }
		        fr.close(); 
	        } catch (IOException e) {
				e.printStackTrace();
			}
			
			if(errorToFile.containsKey(secondLine)){
				errorToFile.get(secondLine).add(file);
			} else {
				Set<String> f = new HashSet<String>();
				
				f.add(file);
				errorToFile.put(secondLine, f);
			}
		}
		
		Random r = new Random();
		for(String error : errorToFile.keySet()){
			boolean success = false;
			String groupDir = "test" + r.nextInt(Integer.MAX_VALUE);
			while(!success){
				success = (new File(groupDir)).mkdir(); 
			}
			int testNo=0;
			File notATest = new File(groupDir + File.separator + ".not_a_test");
			try {
				notATest.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			for(String file : errorToFile.get(error)){
				success = false;
				String specificTest = "test" + testNo;
				String testDir = groupDir + File.separator + specificTest;
				
				while(!success){
					success = (new File(testDir)).mkdir(); 
					
				}
				
				File logExpect = new File(testDir + File.separator + specificTest + ".log.expect");
				try {
					
					
					copy(new File(file), new File(testDir + File.separator +"cmds.txt"));
					copy(new File(m_sourceFile), new File(testDir + File.separator + m_sourceFile));
					makeTestPl(testDir , specificTest, "test.pl");
					logExpect.createNewFile(); 
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				testNo++;
			}
		}
		
	}

	
	private void makeTestPl(String testDir, String specificTest, String testFile) {
		FileWriter outputStream = null;
        PrintWriter printer = null;
        try {
            outputStream = new FileWriter(testDir + File.separator + testFile);
            printer = new PrintWriter(outputStream);
            printer.println("#!/usr/bin/perl");
            printer.println("use strict;");
            printer.println("use warnings;");
            printer.println("my $genDir  = shift(@ARGV);");
            printer.println("my $arch    = shift(@ARGV);");
            printer.println("my $target  = shift(@ARGV);");
            printer.println("my $noBuild = shift(@ARGV);");
            printer.println("if (!$noBuild) {");
            printer.println("        system(\"xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/" + specificTest +".log 2>&1\");");
            printer.println("}");
            printer.println("system(\"@ARGV load $genDir/test.xe \" .");
    		printer.println("       \"source cmds.txt \" .");
			printer.println("       \"-exit > $genDir/" + specificTest +".txt 2>&1\");");
			printer.println("my $fail = 0;");
			printer.println("open(INFILE, \"$genDir/" + specificTest +".txt\") or die \"Can't open input file\";");
			printer.println("while (<INFILE>) {");
			printer.println("    my $line = $_;");
			printer.println("    if($line =~ m/AssertionError/ or $line =~ m/java.lang/){");
			printer.println("        $fail = 1;");
			printer.println("    }        ");
			printer.println("}");
			printer.println("close(INFILE);");
			printer.println("if ($fail) {");
			printer.println("	open(LOG, \">>$genDir/" + specificTest +".log\");");
			printer.println("	print LOG \"FAIL\";");
			printer.println("	close(LOG);");
			printer.println("}");
			
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	} 
}

