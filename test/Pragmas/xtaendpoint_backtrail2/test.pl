#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g main.s test.s -o $genDir/test.xe > $genDir/xtaendpoint_backtrail2.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list endpoints " .
       "list allendpoints " .
       "analyse path start stop " .
       "set required - 100 ns " .
       "analyse path read1,start read1,stop " .
       "set required - 100 ns " .
       "analyse path read2,start read2,stop " .
       "set required - 100 ns " .
       "analyse path read3,start read3,stop " .
       "set required - 100 ns " .
       "analyse path read1,start read2,stop " .
       "set required - 100 ns " .
       "analyse path func1,read1,start func1,read1,stop " .
       "set required - 100 ns " .
       "analyse path func2,read2,start func2,read2,stop " .
       "set required - 100 ns " .
       "analyse path func1,read1,start func2,read2,stop " .
       "set required - 100 ns " .
       "analyse path func1,read1,start read3,stop " .
       "set required - 100 ns " .
       "print summary " .
       "exit > $genDir/xtaendpoint_backtrail2.txt 2>&1");

