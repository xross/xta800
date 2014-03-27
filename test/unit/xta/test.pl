#!/usr/bin/perl

use strict;
use warnings;
use Env;

use lib "../";
use unittest;

my $tools_xta = "$ENV{'XMOS_ROOT'}/tools_xta";
my $infrLibsJava = "$ENV{'XMOS_ROOT'}/infr_libs_java";
my $separator = ($^O eq "MSWin32") ? ";" : ":";
my $java = ($^O eq "darwin") ? "java -d32" : "java";

my $xde    = shift(@ARGV);
my $genDir = shift(@ARGV);
my $regKey = shift(@ARGV);

chdir($genDir);
system("javac -d . -cp " .
       "$tools_xta/com.xmos.xta.core/src$separator" .
       "$tools_xta/com.xmos.xta.test/src$separator" .
       "$infrLibsJava/com.xmos.libs.core/src$separator" .
       "$infrLibsJava/com.xmos.libs.test/src$separator" .
       unittest::getClassPath() . " " .
       "$tools_xta/com.xmos.xta.test/src/com/xmos/xta/test/unit/AllTests.java" .
       "> build.log 2>&1");

system("$java -cp .$separator" .
       unittest::getClassPath() . " " .
       "org.junit.runner.JUnitCore " .
       "com.xmos.xta.test.unit.AllTests " .
       "> run.log 2>&1");

chdir("../");
exit($? >> 8);

