#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc -g a.xc b.xc c.xc -o $genDir/test.xe > $genDir/list_functions.log 2>&1");
system("xta load $genDir/test.xe list functions exit > $genDir/list_functions.txt 2>&1");

