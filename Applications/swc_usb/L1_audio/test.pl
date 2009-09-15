#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "L1_audio";

if (!$noBuild) {
    system(" xmake -f ./xud_lite/build/Makefile       > $genDir/$directory.log 2>&1");
    system(" xmake -f ./apps/L1_audio/build/Makefile >> $genDir/$directory.log 2>&1");
    system(" xmake -f ./xud_lite/build/Makefile cleantemps >> $genDir/$directory.log 2>&1");
    system(" cp ./apps/L1_audio/bin/L1_audio.xe $genDir/test.xe");
}

system("@ARGV load $genDir/test.xe " .
        "source cmds.txt > $genDir/$directory.txt 2>&1");


