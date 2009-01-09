#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1b test.xc -o $genDir/test.xe > $genDir/multi_core_debug.log 2>&1");
}
system("@ARGV load $genDir/test.xe config proc 0 0 analyse function led1_thread summary complete_instructions print src 7 text.xc print src 4 text.xc config proc 0 1 analyse function button_listener summary complete_instructions analyse function led1_thread summary complete_instructions config proc 0 0 analyse function button_listener summary complete_instructions exit > $genDir/multi_core_debug.txt 2>&1");

