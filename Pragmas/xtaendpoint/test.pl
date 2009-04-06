#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/xtaendpoint.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list endpoints " .
       "list allendpoints " .
       "exit > $genDir/xtaendpoint.txt 2>&1");

