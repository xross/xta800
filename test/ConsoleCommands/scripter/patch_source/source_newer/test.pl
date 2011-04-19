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
  open(LOG, ">$genDir/source_newer.log");
  copy("foo.xc", "$genDir/foo.xc") or print(LOG "Failed to copy foo.xc\n");
  copy("bar.xc", "$genDir/bar.xc") or print(LOG "Failed to copy bar.xc\n");
  close(LOG);

  chdir("$genDir");
  system("xcc -target=$target -march=$arch -g -O2 foo.xc bar.xc -o test.xe >> source_newer.log 2>&1");
  
  # touch the file to make it newer than the source
  my $atime = time;
  $atime += 100;
  my $mtime = $atime;
  utime $atime, $mtime, "foo.xc";
  chdir("..");
}

open(INFILE, "cmds.txt.in") or die "Can't read from 'cmds.txt.in'";
open(OUTFILE, ">$genDir/cmds.txt") or die "Can't write to 'cmds.txt'";

while (<INFILE>) {
  my $line = $_;
  $line =~ s/FOO/$cwd\/$genDir\/foo.xc/g;
  $line =~ s/BAR/$cwd\/$genDir\/bar.xc/g;
  print OUTFILE "$line";
}
close(INFILE);
close(OUTFILE);

system("@ARGV load $genDir/test.xe " .
       "source $genDir/cmds.txt > $genDir/source_newer.txt 2>&1");

open(LOG, ">>$genDir/source_newer.txt");
copy("bar.xc", "bar.xc.bak.expect") or print(LOG "Failed to create bar.xc expect copy\n");
close(LOG);