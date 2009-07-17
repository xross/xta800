#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -O3 -fxta-info -g test_pad-aprd-apr___O3.xc -o $genDir/test_pad-aprd-apr___O3.xe > $genDir/test_pad-aprd-apr___O3.log 2>&1");
}
system("@ARGV -config timeout 5 -load $genDir/test_pad-aprd-apr___O3.xe " .
       "-analyse function test " .
       "-analyse path start endpoint0 " .
       "-analyse path start endpoint1 " .
       "-analyse path start endpoint2 " .
       "-analyse path endpoint0 end " .
       "-analyse path endpoint1 end " .
       "-analyse path endpoint2 end " .
       "-analyse path endpoint0 endpoint2 " .
       "-exit > $genDir/test_pad-aprd-apr___O3.txt 2>&1");

