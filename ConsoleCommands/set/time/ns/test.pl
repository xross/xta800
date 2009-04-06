#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/ns.log 2>&1");
}

system("@ARGV " .
       "set time inst 1000 ns " .
       "load $genDir/test.xe " .
       "analyse function main " .
       "set time inst 1000 ns " .
       "list knowns - " .
       "unset time inst " .
       "list knowns - " .
       "set time inst 5000 ns " .
       "list knowns - " .
       "unset time x " .
       "unset time .dfbjdb " .
       "list knowns - " .
       "unset time inst " .
       "list knowns - " .
       "analyse function main " .
       "exit > $genDir/ns.txt 2>&1");

