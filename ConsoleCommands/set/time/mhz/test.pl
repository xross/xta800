#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/mhz.log 2>&1");
}

system("@ARGV " .
       "set time inst 1 mhz " .
       "load $genDir/test.xe " .
       "analyse function main " .
       "set time inst 1 mhz " .
       "list knowns - " .
       "unset time inst " .
       "list knowns - " .
       "set time inst 0.2 mhz " .
       "list knowns - " .
       "unset time x " .
       "unset time .dfbjdb " .
       "list knowns - " .
       "unset time 0x100c8 " .
       "list knowns - " .
       "analyse function main " .
       "exit > $genDir/mhz.txt 2>&1");

