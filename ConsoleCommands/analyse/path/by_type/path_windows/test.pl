#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

my $pwd = $ENV{PWD};

if (!$noBuild) {
  open(LOG, ">$genDir/path_windows.log");
  copy("test.xe", "$genDir/test.xe") or print(LOG "Failed to copy\n");
  close(LOG);
}

system("@ARGV " .
       "-load $genDir/test.xe " .
       "-analyse path LABEL:main LABEL:add " .
       "-print structure - " .
       "-analyse path ENDPOINT:main ENDPOINT:add " .
       "-print structure - " .
       "-analyse path ENDPOINT:C:/peter/git/software/verif_tests_sw/Tools/xta/ConsoleCommands/analyse/path/by_type/path_windows/test.xc:19 " .
         "ENDPOINT:C:/peter/git/software/verif_tests_sw/Tools/xta/ConsoleCommands/analyse/path/by_type/path_windows/test.xc:23 " .
       "-print structure - " .
       "-exit  > $genDir/path_windows.txt 2>&1");
