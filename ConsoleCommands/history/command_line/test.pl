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

       "-add branch 0x10000 0x10000 " .

       "-analyse function main " .
       "-analyse path label1 label2 " .
       "-analyse path 0x10000 0x10002 " .

       "-config proc 0 0 " .
       "-config freq 999 " .
       "-config threads 99 " .
       "-config srcpaths /test/path/srcpath/ " .

       "-clear " .
       "-echo hello " .
       "-pwd " .
       "-help " .
       "-help history " .
       "-load invalid.xe " .

       "-list branches " .
       "-list labels " .
       "-list srclabels " .
       "-list endpoints " .
       "-list allsrclabels " .
       "-list allendpoints " .
       "-list functions " .
       "-list sources " .
       "-list exclusions " .
       "-list unknowns - " .
       "-list knowns - " .

       "-print asm - " .
       "-print src - " .
       "-print mix - " .
       "-print trace - " .
       "-print structure - " .
       "-print summary " .
       "-print functions - " .
       "-print sources - " .
       "-print node_info - 0 " .
       "-print route_info 0 " .

       "-remove branch branch1 " .

       "-set loop - a 99 " .
       "-set time - test.xc:12 99 ns " .
       "-set time - label1 99 cycles " .
       "-set time - 0x1000 99 cycles " .
 
       "-source invalid.txt " .
       "-status " .
       "-version " .

       "-history " .

       "-exit > $genDir/command_line.txt 2>&1");

       
