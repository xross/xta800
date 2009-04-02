#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "g1";

if (!$noBuild) {
    system("xcc -Wall -g -O3 link.S top.xc -o $genDir/test.xe >  $genDir/$directory.log 2>&1");
}

my $filename = "trace";
my $compareFnnops = 0;

system("@ARGV load $genDir/test.xe " .

        "analyse path sdr_input0_0 sdr_input0_1 " .
        "set required - 333 ns " .
        "analyse path sdr_input0_1 sdr_input0_2 " .
        "set required - 333 ns " .
        "analyse path sdr_input0_2 sdr_input0_3 " .
        "set required - 333 ns " .
        "analyse path sdr_input0_3 sdr_input0_0 " .
        "set required - 333 ns " .

        "analyse path sdr_input1_0 sdr_input1_1 " .
        "set required - 333 ns " .
        "analyse path sdr_input1_1 sdr_input1_2 " .
        "set required - 333 ns " .
        "analyse path sdr_input1_2 sdr_input1_3 " .
        "set required - 333 ns " .
        "analyse path sdr_input1_3 sdr_input1_0 " .
        "set required - 333 ns " .

        "analyse path sdr_input2_0 sdr_input2_1 " .
        "set required - 333 ns " .
        "analyse path sdr_input2_1 sdr_input2_2 " .
        "set required - 333 ns " .
        "analyse path sdr_input2_2 sdr_input2_3 " .
        "set required - 333 ns " .
        "analyse path sdr_input2_3 sdr_input2_0 " .
        "set required - 333 ns " .

        "analyse path sdr_input3_0 sdr_input3_1 " .
        "set required - 333 ns " .
        "analyse path sdr_input3_1 sdr_input3_2 " .
        "set required - 333 ns " .
        "analyse path sdr_input3_2 sdr_input3_3 " .
        "set required - 333 ns " .
        "analyse path sdr_input3_3 sdr_input3_0 " .
        "set required - 333 ns " .

        "print summary " .
        "-exit > $genDir/$directory.txt 2>&1");


