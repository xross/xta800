#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("touch $genDir/broken_xe.log");
}
system("@ARGV load test_0.xe " .
       "load test_1.xe " .
       "load test_2.xe " .
       "status " .
       "-exit > $genDir/broken_xe.txt 2>&1");

