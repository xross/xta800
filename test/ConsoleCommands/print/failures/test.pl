#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g -O1 test.xc -o $genDir/test.xe > $genDir/failures.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function div " .
       "analyze function test " .
       "-set required - 10 ns " .
       "analyze function test " .
       "-set required - 2000 ns " .
       "analyze function pausable " .
       "-set required - 1000 ns " .
       "source cmds.txt " .
       "-config Werror on " .
       "-print warnings " .
       "-exit > $genDir/failures.txt 2>&1");

