#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xmake -f Makefile > $genDir/console.log 2>&1");
  rename("tutorial_console.xe", "$genDir/tutorial_console.xe");
}
system("@ARGV load $genDir/tutorial_console.xe " .
       "source receive.sr.sample > $genDir/console.txt 2>&1");
