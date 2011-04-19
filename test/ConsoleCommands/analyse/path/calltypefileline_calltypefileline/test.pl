#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/calltypefileline_calltypefileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/calltypefileline_calltypefileline.txt 2>&1");

