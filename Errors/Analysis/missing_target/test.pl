#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -nostartfiles -target=$target -march=$arch -g test.s -o $genDir/test.xe > $genDir/missing_target.log 2>&1");
}
# Need the 'status' to force it to load
system("@ARGV load $genDir/test.xe " .
       "status " .
       "-exit > $genDir/missing_target.txt 2>&1");

