#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc main.xc func.xc checkCoreId.s -o $genDir/test.xe > $genDir/change_proc.log 2>&1");
system("xta load $genDir/test.xe proc 128 3 status exit > $genDir/change_proc.txt 2>&1");

