package com.xmos.xta.xctestgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGen {

	static String m_testDescription;
	static int m_unique;
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

	private static void createTest(String description, String optLevel) {
		
		
		try {
			m_testDescription = description;
			m_unique = 0;
			
			CodeElement test = new TestCodeElement();
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
			//test.emitExpect(0, runExpectFile, 0);
			runExpectFile.close();
	
			// Create empty build expect
			BufferedWriter logExpectFile = new BufferedWriter(new FileWriter(testName + "/" + testName + ".log.expect"));
			logExpectFile.close();
	
			// Create empty build expect
			BufferedWriter testPerlFile = new BufferedWriter(new FileWriter(testName + "/test.pl"));
			writeTestPerl(testPerlFile, testName, "-O"+optLevel);
			testPerlFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeTestPerl(BufferedWriter file, String testName, String flags) throws IOException {
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

	static public void main(String[] args) {
		String tests[] = {
				"a",
				"d(a)",
				"d(d(a))",
				"d(d(d(a)))",
				"d(d(d(d(a))))",
				"ad(a)",
				"ad(ad(a))",
				"ad(ad(ad(a)))",
				"ad(ad(ad(ad(a))))",
				
				"d(i(a))",
				"d(d(i(a)))",
				"d(d(d(i(a))))",
				"d(d(d(d(i(a)))))",
				"i(a)d(i(a))",
				"i(a)d(i(a)d(i(a)))",
				"i(a)d(i(a)d(i(a)d(i(a))))",
				"i(a)d(i(a)d(i(a)d(i(a)d(i(a)))))",
				
				"f(a)",
				"f(f(a))",
				"f(f(f(f(a))))",
				"af(a)",
				"af(af(a))",
				"af(af(af(af(a))))",
				"f(i(a))",
				"f(f(i(a)))",
				"f(f(f(f(i(a)))))",
				"i(a)f(i(a))",
				"i(a)f(i(a)f(i(a)))",
				"i(a)f(i(a)f(i(a)f(i(a)f(i(a)))))",
				
				"w(a)",
				"w(w(a))",
				"w(w(w(w(a))))",
				"aw(a)",
				"aw(aw(a))",
				"aw(aw(aw(aw(a))))",
				"w(i(a))",
				"w(w(i(a)))",
				"w(w(w(w(i(a)))))",
				"i(a)w(i(a))",
				"i(a)w(i(a)w(i(a)))",
				"i(a)w(i(a)w(i(a)w(i(a)w(i(a)))))",
				
				"d(i(a)(a))",
				"d(d(i(a)(a)))",
				"d(d(d(i(a)(a))))",
				"d(d(d(d(i(a)(a)))))",
				"i(a)(a)d(i(a)(a))",
				"i(a)(a)d(i(a)(a)d(i(a)(a)))",
				"i(a)(a)d(i(a)(a)d(i(a)(a)d(i(a)(a))))",
				"i(a)(a)d(i(a)(a)d(i(a)(a)d(i(a)(a)d(i(a)(a)))))",
				
				"f(a)",
				"f(f(a))",
				"f(f(f(f(a))))",
				"af(a)",
				"af(af(a))",
				"af(af(af(af(a))))",
				"f(i(a)(a))",
				"f(f(i(a)(a)))",
				"f(f(f(f(i(a)(a)))))",
				"i(a)(a)f(i(a)(a))",
				"i(a)(a)f(i(a)(a)f(i(a)(a)))",
				"i(a)(a)f(i(a)(a)f(i(a)(a)f(i(a)(a)f(i(a)(a)))))",
				
				"w(a)",
				"w(w(a))",
				"w(w(w(w(a))))",
				"aw(a)",
				"aw(aw(a))",
				"aw(aw(aw(aw(a))))",
				"w(i(a)(a))",
				"w(w(i(a)(a)))",
				"w(w(w(w(i(a)(a)))))",
				"i(a)(a)w(i(a)(a))",
				"i(a)(a)w(i(a)(a)w(i(a)(a)))",
				"i(a)(a)w(i(a)(a)w(i(a)(a)w(i(a)(a)w(i(a)(a)))))",
				
				
				"s(a)(a)",
				"s(a)(a)(a)",
				"s(a)(a)(a)(a)",
				
				"d(s(a)(a))",
				"d(s(a)(a)(a))",
				"d(s(a)(a)(a)(a))",
				
				"s(d(a))(d(a))",
				"s(d(a))(d(a))(d(a))",
				"s(d(a))(d(a))(d(a))(d(a))",
				
				"d(s(d(a))(d(a)))",
				"d(s(d(a))(d(a))(d(a)))",
				"d(s(d(a))(d(a))(d(a))(d(a)))",
				
				"s(s(a)(a))(a)",
				"s(s(a)(a))(s(a)(a))",
				"s(s(a)(s(a)(a)))(s(a)(a))",
				"s(s(a)(s(a)(a)))(s(a)(s(a)(a)))",

				"e(a)(a)",
				"e(a)(a)(a)",
				"e(a)(a)(a)(a)",
				
				"d(e(a)(a))",
				"d(e(a)(a)(a))",
				"d(e(a)(a)(a)(a))",
				
				"e(d(a))(d(a))",
				"e(d(a))(d(a))(d(a))",
				"e(d(a))(d(a))(d(a))(d(a))",
				
				"d(e(d(a))(d(a)))",
				"d(e(d(a))(d(a))(d(a)))",
				"d(e(d(a))(d(a))(d(a))(d(a)))",
				
				"e(e(a)(a))(a)",
				"e(e(a)(a))(e(a)(a))",
				"e(e(a)(e(a)(a)))(e(a)(a))",
				"e(e(a)(e(a)(a)))(e(a)(e(a)(a)))",
		};

		m_testCount = 0;
	  for (String testDesc : tests) {
		  createTest(testDesc, "0");
		  createTest(testDesc, "1");
		  createTest(testDesc, "2");
		  createTest(testDesc, "3");
		  createTest(testDesc, "s");
	  }
	}
}
