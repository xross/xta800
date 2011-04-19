#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "xtreme-v2";

if (!$noBuild) {
    system(" xmake TARGETDIR=$genDir > $genDir/$directory.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "source cmds.txt > $genDir/$directory.txt 2>&1");


