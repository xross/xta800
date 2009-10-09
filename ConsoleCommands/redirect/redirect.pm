#!/usr/bin/perl

package redirect;

use strict;
use warnings;
use File::Basename;
use Cwd;

sub appendOuputFile($$) {
  my ($genDir, $file) = @_;

  my $cwd = getcwd();
  my $suite = basename($cwd);

  open(OUTPUT, ">>$genDir/$suite.txt") 
    or die "Can't open output file";

  open(REDIRECT, "<$file") 
    or die "Can't open redirect file";

  print OUTPUT "======= $file ======\n";
  while(my $line = <REDIRECT>) {
    print OUTPUT "$line";
  }
  print OUTPUT "====================\n";
  close(REDIRECT);

  close(OUTPUT);
}

1;

