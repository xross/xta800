#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc up/up.xc -o $genDir/test.xe > $genDir/multi_directory_with_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " . 
       "list srclabels " .
       "analyse function main " .
       "print src - " .
       "-exit  > $genDir/multi_directory_with_labels.txt 2>&1");

