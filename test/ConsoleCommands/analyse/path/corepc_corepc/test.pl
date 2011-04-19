#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

sub getPc($$) {
    my ($fileName, $grepString) = @_;

    my $fp;
    open($fp, "<$fileName") 
      or die("Error: Cannot open file: $fileName");
    my @lines = <$fp>;
    close($fp);

    foreach my $line (@lines) {
        if ($line =~ m/$grepString/) {
            my @fields = split(/\s+/, $line);
            return $fields[0]; 
        }
    }
    die("Error: Cannot find string: $grepString in file: $fileName");
}

if (!$noBuild) {
    system("xcc -target=$target -march=$arch -g test.xc func.s -o $genDir/test.xe > $genDir/test.log 2>&1");
}

system("xobjdump -t $genDir/test.xe > $genDir/test.symbols 2>&1");
my $startPc = getPc("$genDir/test.symbols", "INPUT_1");
my $endPc   = getPc("$genDir/test.symbols", "INPUT_2");

system("@ARGV load $genDir/test.xe " .
       "analyze path stdcore[0]::$startPc stdcore[0]::$endPc " .
       "print summary " .
       "-exit > $genDir/test.txt 2>&1");

