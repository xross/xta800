#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/test.log 2>&1");
}

my $message = "RETURN CODE WRONG";

# Expect to get 1 since warnings should be treated as errors
my $retval = system("@ARGV load $genDir/test.xe " .
       "config Terror off " .
       "config Ewarning on " .
       "help config " .
       "status " .
       "config Ewarning off " .
       "status " .
       "config Ewarning on " .
       "analyze function nonexistant " .
       "-exit > $genDir/test.txt 2>&1");

if (($retval >> 8) == 0) {
  $message = "RETURN CODE OK";
}

open(FILE, ">> $genDir/test.txt");
print(FILE "$message\n");
close(FILE);

