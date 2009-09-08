#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "swc_usbhost";

if (!$noBuild) {
    system("xmake>  $genDir/$directory.log 2>&1");
    system("mv  a.xe $genDir/test.xe >  $genDir/$directory.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "-analyse path 0x109ea 0x10a0a " .
        "-analyse path 0x10b10 0x10b04 " .
        "-add exclusion 0x10c9c " .
        "-analyse path host.xc:422 host.xc:422 " .
        "-analyse path host.xc:432 host.xc:442 " .
        "-remove exclusion 0x10c9c " .
        "-analyse path host.xc:464 host.xc:457 " .
        "-print summary " .         
        "-exit > $genDir/$directory.txt 2>&1");

