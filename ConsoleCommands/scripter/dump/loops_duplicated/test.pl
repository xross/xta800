#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

open(LOG, ">$genDir/loops_duplicated.log");
copy("test.xc", "$genDir/test.xc") or print(LOG "Failed to copy source\n");
copy("test.xc", "test.xc.bak.expect") or print(LOG "Failed to copy expect\n");
close(LOG);

chdir("$genDir");

system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> loops_duplicated.log 2>&1");

system("@ARGV load test.xe " .
       "source ../cmds.txt " .
       "echo ONE " .
       "scripter listrefs " .
       "source ../dump.txt " .
       "-exit > loops_duplicated.txt 2>&1");

system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> loops_duplicated.log 2>&1");

system("@ARGV load test.xe " .
       "source dump.txt " .
       "echo TWO " .
       "scripter listrefs " .
       "scripter dump " .
       "-exit >> loops_duplicated.txt 2>&1");

chdir("..");
