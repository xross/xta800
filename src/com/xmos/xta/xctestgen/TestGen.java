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

	private static void createTest(String description) {
		try {
			m_testDescription = description;
			m_unique = 0;
			
			CodeElement test = new TestCodeElement();
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
	
			// Create empty build expect
			BufferedWriter testPerlFile = new BufferedWriter(new FileWriter(testName + "/test.pl"));
			writeTestPerl(testPerlFile, testName);
			testPerlFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeTestPerl(BufferedWriter file, String testName) throws IOException {
		file.write("#!/usr/bin/perl\n");
		file.write("use strict;\n");
		file.write("use warnings;\n");
		file.write("\n");
		file.write("my $genDir  = shift(@ARGV);\n");
		file.write("my $arch    = shift(@ARGV);\n");
		file.write("my $noBuild = shift(@ARGV);\n");
		file.write("\n");
		file.write("if (!$noBuild) {\n");
		file.write("  system(\"xcc -march=$arch -g " + testName + ".xc -o $genDir/" + testName + ".xe > $genDir/" + testName + ".log 2>&1\");\n");
		file.write("}\n");
		file.write("system(\"@ARGV load $genDir/" + testName + ".xe \" .\n");
		file.write("       \"analyse function test \" .\n");
		file.write("       \"summary complete_loops \" .\n");
		file.write("       \"exit > $genDir/" + testName + ".txt 2>&1\");\n");
		file.write("\n");
	}

	static public void main(String[] args) {
		String tests[] = {
				"a",
				"d(a)",
				"e(a)(a)(a)(a)",
				"f(a)",
				"i(a)",
				"s(a)(a)(a)(a)",
				"w(a)",
				"i(a)i(i(a))",
				"i(a)i(a)i(i(a)i(a))w(i(a)i(a)w(i(a)))",
				"i(a)i(a)i(a)i(a)i(a)i(a)",
				"i(i(i(i(i(i(i(i(a))))))))",
				//"s(w(i(a)i(a)i(s(a)(a))))(i(i(a)(a))i(a))(a)(i(a))",
				//"s(e(i(a))(w(a)))i(a)w(i(a))f(a)",
				"s(i(a)w(i(a)i(a)i(s(a)(a))))",
				//"d(w(i(s(e(a)(a))(e(a)(a)))(s(a)(a))))",
				//"d(aw(i(s(e(a)(a))(e(a)(a)))(s(a)(a))))",
				//"d(w(a))",
				//"d(w(a)w(a))",
				//"d(d(w(a)))",
				//"d(d(d(w(a))))",
				//"d(d(d(d(w(a)))))",
				//"d(ad(d(d(w(a)))))",
				//"d(ad(ad(d(w(a)))))",
				//"d(ad(ad(ad(w(a)))))",
				//"d(i(a)d(d(d(w(a)))))",
				//"d(i(a)d(i(a)d(d(w(a)))))",
				//"d(i(a)d(i(a)d(i(a)d(w(a)))))",
				//"d(d(i(a)d(d(w(a)))))",
				//"d(d(d(i(a)d(w(a)))))",
				//"d(d(d(d(i(a)w(a)))))",
				//"d(f(a)d(d(d(w(a)))))",
				//"d(d(f(a)d(d(w(a)))))",
				//"d(d(d(f(a)d(w(a)))))",
				//"d(d(d(d(f(a)w(a)))))",
				//"d(f(a)d(f(a)d(d(w(a)))))",
				//"d(d(a)d(d(d(w(a)))))",
				//"d(d(d(a)d(d(w(a)))))",
				//"d(d(d(d(a)d(w(a)))))",
				//"d(d(d(d(d(a)w(a)))))",
				//"d(d(a)d(d(a)d(d(a)d(w(a)))))",				
				//"d(w(w(w(w(a)))))",
				//"e(e(s(i(a)i(a)i(w(a)))(d(a)))(a))(f(a))",
				//"f(i(a)(w(a)))",
				//"f(i(a)(w(a))(e(a)(a)))",
				//"f(i(a)(w(a))(e(a)(a))(s(a)(a)))",
				"w(w(a))w(aaa)",
		};

		m_testCount = 0;
	  for (String testDesc : tests) {
		  createTest(testDesc);
	  }
	}
}
