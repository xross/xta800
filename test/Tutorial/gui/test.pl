#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -O1 tutorial_gui.xc core0.xc core1.xc -o $genDir/tutorial_gui.xe > $genDir/gui.log 2>&1");
}
system("@ARGV load $genDir/tutorial_gui.xe " .
       "source cmds.txt > $genDir/gui.txt 2>&1");
