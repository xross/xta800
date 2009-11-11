#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake TARGET=$target TARGETDIR=$genDir > $genDir/sid_demo.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "> $genDir/sid_demo.txt 2>&1");

