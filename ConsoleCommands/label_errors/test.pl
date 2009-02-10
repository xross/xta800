#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/label_errors.log 2>&1");
}
system("@ARGV " .
       "add label one 0x10000 " .
       "add label 0x10000 one " .
       "load $genDir/test.xe " .
       "add label 0x10000 one " .
       "list labels ". 
       "add label 0x1000 one " .
       "add label 0x10002 one " .
       "add label 0x10004 one " .
       "set time one ns 100 " .
       "list times " .
       "unset time one " .
       "add label 0x10000 0x10000 " .
       "exit > $genDir/label_errors.txt 2>&1");

