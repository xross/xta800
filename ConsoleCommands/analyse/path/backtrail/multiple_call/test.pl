#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/multiple_call.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path test1,func1,start test1,func1,stop " .
       "analyse path test1,func2,start test1,func2,stop " .
       "analyse path test2,func1,start test2,func1,stop " .
       "analyse path test2,func2,start test2,func2,stop " .
       "print summary " .
       "-exit > $genDir/multiple_call.txt 2>&1");

