#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/threads.log 2>&1");
}
system("@ARGV config threads stdcore[0] 4 " .
       "status " .
       "load $genDir/test.xe " .
       "config threads stdcore[0] 4 " .
       "status " .
       "config threads stdcore[0] -1 " .
       "status " .
       "config threads stdcore[0] 10 " .
       "status " .
       "exit > $genDir/threads.txt 2>&1");

