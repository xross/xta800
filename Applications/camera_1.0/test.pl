#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch -g -O3 main.xc lcd_svr.xc camera.xc sccb.xc line_buf.xc -o $genDir/test.xe > $genDir/test.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "-analyse path a a " .
        "print structure - " .
        "-exit > $genDir/test.txt 2>&1");

