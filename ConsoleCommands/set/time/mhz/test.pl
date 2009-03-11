#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/mhz.log 2>&1");
}

system("@ARGV " .
       "set time 0x100c8 1 mhz " .
       "load $genDir/test.xe " .
       "set time 0x100c8 1 mhz " .
       "list times " .
       "unset time 0x100c8 " .
       "list times " .
       "set time 0x100c8 0.2 mhz " .
       "list times " .
       "unset time x " .
       "unset time .dfbjdb " .
       "list times " .
       "unset time 0x100c8 " .
       "list times " .
       "analyse function main " .
       "exit > $genDir/mhz.txt 2>&1");

