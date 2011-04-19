#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch main.xc src1/src.xc src2/src.xc -o $genDir/test.xe > $genDir/xtalabel_multidirectory.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list srclabels " .
       "list allsrclabels " .
       "exit > $genDir/xtalabel_multidirectory.txt 2>&1");

