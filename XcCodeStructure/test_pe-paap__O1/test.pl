#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -O1 -fxta-info -g test_pe-paap__O1.xc -o $genDir/test_pe-paap__O1.xe > $genDir/test_pe-paap__O1.log 2>&1");
}
system("@ARGV -config timeout 5 -load $genDir/test_pe-paap__O1.xe " .
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
       "-exit > $genDir/test_pe-paap__O1.txt 2>&1");

