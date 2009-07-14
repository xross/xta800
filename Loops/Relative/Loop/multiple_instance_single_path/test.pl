#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/multiple_instance_single_path.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopmax - loop 100 " .
       "print completed_loops " .
       "unset loopmax - loop " .
       "print completed_loops " .
       "exit > $genDir/multiple_instance_single_path.txt 2>&1");

