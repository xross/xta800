#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

open(LOG, ">$genDir/loop_scope.log");
copy("test.xc", "$genDir/test.xc") or print(LOG "Failed to copy source\n");
copy("test.xc", "test.xc.bak.expect") or print(LOG "Failed to copy expect\n");
close(LOG);

chdir("$genDir");

system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> loop_scope.log 2>&1");

system("@ARGV load test.xe " .
       "source ../cmds.txt " .
       "echo ONE " .
       "scripter listrefs " .
       "-exit > loop_scope.txt 2>&1");

system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> loop_scope.log 2>&1");

system("@ARGV source dump.txt " .
       "echo TWO " .
       "scripter listrefs " .
       "scripter dump " .
       "-exit >> loop_scope.txt 2>&1");

chdir("..");
