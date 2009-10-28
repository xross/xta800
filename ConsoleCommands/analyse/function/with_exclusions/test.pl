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
       "-analyse function f " .
       "-print structure 0 " .
       "-add exclusion g,f,then " .
       "-add exclusion h,f,else " .
       "-analyse function f " .
       "-print structure 1 " .
       "-print structure 2 " .
       "-exit > $genDir/with_exclusions.txt 2>&1");

