#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/ns.log 2>&1");
}

system("@ARGV " .
       "set time 0x100c8 1000 ns " .
       "load $genDir/test.xe " .
       "set time 0x100c8 1000 ns " .
       "list times " .
       "unset time 0x100c8 " .
       "list times " .
       "set time 0x100c8 5000 ns " .
       "list times " .
       "unset time x " .
       "unset time .dfbjdb " .
       "list times " .
       "unset time 0x100c8 " .
       "list times " .
       "analyse function main " .
       "exit > $genDir/ns.txt 2>&1");

