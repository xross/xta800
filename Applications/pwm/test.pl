#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake TARGETDIR=$genDir > $genDir/pwm.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "> $genDir/pwm.txt 2>&1");

