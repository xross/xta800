#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -fxta-info -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/function_out_and_back.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add exclusion the_great_outdoors " .
       "analyse path x x " .
       "-print structure - " .
       "-exit > $genDir/function_out_and_back.txt 2>&1");

