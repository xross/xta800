#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xmake -f Makefile > $genDir/gui.log 2>&1");
  rename("tutorial_gui.xe", "$genDir/tutorial_gui.xe");
}
system("@ARGV load $genDir/tutorial_gui.xe " .
       "analyse function resetTable " .
       "analyse function functionCalls " .
       "analyse function main " .
       "print completed_paths " .
       "exit > $genDir/gui.txt 2>&1");
