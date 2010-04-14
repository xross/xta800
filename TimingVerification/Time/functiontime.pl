#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;
use POSIX qw(ceil floor);

sub atoi {
  my $t = 0;
  foreach my $d (split(//, shift())) {
    $t = $t * 10 + $d;
  }
  return $t;
}

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch test.xc -O1 -o $genDir/test.xe >  $genDir/$suite.log 2>&1");
    system("xsim -t --enable-fnop-tracing $genDir/test.xe >  $suite.trace ");
}

unlink("$suite.log.expect");
unlink("$suite.txt.expect");

open(LOG, ">$suite.log.expect") or die "Can't open input file";
close(LOG);

open(INFILE, "$suite.trace") or die "Can't open input file";
open(OUTFILE, ">$suite.txt.expect") or die "Can't open file to write";

my $xsimTime = $_;
my $xsimStartCycles = 0;
my $xsimEndCycles = 0;
my $foundFunction = 0;

my $xsimNumInstructions = 0;
my $xsimNumFNOPs = 0;


$foundFunction = 0;
while (my $line = <INFILE>) {
    if($line =~ m/test/ and $foundFunction == 0){
        $foundFunction = 1;  
        if($line =~ m/ @(.*)/) {
            $foundFunction = 1;
            $xsimStartCycles = atoi($1);
        }
    }    
    if($foundFunction == 1){
        if($line =~ m/FNOP/){
            $xsimNumFNOPs = $xsimNumFNOPs + 1;
        } else {
            if($line =~ m/ P A/) {
                #do nothing
            } else {
                $xsimNumInstructions = $xsimNumInstructions + 1;
            }
            
        }
    }
    if($line =~ m/test/ and $line =~ m/retsp/){
        if($line =~ m/ @(.*)/) {
            $xsimEndCycles = atoi($1)+4;
        }
        $foundFunction = 2; 
    }    
}
$xsimTime = (($xsimEndCycles - $xsimStartCycles) *10 ) /4;
if ($xsimTime >= 1000000.0) {
  $xsimTime = $xsimTime / 1000000.0;
} elsif ($xsimTime >= 1000.0) {
  $xsimTime = $xsimTime / 1000.0;
}

my $s = %_;
$s = sprintf("Time: %.1f\nInstructions: %d\nFNOPs acceptable: TRUE\n", $xsimTime, $xsimNumInstructions);
print OUTFILE $s;

close(INFILE);
close(OUTFILE);



system("@ARGV load $genDir/test.xe " .
        "-config threads stdcore[0] 4 " .
        "-analyse function test " .
        "-print nodeinfo - - " .
        "-exit > $genDir/$suite.in 2>&1");

open(INFILE, "$genDir/$suite.in") or die "Can not open input file";
open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open file to write";
$foundFunction = 0;

my $xtaTotalTimeTaken = 0;
my $xtaNumInstructions = 0;
my $xtaNumFnops = 0;
my $scale = 1;

while(<INFILE>) {
    my $line = $_;
    if($line =~ m/Time taken:       (.*) ([mun]s) \//) {
        $xtaTotalTimeTaken = $1;
        if ($2 eq "ms") {
          $scale = 1000000.0;
        } elsif ($2 eq "us") {
          $scale = 1000.0;
        }
    }
    if($line =~ m/Num Instructions: (.*) \//) {
        $xtaNumInstructions = $1;
    }
    if($line =~ m/Num Fnops:        (.*) \//) {
        $xtaNumFnops = $1;
    }
}
my $t = %_;
my $fnopsWithinAcceptableBounds = "";

if($xtaNumFnops >= $xsimNumFNOPs and $xtaNumFnops <= $xsimNumFNOPs*2 ){
    $fnopsWithinAcceptableBounds = "TRUE";
} else {
    $fnopsWithinAcceptableBounds = "FALSE xta: " . $xtaNumFnops . " xsim: " . $xsimNumFNOPs;
}

my $fnopAdjustedTime = $xtaTotalTimeTaken - ((($xtaNumFnops- $xsimNumFNOPs) * 10 ) / $scale);

# Do this because printf("%.1f", 1.95); produces different results on different machines/versions
if ($fnopAdjustedTime == 1.95) {
  $fnopAdjustedTime = 2.0;
}

$t = sprintf("Time: %.1f\nInstructions: %d\nFNOPs acceptable: %s\n", $fnopAdjustedTime, $xtaNumInstructions, $fnopsWithinAcceptableBounds);
print OUTFILE $t;

close(INFILE);
close(OUTFILE);




