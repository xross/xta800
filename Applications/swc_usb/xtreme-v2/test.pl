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
    system(" xmake -f ./xud/build/Makefile                   > $genDir/$directory.log 2>&1");
    system(" xmake -f ./apps/audio/xtreme-v2/build/Makefile >> $genDir/$directory.log 2>&1");
    system(" xmake -f ./xud/build/Makefile  cleantemps      >> $genDir/$directory.log 2>&1");
    system(" cp ./apps/audio/xtreme-v2/bin/xtreme-v2.xe $genDir/test.xe");
}

system("@ARGV load $genDir/test.xe " .
        "source cmds.txt > $genDir/$directory.txt 2>&1");


