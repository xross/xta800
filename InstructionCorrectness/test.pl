#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd = getcwd();
my $suite = basename($cwd);
my $PASS = "true";
my $FAIL = "false";

open(OUTFILE, ">$suite.txt.expect.regex") or die "Can't open file to write";
print OUTFILE $PASS;
close(OUTFILE);
open(OUTFILE, ">$suite.log.expect") or die "Can't open file to write";
close(OUTFILE);

# Searches up the directory hierarchy (for a few levels) 
# looking for the first test.xc harness file it can find....
my $testHarness = "test.xc";
my $maxNumLevels = 5;
my $found = 0; 
while ($maxNumLevels > 0) {
  if (-e $testHarness) {
    $found = 1;
    last;
  }   
  $testHarness = "../$testHarness";
  --$maxNumLevels;
}

if ($found == 1) {
  if (!$noBuild) {
    system("xcc -march=$arch -g $testHarness test.s -o $genDir/test.xe > $genDir/$suite.log 2>&1");
  }

  system("@ARGV load $genDir/test.xe " .
         "analyse function main " .
         "print trace 0 " .
         "exit > temp.txt 2>&1");
  
  open(INFILE, "temp.txt") or die "Can't open input file";
  open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open file to write";
  my $success = $FAIL;
  
  while (<INFILE>) {
          my $line = $_;
          if($line =~ m/success/){
               $success = $PASS;
          }
  }
  print OUTFILE "$success";
  close(INFILE);
  close(OUTFILE);
  #system("rm temp.txt");
}


