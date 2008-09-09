#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc test.xc -o $genDir/test.xe > $genDir/change_threads.log 2>&1");
system("xta threads 4 status exit > $genDir/change_threads.txt 2>&1");

