#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("touch $genDir/load_file.log");
}

# Doesnt exist
system("@ARGV load $genDir/john.xe " .
       "analyse function main " .
       "exit > $genDir/load_file.txt 2>&1");

# Exists but isn't an xe
system("@ARGV load sponge.xe " .
       "analyse function main " .
       "exit >> $genDir/load_file.txt 2>&1");
       
