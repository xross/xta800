#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake -C Release/ -f Makefile all -B TARGETDIR=$genDir TARGET=$target > $genDir/CAN.log 2>&1");
}

system("@ARGV load Release/CAN.xe " .
       "-source cmds.txt " .
       "-exit > $genDir/can.txt 2>&1");

