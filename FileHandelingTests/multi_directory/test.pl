#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc add/add.xc sub/sub.xc -o $genDir/test.xe > $genDir/multi_directory.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list sources " .
       "analyse function main " .
       "-print src - " .
       "-exit  > $genDir/multi_directory.txt 2>&1");

