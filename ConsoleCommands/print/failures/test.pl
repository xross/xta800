#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/failures.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function div " .
       "analyse function test " .
       "-set required - 10 ns " .
       "analyse function test " .
       "-set required - 1000 ns " .
       "analyse function pausable " .
       "-set required - 1000 ns " .
       "source cmds.txt " .
       "-config Werror on " .
       "-print failures " .
       "-exit > $genDir/failures.txt 2>&1");

