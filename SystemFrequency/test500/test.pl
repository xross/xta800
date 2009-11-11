#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc f.S test.xn -o $genDir/test.xe > $genDir/test500.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function f " .
       "-set required - 3 cycles " .
       "config proc 1 1 " .
       "analyse function f " .
       "-set required - 3 cycles " .
       "config proc 2 2 " .
       "analyse function f " .
       "-set required - 3 cycles " .
       "config proc 3 3 " .
       "analyse function f " .
       "-set required - 3 cycles " .
       "print summary " .
       "-exit > $genDir/test500.txt 2>&1");

