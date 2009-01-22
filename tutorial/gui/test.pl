#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $pwd = $ENV{PWD};

if (!$noBuild) {
system("xcc -march=$arch -g tutorial_gui.xc -o $genDir/test.xe > $genDir/gui.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function resetTable " .
       "summary complete " .
       "analyse path $pwd/tutorial_gui.xc:13 $pwd/tutorial_gui.xc:18 " .
       "summary complete " .
       "analyse function functionCalls " .
       "summary complete " .
       "analyse function main " .
       "summary complete " .
       "config mode resolve " .
       "analyse function main " .
       "summary complete " .
       "exit > $genDir/gui.txt 2>&1");
