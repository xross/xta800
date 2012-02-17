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
    system("xcc -target=$target -march=$arch test.xc -O0 -o $genDir/test.xe >  $genDir/$suite.log 2>&1");
    system("xsim --max-cycles 100000 -t --enable-fnop-tracing $genDir/test.xe >  $suite.trace ");
}

system("@ARGV load $genDir/test.xe " .
        "-config threads stdcore[0] 4 " .
        "-analyse function test " .
        "-set loop - outer_loop 18 " .
        "-set loopscope - outer_loop a " .
        "-set loop - inner_loop 108 " .
        "-set loopscope - inner_loop a " .
        "-print nodeinfo - - " .
        "-exit > $genDir/$suite.in 2>&1");

absolutetime::createFiles($suite, $genDir);

