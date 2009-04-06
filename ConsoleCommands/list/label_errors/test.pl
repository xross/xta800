#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/label_errors.log 2>&1");
}
system("@ARGV " .
       "source cmds1.txt " .
       "load $genDir/test.xe " .
       "source cmds2.txt " .
       "exit > $genDir/label_errors.txt 2>&1");

