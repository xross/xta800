#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/valid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config core 0 0 " .
       "-analyse function test0 " .
       "print summary " .
       "-config core 0 1 " .
       "-analyse function test1 " .
       "print summary " .
       "-config core 0 2 " .
       "-analyse function test2 " .
       "print summary " .
       "-config core 0 3 " .
       "-analyse function test3 " .
       "print summary " .
       "exit > $genDir/valid.txt 2>&1");

