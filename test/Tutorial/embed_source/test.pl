#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

open(LOG, ">$genDir/embed_source.log");
copy("uart-loopback.xc", "$genDir/uart-loopback.xc") or print(LOG "Failed to copy source\n");
copy("uart-loopback.xc", "uart-loopback.xc.bak.expect") or print(LOG "Failed to copy expect\n");
close(LOG);

chdir("$genDir");

system("xcc -target=$target -march=$arch -g -O3 uart-loopback.xc -o uart-loopback.xe >> embed_source.log 2>&1");

system("@ARGV load uart-loopback.xe " .
       "source ../cmds.txt " .
       "-exit > embed_source.txt 2>&1");

system("xcc -target=$target -march=$arch -g -O3 uart-loopback.xc -o uart-loopback.xe >> embed_source.log 2>&1");

system("@ARGV load uart-loopback.xe " .
       "print summary " .
       "-exit >> embed_source.txt 2>&1");

chdir("..");
