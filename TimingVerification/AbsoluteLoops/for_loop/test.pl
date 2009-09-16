#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use Cwd;

use lib "..";
use absolutetime;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch test.xc -O1 -o $genDir/test.xe >  $genDir/$suite.log 2>&1");
    system("xsim -t -f $genDir/test.xe >  $suite.trace ");
}

system("@ARGV load $genDir/test.xe " .
        "-config threads 4 " .
        "-analyse function test " .
        "-set loop - inner_loop 18 " .
        "-set loopscope - inner_loop a " .
        "-print node_info - - " .
        "-exit > $genDir/$suite.in 2>&1");

absolutetime::createFiles($suite, $genDir);



