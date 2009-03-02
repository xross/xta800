#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/command_line.log 2>&1");
}
system("@ARGV  " .

       "add branch fromRef toRef " .
       "add branch 0x1000 0x1000 " .
       "add label codeRef string " .
       "add label 0x10000 string " .
       "add srclabel codeRef string " .
       "add srclabel 0x10000 string " .
       "add endpoint codeRef string " .
       "add endpoint 0x10000 string " .

       "analyse function main " .
       "analyse path label1 label2 " .
       "analyse path 0x1000 0x1000 " .

       "assert maxtime 999 " .
       "assert maxcycles 999 " .

       "config proc 9 9 " .
       "config freq 999 " .
       "config threads 99 " .
       "config srcpaths /test/path/srcpath/ " .

       "clear " .
       "echo hello " .
       "pwd " .
       "help " .
       "load invalid.xe " .

       "list branches " .
       "list labels " .
       "list srclabels " .
       "list endpoints " .
       "list allsrclabels " .
       "list allendpoints " .
       "list unknowns " .
       "list functions " .
       "list sources " .
       "list times " .
       "list constraint " .
       
       "print asm 99 " .
       "print src 99 " .
       "print mix 99 " .
       "print trace 99 " .

       "-remove branch branch1 " .
       "-remove label label1 " .
       "-remove srclabel label1 " .
       "-remove endpoint label1 " .

       "-pwd " .

       "-set loop 99 r 99 " .
       "-set time test.xc:12 ns 99 " .
       "-set time label1 cycles 99 " .
       "-set time 0x1000 cycles 99 " .
       "-set constraint expression " .
 
       "-source invalid.txt " .
       "status " .

       "summary simple " .
       "summary complete " .
       "summary complete_loops " .
       "summary complete_blocks " .
       "summary complete_instructions " .
       "summary unresolved " .
       "summary unresolved_loops " .
       "summary unresolved_blocks " .
       "summary unresolved_instructions " .
       "summary functions " .
       "summary sources " .
       "summary info 99 " .

       "unset time 0x10000 " .
       "version " .

       "history " .
       "exit > $genDir/command_line.txt 2>&1");

