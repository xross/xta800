#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xmake>  $genDir/AesAssembly.log>&1");
system("mv  aes_test_assembly.xe $genDir/test.xe >  $genDir/AesAssembly.log>&1");
}

system("@ARGV load $genDir/test.xe " .
        "function main " .
        "exit > $genDir/AesAssembly.txt 2>&1");

