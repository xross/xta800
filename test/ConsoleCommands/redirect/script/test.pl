#!/usr/bin/perl
use strict;
use warnings;

use lib "..";
use redirect;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/script.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/script.txt 2>&1");

redirect::appendOuputFile($genDir, "output.txt");
