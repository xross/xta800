#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/cycles.log 2>&1");
}

system("@ARGV " .
       "set time inst 1000 cycles " .
       "load $genDir/test.xe " .
       "analyse function main " .
       "set time inst 1000 cycles " .
       "list knowns - " .
       "unset time inst " .
       "list knowns - " .
       "set time inst 5000 cycles " .
       "list knowns - " .
       "unset time x " .
       "unset time .dfbjdb " .
       "list knowns - " .
       "unset time inst " .
       "list knowns - " .
       "analyse function main " .
       "exit > $genDir/cycles.txt 2>&1");

