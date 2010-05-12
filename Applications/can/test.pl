#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake TARGETDIR=$genDir TARGET=$target > $genDir/can.log 2>&1");
}

system("@ARGV load $genDir/can.xe " .
       "source cmds.txt " .
       "-exit " .
       "> $genDir/can.txt 2>&1");

