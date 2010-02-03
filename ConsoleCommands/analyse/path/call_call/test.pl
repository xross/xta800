#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/call_call.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config core 0 0 " .
       "-list calls " .
       "analyse path call_function_a0 call_function_b0 " .
       "analyse path call_function_a1 call_function_b1 " .
       "-config core 0 1 " .
       "-list calls " .
       "analyse path call_function_a1 call_function_b1 " .
       "analyse path call_function_a0 call_function_b0 " .
       "print summary " .
       "-exit > $genDir/call_call.txt 2>&1");

