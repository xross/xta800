#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info test.xc -o $genDir/test.xe > $genDir/call.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "echo ==========CORE_0========== " .
       "config proc 0 0 " .
       "add call 0x10000 bob " .
       "add call test.xc:9 bill " .
       "add call test.xc:10 ben " .
       "add call test.xc:11 fred " .
       "list calls " .
       "echo ==========CORE_1========== " .
       "config proc 0 1 " .
       "add call 0x10000 bob " .
       "add call test.xc:9 bill " .
       "add call test.xc:10 ben " .
       "add call test.xc:11 fred " .
       "list calls " .
       "echo ==========CORE_2========== " .
       "config proc 0 2 " .
       "add call 0x10000 bob " .
       "add call test.xc:9 bill " .
       "add call test.xc:10 ben " .
       "add call test.xc:11 fred " .
       "list calls " .
       "echo ==========CORE_3========== " .
       "config proc 0 3 " .
       "add call 0x10000 bob " .
       "add call test.xc:9 bill " .
       "add call test.xc:10 ben " .
       "add call test.xc:11 fred " .
       "list calls " .
       "exit > $genDir/call.txt 2>&1");

