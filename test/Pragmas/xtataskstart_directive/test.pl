#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/xtataskstart_directive.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list threadstartpoints " .
       "exit > $genDir/xtataskstart_directive.txt 2>&1");

