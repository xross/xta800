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

if (!$noBuild) {
        system("xcc -march=xs1b -g test.xc test.s -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "config mode RESOLVE " .
       "function main " .
       "trace 0 " .
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
system("rm temp.txt");



