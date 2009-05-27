#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/xtathread_delimiters.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list threadstartpoints " .
       "list threadstoppoints " .
       "exit > $genDir/xtathread_delimiters.txt 2>&1");

