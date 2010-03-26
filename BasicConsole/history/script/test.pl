#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/script.log 2>&1");
}
system("java -DConsole=Basic -jar \$INSTALLS_DIR/xta.jar " .
       "source cmds.txt " .
       "history " .
       "exit > $genDir/script.txt 2>&1");

