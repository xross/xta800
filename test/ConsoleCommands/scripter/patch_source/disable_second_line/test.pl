#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;
use Cwd;

my $cwd = getcwd();
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  open(LOG, ">$genDir/disable_second_line.log");
  copy("test.xc", "$genDir/test.xc") or print(LOG "Failed to copy source\n");
  close(LOG);

  chdir("$genDir");
  system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> disable_second_line.log 2>&1");
  chdir("..");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt > $genDir/disable_second_line.txt 2>&1");

open(LOG, ">>$genDir/disable_second_line.txt");
copy("test.xc", "test.xc.bak.expect") or print(LOG "Failed to create expect copy\n");
close(LOG);