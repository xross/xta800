#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;
use Cwd;
use Fcntl ':mode';

my $cwd = getcwd();
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  open(LOG, ">$genDir/source_read_only.log");
  copy("foo.xc", "$genDir/foo.xc") or print(LOG "Failed to copy foo.xc\n");
  copy("bar.xc", "$genDir/bar.xc") or print(LOG "Failed to copy bar.xc\n");
 
  # Remove write permissions
  chmod 0444, "$genDir/foo.xc";
  close(LOG);

  chdir("$genDir");
  system("xcc -target=$target -march=$arch -g -O2 foo.xc bar.xc -o test.xe >> source_read_only.log 2>&1");
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
       "source $genDir/cmds.txt > $genDir/source_read_only.txt 2>&1");

open(LOG, ">>$genDir/source_read_only.txt");
copy("bar.xc", "bar.xc.bak.expect") or print(LOG "Failed to create bar.xc expect copy\n");
close(LOG);