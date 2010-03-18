#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

open(LOG, ">$genDir/dump_script.log");
copy("uart-loopback.xc", "$genDir/uart-loopback.xc") or print(LOG "Failed to copy source\n");
copy("uart-loopback.xc", "uart-loopback.xc.bak.expect") or print(LOG "Failed to copy expect\n");
close(LOG);

chdir("$genDir");

system("xcc -target=$target -march=$arch -g -O3 uart-loopback.xc -o uart-loopback.xe >> dump_script.log 2>&1");

system("@ARGV load uart-loopback.xe " .
       "source ../cmds.txt " .
       "-exit > dump_script.txt 2>&1");

system("xcc -target=$target -march=$arch -g -O3 uart-loopback.xc -o uart-loopback.xe >> dump_script.log 2>&1");

system("@ARGV load uart-loopback.xe " .
       "source dump.txt " .
       "print summary " .
       "-exit >> dump_script.txt 2>&1");

chdir("..");
