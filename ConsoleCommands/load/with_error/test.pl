#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/with_error.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
  "status " .
  "load not_found.xe " .
  "status " .
  "-exit > $genDir/with_error.txt 2>&1");

