#!/usr/bin/perl
use strict;
use warnings;
use Cwd;

my $cwd = getcwd();
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/rename.log 2>&1");
}

open(INFILE, "cmds.txt.in") or die "Can't read from 'cmds.txt.in'";
open(OUTFILE, ">cmds.txt") or die "Can't write to 'cmds.txt'";

while (<INFILE>) {
  my $line = $_;
  $line =~ s/SOURCE/$cwd\/test.xc/g;
  print OUTFILE "$line";
}
close(INFILE);
close(OUTFILE);

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt > $genDir/rename.txt 2>&1");

