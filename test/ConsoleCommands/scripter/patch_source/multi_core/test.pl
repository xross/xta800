#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  open(LOG, ">$genDir/test.log");
  copy("test.xc", "$genDir/test.xc") or print(LOG "Failed to copy source\n");
  close(LOG);

  chdir("$genDir");
  system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> test.log 2>&1");
  chdir("..");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/test.txt 2>&1");

