#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
chdir("add") or die ("Could not chdir to directory add");
system("xcc -march=$arch -g -c test.xc -o ../$genDir/add.o > ../$genDir/different_compilation_directories.log 2>&1");
chdir("../") or die ("Could not chdir to directory ../");

chdir("sub") or die ("Could not chdir to directory sub");
system("xcc -march=$arch -g -c test.xc -o ../$genDir/sub.o >> ../$genDir/different_compilation_directories.log 2>&1");
chdir("../") or die ("Could not chdir to directory ../");

system("xcc -march=$arch -g -c test.xc -o $genDir/test.o >> $genDir/different_compilation_directories.log 2>&1");
system("xcc -target=$target -march=$arch -g $genDir/test.o $genDir/add.o $genDir/sub.o -o $genDir/test.xe >> $genDir/different_compilation_directories.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "list sources " .
       "analyse function main " .
       "print src - 0 " .
       "exit  > $genDir/different_compilation_directories.txt 2>&1");

