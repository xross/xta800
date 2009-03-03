#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g -O3 mii.xc -o $genDir/test.xe > $genDir/console.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "set constraint !packet_start " .
       "-analyse path receive_loop receive_loop " .
       "summary complete " .
       "-assert maxtime 320 " .
       "set constraint " .
       "-analyse path eop_detected packet_start " .
       "summary complete " .
       "-assert maxtime 1240 " .
       "exit > $genDir/console.txt 2>&1");
