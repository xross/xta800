#!/usr/bin/perl
use strict;
use warnings;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);


if (!$noBuild) {
my $cwd = getcwd();
system("xcc -target=$target -march=$arch -g $cwd/test.xc -o $genDir/test.xe > $genDir/test.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/test.txt 2>&1");

