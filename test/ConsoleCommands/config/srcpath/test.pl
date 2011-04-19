#!/usr/bin/perl
use strict;
use warnings;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd     = getcwd();

if (!$noBuild) {
system("cp a.xc test.xc");
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/srcpath.log 2>&1");
system("mv test.xc ./up/test.xc");
}
system("@ARGV " .
       "load $genDir/test.xe " .
       "list sources " .
       "config srcpaths $cwd/up " . 
       "status " .
       "load $genDir/test.xe " .
       "list sources " .
       "exit > $genDir/srcpath.txt 2>&1");
