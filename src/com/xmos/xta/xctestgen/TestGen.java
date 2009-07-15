package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGen {

	static String m_testDescription;
	static int m_unique;
	static int m_endpoint;
	static int m_testCount;
	
	static boolean done() {
		return (m_testDescription.length() == 0);
	}

	static char getNext() {
		m_unique++;
		assert (m_testDescription.length() > 0);
		char nextC = m_testDescription.charAt(0);
		m_testDescription = m_testDescription.substring(1);
		return nextC;
	}

	private static void error(String string) {
		System.out.println("'(' detected without a node before it.");
		System.exit(1);
	}

	static private void generate(CodeElement code) {
		if (done())
			return;

		code.addNode();
		
		CodeElement lastElement = null;

		do {
			char nextC = getNext();
			switch (nextC) {
			case 'p':
				code.addChild(new EndPointStatement(m_endpoint));
				m_endpoint++;
				break;
			case 'a':
				code.addChild(new AddStatement(m_unique));
				break;
			case 'd':
				lastElement = new DoWhileStatement(m_unique);
				code.addChild(lastElement);
				break;
			case 'e':
				lastElement = new SelectStatement(m_unique);
				code.addChild(lastElement);
				break;
			case 'f':
				lastElement = new ForStatement(m_unique);
				code.addChild(lastElement);
				break;
			case 'i':
				lastElement = new IfStatement(m_unique);
				code.addChild(lastElement);
				break;
			case 's':
				lastElement = new SwitchStatement(m_unique);
				code.addChild(lastElement);
				break;
			case 'w':
				lastElement = new WhileStatement(m_unique);
				code.addChild(lastElement);
				break;
			case '(':
				if (lastElement == null) {
					error("'(' detected without a node before it.");
				}
				generate(lastElement);
				break;
			case ')':
				return;
			default:
				error("'" + nextC + "' unsupported node type.");
				break;
			}
		} while (!done());
	}

	private static void createNonStucturalTest(String description, String optLevel) {
		
		try {
			m_testDescription = description;
			m_unique = 0;
			m_endpoint = 0;
			CodeElement test = new TestCodeElement(description);
			generate(test);
			
			String testName = test.getName()+"_O" + optLevel;
			m_testCount++;
			
			File dir = new File(testName);
			if (!dir.exists()) {
				boolean success = (new File(testName)).mkdir();
				if (!success) {
					error("Error: failed to mkdir '" + testName + "'");
				}
			}
		  
			BufferedWriter sourceFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".xc"));
			test.emitSource(sourceFile, 0);
			sourceFile.close();
	
			BufferedWriter runExpectFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".txt.expect.regex"));
			runExpectFile.close();
	
			// Create empty build expect
			BufferedWriter logExpectFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".log.expect"));
			logExpectFile.close();
	
			BufferedWriter testPerlFile = new BufferedWriter(new FileWriter(testName + "/test.pl"));
			writeNonStucturalTestPerl(testPerlFile, testName, "-O"+optLevel);
			testPerlFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createStucturalTest(String description) {
		
		try {
			m_testDescription = description;
			m_unique = 0;
			m_endpoint = 0;
			CodeElement test = new TestCodeElement(description);
			generate(test);
			
			String testName = test.getName();
			m_testCount++;
			
			File dir = new File(testName);
			if (!dir.exists()) {
				boolean success = (new File(testName)).mkdir();
				if (!success) {
					error("Error: failed to mkdir '" + testName + "'");
				}
			}
		  
			BufferedWriter sourceFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".xc"));
			test.emitSource(sourceFile, 0);
			sourceFile.close();
	
			BufferedWriter runExpectFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".txt.expect.regex"));
			test.emitExpect(0, runExpectFile, 0);
			runExpectFile.close();
	
			// Create empty build expect
			BufferedWriter logExpectFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".log.expect"));
			logExpectFile.close();
	
			BufferedWriter testPerlFile = new BufferedWriter(new FileWriter(testName + "/test.pl"));
			writeStucturalTestPerl(testPerlFile, testName, "");
			testPerlFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeStucturalTestPerl(BufferedWriter file, String testName, String flags) throws IOException {
		file.write("#!/usr/bin/perl\n");
		file.write("use strict;\n");
		file.write("use warnings;\n");
		file.write("\n");
		file.write("my $genDir  = shift(@ARGV);\n");
		file.write("my $arch    = shift(@ARGV);\n");
		file.write("my $target  = shift(@ARGV);\n");
		file.write("my $noBuild = shift(@ARGV);\n");
		file.write("\n");
		file.write("if (!$noBuild) {\n");
		file.write("  system(\"xcc -target=$target -march=$arch " + flags +" -fxta-info -g " + testName + ".xc -o $genDir/" + testName + ".xe > $genDir/" + testName + ".log 2>&1\");\n");
		file.write("}\n");
		file.write("system(\"@ARGV -config timeout 5 -load $genDir/" + testName + ".xe \" .\n");
		file.write("       \"-analyse function test \" .\n");
		file.write("       \"-print structure - \" .\n");
		file.write("       \"-exit > $genDir/" + testName + ".txt 2>&1\");\n");
		file.write("\n");
	}
	
	private static void writeNonStucturalTestPerl(BufferedWriter file, String testName, String flags) throws IOException {
		file.write("#!/usr/bin/perl\n");
		file.write("use strict;\n");
		file.write("use warnings;\n");
		file.write("\n");
		file.write("my $genDir  = shift(@ARGV);\n");
		file.write("my $arch    = shift(@ARGV);\n");
		file.write("my $target  = shift(@ARGV);\n");
		file.write("my $noBuild = shift(@ARGV);\n");
		file.write("\n");
		file.write("if (!$noBuild) {\n");
		file.write("  system(\"xcc -target=$target -march=$arch " + flags +" -fxta-info -g " + testName + ".xc -o $genDir/" + testName + ".xe > $genDir/" + testName + ".log 2>&1\");\n");
		file.write("}\n");
		file.write("system(\"@ARGV -config timeout 5 -load $genDir/" + testName + ".xe \" .\n");
		file.write("       \"-analyse function test \" .\n");
		file.write("       \"-analyse path start endpoint0 \" .\n");
		file.write("       \"-analyse path start endpoint1 \" .\n");
		file.write("       \"-analyse path start endpoint2 \" .\n");
		file.write("       \"-analyse path endpoint0 end \" .\n");
		file.write("       \"-analyse path endpoint1 end \" .\n");
		file.write("       \"-analyse path endpoint2 end \" .\n");
		file.write("       \"-analyse path endpoint0 endpoint2 \" .\n");
		file.write("       \"-exit > $genDir/" + testName + ".txt 2>&1\");\n");
		file.write("\n");
	}

	static public void main(String[] args) {
		String tests[] = {
				
				"pad(ai(a)p)p",
				"pad(ai(a)(p))p",
				/*
				"papp",
				"pd(pap)",
				"pd(pd(ap))",
				"pd(d(pd(ap)))",
				"pd(d(pd(d(ap))))",
				"pad(pap)",
				"pad(apd(ap))",
				"pad(ad(apd(ap)))",
				"pad(ad(apd(ad(ap))))",
				
				"pd(i(pap))",
				"pd(d(pi(ap)))",
				"pd(d(pd(i(ap))))",
				"pd(d(d(pd(i(ap)))))",
				"pi(a)d(pi(ap))",
				"pi(a)d(pi(a)d(i(ap)))",
				"pi(a)d(i(a)d(pi(a)d(i(ap))))",
				"pi(a)d(i(a)d(pi(a)d(i(a)d(i(ap)))))",
				
				"pf(pap)",
				"pf(pf(ap))",
				"pf(f(pf(f(ap))))",
				"paf(pap)",
				"paf(apf(ap))",
				"paf(af(paf(af(ap))))",
				"pf(i(pap))",
				"pf(f(pi(ap)))",
				"pf(f(pf(f(i(ap)))))",
				"pi(a)pf(i(ap))",
				"pi(a)f(pi(a)f(i(ap)))",
				"pi(a)f(pi(a)f(i(a)f(i(a)f(i(ap)))))",
				
				"pw(pap)",
				"pw(pw(ap))",
				"pw(w(w(pw(ap))))",
				"paw(pap)",
				"paw(apw(ap))",
				"paw(aw(apw(aw(ap))))",
				"pw(pi(ap))",
				"pw(w(pi(ap)))",
				"pw(w(pw(w(i(ap)))))",
				"pi(a)w(i(pap))",
				"pi(a)w(i(pa)w(i(ap)))",
				"pi(a)w(i(a)w(i(pa)w(i(a)w(i(ap)))))",
				
				"pd(i(pa)(ap))",
				"pd(d(i(pa)(ap)))",
				"pd(d(d(i(pa)(ap))))",
				"pd(d(pd(d(i(a)(ap)))))",
				"pi(a)(pa)d(i(a)(ap))",
				"pi(a)(a)d(i(ap)(a)d(i(a)(ap)))",
				"pi(a)(a)d(i(a)(pa)d(i(a)(a)d(i(a)(ap))))",
				"pi(a)(a)d(i(a)(a)d(i(pa)(a)d(i(a)(a)d(i(a)(ap)))))",
				
				"pf(pap)",
				"pf(pf(ap))",
				"pf(pf(f(f(ap))))",
				"paf(pap)",
				"paf(apf(ap))",
				"paf(af(apf(af(ap))))",
				"pf(i(pa)(ap))",
				"pf(f(i(pa)(ap)))",
				"pf(f(f(pf(i(a)(ap)))))",
				"pi(a)(pa)f(i(a)(ap))",
				"pi(a)(a)f(i(pa)(a)f(i(a)(ap)))",
				"pi(a)(a)f(i(a)(a)f(i(pa)(a)f(i(a)(a)f(i(a)(ap)))))",
				
				"pw(pap)",
				"pw(w(pap))",
				"pw(w(pw(w(ap))))",
				"papw(ap)",
				"paw(paw(ap))",
				"paw(aw(paw(aw(ap))))",
				"pw(i(pa)(ap))",
				"pw(w(pi(a)(ap)))",
				"pw(w(w(pw(i(a)(ap)))))",
				"pi(a)(pa)w(i(a)(ap))",
				"pi(a)(a)w(i(pa)(a)w(i(a)(ap)))",
				"pi(a)(a)w(i(a)(a)w(i(a)(pa)w(i(a)(a)w(i(a)(ap)))))",
				
				
				"ps(pa)(ap)",
				"ps(a)(pa)(ap)",
				"ps(pa)(a)(a)(ap)",
				
				"pd(s(pa)(ap))",
				"pd(s(a)(pa)(ap))",
				"pd(s(a)(pa)(a)(ap))",
				
				"ps(d(pa))(d(ap))",
				"ps(d(a))(d(pa))(d(ap))",
				"ps(d(pa))(d(a))(d(a))(d(ap))",
				
				"pd(s(d(ap))(d(ap)))",
				"pd(s(d(ap))(d(a))(d(ap)))",
				"pd(s(d(ap))(d(a))(d(a))(d(ap)))",
				
				"ps(s(pa)(a))(ap)",
				"ps(s(a)(pa))(s(a)(ap))",
				"ps(s(a)(s(pa)(a)))(s(a)(ap))",
				"ps(s(pa)(s(a)(a)))(s(a)(s(a)(ap)))",

				"pe(pa)(ap)",
				"pe(a)(pa)(ap)",
				"pe(a)(pa)(a)(ap)",
				
				"pd(e(pa)(ap))",
				"pd(e(pa)(a)(ap))",
				"pd(e(a)(pa)(a)(ap))",
				
				"pe(d(pa))(d(ap))",
				"pe(d(a))(d(pa))(d(ap))",
				"pe(d(pa))(d(a))(d(a))(d(ap))",
				
				"pd(e(d(pa))(d(ap)))",
				"pd(e(d(a))(d(ap))(d(ap)))",
				"pd(e(d(pa))(d(a))(d(a))(d(ap)))",
				
				"pe(e(pa)(a))(ap)",
				"pe(e(a)(pa))(e(a)(ap))",
				"pe(e(a)(e(pa)(a)))(e(a)(ap))",
				"pe(e(pa)(e(a)(a)))(e(a)(e(a)(ap)))",
				*/
		};

		m_testCount = 0;
	  for (String testDesc : tests) {
		 /*
		  createNonStucturalTest(testDesc, "0");
		  createNonStucturalTest(testDesc, "1");
		  createNonStucturalTest(testDesc, "2");
		  createNonStucturalTest(testDesc, "3");
		  createNonStucturalTest(testDesc, "s");
		  */
		  createStucturalTest(testDesc);
	  }
	}
}
