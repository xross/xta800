#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -O2 -fxta-info -g test_pi-a_w-i-pa_w-i-ap____O2.xc -o $genDir/test_pi-a_w-i-pa_w-i-ap____O2.xe > $genDir/test_pi-a_w-i-pa_w-i-ap____O2.log 2>&1");
}
system("@ARGV -config timeout 5 -load $genDir/test_pi-a_w-i-pa_w-i-ap____O2.xe " .
       "-analyse function test " .
       "-analyse path start endpoint0 " .
       "-analyse path start endpoint1 " .
       "-analyse path start endpoint2 " .
       "-analyse path endpoint0 end " .
       "-analyse path endpoint1 end " .
       "-analyse path endpoint2 end " .
       "-analyse path endpoint0 endpoint2 " .
       "-add exclusion endpoint0 " .
       "-analyse function test " .
       "-remove exclusion endpoint0 " .
       "-add exclusion endpoint1 " .
       "-analyse function test " .
       "-remove exclusion endpoint1 " .
       "-add exclusion endpoint2 " .
       "-analyse function test " .
       "-remove exclusion endpoint2 " .
       "-add exclusion endpoint0 " .
       "-add exclusion endpoint1 " .
       "-analyse function test " .
       "-remove exclusion endpoint0 " .
       "-remove exclusion endpoint1 " .
       "-add exclusion endpoint0 " .
       "-add exclusion endpoint2 " .
       "-analyse function test " .
       "-remove exclusion endpoint0 " .
       "-remove exclusion endpoint2 " .
       "-add exclusion endpoint1 " .
       "-add exclusion endpoint2 " .
       "-analyse function test " .
       "-exit > $genDir/test_pi-a_w-i-pa_w-i-ap____O2.txt 2>&1");

