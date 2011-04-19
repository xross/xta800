#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/active_cores.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/active_cores.txt 2>&1");

