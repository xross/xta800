#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);



open(INFILE, "test.xc") or die "Can't read from 'test.xc'";
open(CMDSFILE, ">$genDir/cmds.txt") or die "Can't write to '$genDir/cmds.txt'";
open(EXPECTFILE, ">if.txt.expect.regex") or die "Can't write to 'if.txt.expect.regex'";
open(MAINFILE, ">main.xc") or die "Can't write to 'main.xc'";

my $testing = 0;
my $currentFnName = "foo";
my $lineNumber = 0;

my @functions;


while (<INFILE>) {
  my $line = $_;
  $lineNumber++;

  if($line =~ "stop") {
    $testing = 0;
  }

  if($testing == 1){
    
    my $knownResult = 1;    

    if($line =~ "no_path") {
      print EXPECTFILE "### Testing line test.xc:$lineNumber ###\n";
      print EXPECTFILE "xta: csim__xs1b__default/cmds.txt:\\d+: warning: no valid paths of execution\n";
      print EXPECTFILE ".*\n";
      print EXPECTFILE "[^^^]\n";
    } elsif($line =~ "infinite") {
      print EXPECTFILE "### Testing line test.xc:$lineNumber ###\n";
    } elsif($line =~ "path") {
      print EXPECTFILE "### Testing line test.xc:$lineNumber ###\n";
      print EXPECTFILE "function\\(\\d+\\)\\s+: \\d+.\\d+ ns / \\d+.\\d+ ns $currentFnName\n";
      print EXPECTFILE ".*\n";
      print EXPECTFILE "[^^^]\n";
      print EXPECTFILE "\n";
    } else {
      $knownResult = 0; 
    }


    if( $knownResult){
      print CMDSFILE "echo \"### Testing line test.xc:$lineNumber ###\"\n";
      print CMDSFILE "remove exclusion *\n";
      print CMDSFILE "add exclusion test.xc:$lineNumber\n";
      print CMDSFILE "analyse function $currentFnName\n";
      print CMDSFILE "print structure -\n";
      print CMDSFILE "remove route *\n";
    }
  }

  if($line =~ "start") {
    my @words = split(/ /,$line);
    $currentFnName = $words[1];
    print CMDSFILE "echo \"### Testing function $currentFnName ###\"\n";
    print EXPECTFILE "### Testing function $currentFnName ###\n";
    push(@functions, $currentFnName);
    print CMDSFILE "remove exclusion *\n";
    print CMDSFILE "analyse function $currentFnName\n";
    print CMDSFILE "print structure -\n";
    print CMDSFILE "remove route *\n";

  if($line =~ "no_path") {
    print EXPECTFILE "no_path\n";
  }
  if($line =~ "infinite") {
    print EXPECTFILE "function\\(\\d+\\)\\s+: Infinity / \\d+.\\d+ ns $currentFnName\n";
    print EXPECTFILE ".*\n";
    print EXPECTFILE "[^^^]\n";
    print EXPECTFILE "\n";
  }
  if($line =~ "path") {
    print EXPECTFILE "path\n";
  }
    $testing = 1;
  }

}
foreach (@functions) {
  my $fn = $_;
  print MAINFILE "void $fn(int);\n";
}
print MAINFILE "int main(){;\n";
foreach (@functions) {
  my $fn = $_;
  print MAINFILE "  $fn(0);\n";
}

print MAINFILE "  return;\n;}\n";

if (!$noBuild) {
  system("xcc -target=$target -march=$arch test.xc -O3 -c -o $genDir/test.o");
  system("xcc -target=$target -march=$arch main.xc -O0 -c -o $genDir/main.o");
  system("xcc -target=$target -march=$arch noReturn.xc -O3 -c -o $genDir/noReturn.o");
  system("xcc -target=$target -march=$arch -O0 $genDir/test.o $genDir/main.o $genDir/noReturn.o -o $genDir/test.xe");
}

system("@ARGV -load $genDir/test.xe -source $genDir/cmds.txt -exit > $genDir/if.txt 2>&1");
