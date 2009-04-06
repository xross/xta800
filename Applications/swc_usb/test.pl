#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "swc_usb";

if (!$noBuild) {
#make -f ./xud/build/Makefile &&
    system(" make -f ./tests/audio/build/Makefile  >  $genDir/$directory.log 2>&1");
    system(" cp ./tests/audio/bin/audio.xe $genDir/test.xe");
}

my $filename = "trace";
my $compareFnnops = 0;

system("@ARGV load $genDir/test.xe " .
        "config proc 0 2 " .
        "set constraint !errorCase " .
        "-analyse path physicalStart deliverFinish " .
        "set required - 5000 ns " .
        "print summary " .
        "-exit > $genDir/$directory.txt 2>&1");


