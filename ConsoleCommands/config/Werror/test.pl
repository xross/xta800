#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/Werror.log 2>&1");
}

my $message = "RETURN CODE WRONG";

# Expect to get 1 since warnings should be treated as errors
my $retval = system("@ARGV load $genDir/test.xe " .
       "config Werror " .
       "status " .
       "analyse function main " .
       "-set required - 10000 ns " .
       "print summary " .
       "-exit > $genDir/Werror.txt 2>&1");

if (($retval >> 8) == 1) {
  $message = "RETURN CODE OK";
}

open(FILE, ">> $genDir/Werror.txt");
print(FILE "$message\n");
close(FILE);

