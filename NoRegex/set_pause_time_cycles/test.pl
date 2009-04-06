#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/set_pause_time_cycles.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .

       "list unknowns - " .
       "list knowns - " .
       "print completed_instructions " .
       "print trace - 0 " .

       "set time 0x10004 100 cycles " .

       "list unknowns - " .
       "list knowns - " .
       "print completed_instructions " .
       "print trace - 0 " .

       "-exit > $genDir/set_pause_time_cycles.txt 2>&1");

