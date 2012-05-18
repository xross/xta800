#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/with_exclusions.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-add exclusion g,f,then " .
       "-add exclusion h,f,else " .
       "-analyze function f " .
       "-print structure - " .
       "-exit > $genDir/with_exclusions.txt 2>&1");

