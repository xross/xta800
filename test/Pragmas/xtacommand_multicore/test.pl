#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc core0.xc core1.xc core2.xc core3.xc commands.xc -o $genDir/test.xe > $genDir/test.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-exit > $genDir/test.txt 2>&1");

