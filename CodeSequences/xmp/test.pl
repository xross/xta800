#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=XMP -O3 -g main.xc -o $genDir/test.xe > $genDir/xmp.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "-status " .
       "-analyse path loop loop " .
       "-print summary " .
       "-exit > $genDir/xmp.txt 2>&1");

