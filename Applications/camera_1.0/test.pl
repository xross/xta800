#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "camera_1.0";

if (!$noBuild) {
    system("xmake>  $genDir/$directory.log 2>&1");
    system("mv  bin/XDK/camera.xe $genDir/test.xe >  $genDir/$directory.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "config proc 0 1 " .
        "analyse path a a " .
        "print structure - " .
        "-exit > $genDir/$directory.txt 2>&1");

