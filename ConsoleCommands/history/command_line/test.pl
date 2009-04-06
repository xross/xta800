#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/command_line.log 2>&1");
}
system("@ARGV  " .

       "add branch 0x10000 0x10000 " .
       "add label 0x10000 string " .
       "add srclabel test.xc:10 string " .
       "add endpoint input1 string " .

       "analyse function main " .
       "analyse path label1 label2 " .
       "analyse path 0x10000 0x10002 " .

       "-config proc 0 0 " .
       "config freq 999 " .
       "config threads 99 " .
       "config srcpaths /test/path/srcpath/ " .

       "clear " .
       "echo hello " .
       "pwd " .
       "help " .
       "-help history " .
       "load invalid.xe " .

       "list branches " .
       "list labels " .
       "list srclabels " .
       "list endpoints " .
       "list allsrclabels " .
       "list allendpoints " .
       "list unknowns - " .
       "list unknowns 10 " .
       "list functions " .
       "list sources " .
       "list knowns - " .
       "list knowns 10 " .
       "list constraint " .
       
       "print asm - 99 " .
       "print src - 99 " .
       "print mix - 99 " .
       "print trace - 99 " .

       "-remove branch branch1 " .
       "-remove label label1 " .
       "-remove srclabel label1 " .
       "-remove endpoint label1 " .

       "-pwd " .

       "-set loop - 99 99 " .
       "-set time test.xc:12 99 ns " .
       "-set time label1 99 cycles " .
       "-set time 0x1000 99 cycles " .
       "-set constraint expression " .
 
       "-source invalid.txt " .
       "status " .

       "print summary " .
       "print completed_loops " .
       "print completed_blocks " .
       "print completed_instructions " .
       "print unresolved_paths " .
       "print unresolved_loops " .
       "print unresolved_blocks " .
       "print unresolved_instructions " .
       "print functions - " .
       "print sources - " .
       "print path_info - 10 " .
       "print route_info 99 " .

       "unset time 0x10000 " .
       "version " .

       "history " .
       "exit > $genDir/command_line.txt 2>&1");

