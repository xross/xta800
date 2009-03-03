#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/extra_arguments.log 2>&1");
}
system("@ARGV source cmds.txt " .
       "exit > $genDir/extra_arguments.txt 2>&1");

