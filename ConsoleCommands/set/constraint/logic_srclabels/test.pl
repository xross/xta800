#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/logic_srclabels.log 2>&1");
}
system("@ARGV " .
       "load $genDir/test.xe " .
       "source cmds.txt > $genDir/logic_srclabels.txt 2>&1");
