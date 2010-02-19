#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;
use Cwd;

my $cwd = getcwd();
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  open(LOG, ">$genDir/single_file.log");
  copy("test.xc", "$genDir/test.xc") or print(LOG "Failed to copy source\n");
  close(LOG);

  chdir("$genDir");
  system("xcc -target=$target -march=$arch -g test.xc -o test.xe >> single_file.log 2>&1");
  chdir("..");
}

open(INFILE, "cmds.txt.in") or die "Can't read from 'cmds.txt.in'";
open(OUTFILE, ">$genDir/cmds.txt") or die "Can't write to 'cmds.txt'";

while (<INFILE>) {
  my $line = $_;
  $line =~ s/SOURCE/$cwd\/$genDir\/test.xc/g;
  print OUTFILE "$line";
}
close(INFILE);
close(OUTFILE);

system("@ARGV load $genDir/test.xe " .
       "source $genDir/cmds.txt > $genDir/single_file.txt 2>&1");

open(LOG, ">>$genDir/single_file.txt");
copy("test.xc", "test.xc.bak.expect") or print(LOG "Failed to create expect copy\n");
close(LOG);