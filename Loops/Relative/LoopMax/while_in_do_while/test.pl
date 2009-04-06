#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/while_in_do_while.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "print completed_loops " .
       "set loopmax - do 100 " .
       "set loopmax - while 50 " .
       "print completed_loops " .
       "exit > $genDir/while_in_do_while.txt 2>&1");

