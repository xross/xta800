#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

my $pwd = $ENV{PWD};

if (!$noBuild) {
  open(LOG, ">$genDir/paths_windows.log");
  copy("test.xe", "$genDir/test.xe") or print(LOG "Failed to copy\n");
  close(LOG);
}

system("@ARGV " .
       "config srcpaths $pwd " .
       "load $genDir/test.xe " .
       "list sources " .
       "analyse function main " .
       "print src 0 " .
       "exit  > $genDir/paths_windows.txt 2>&1");

