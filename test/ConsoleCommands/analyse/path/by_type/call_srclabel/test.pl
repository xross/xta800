#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc func.s -o $genDir/test.xe > $genDir/call_srclabel.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyze path label1 label2 " .
       "-print structure - " .
       "-analyze path LABEL:label1 LABEL:label2 " .
       "-print structure - " .
       "-analyze path ENDPOINT:label1 ENDPOINT:label2 " .
       "-print structure - " .
       "-analyze path CALL:label2 CALL:label1 " .
       "-print structure - " .
       "-exit > $genDir/call_srclabel.txt 2>&1");

