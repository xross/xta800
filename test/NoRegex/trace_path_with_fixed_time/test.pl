#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/trace_path_with_fixed_time.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add pathtime a b 401.5 ns " .
       "analyse path a c " .
       "print trace - " .

       "-exit > $genDir/trace_path_with_fixed_time.txt 2>&1");

