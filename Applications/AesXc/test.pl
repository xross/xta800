#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake>  $genDir/AesXc.log>&1");
system("mv  aes_test_xc.xe $genDir/test.xe >  $genDir/AesXc.log>&1");
}

system("@ARGV load $genDir/test.xe " .
        "function _Encrypt " .
        "summary COMPLETE_INSTRUCTIONS " .
        "exit > $genDir/AesXc.txt 2>&1");

