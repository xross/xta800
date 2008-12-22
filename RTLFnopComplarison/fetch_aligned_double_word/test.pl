#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
        system("xcc -march=$arch -nostartfiles test.S -I ../ -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}

system("@ARGV load $genDir/test.xe analyse function main print trace 0 exit > $suite.tmp 2>&1");

open(INFILE, "$suite.tmp") or die "Can't open input file";
open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open file to write";

my $foundFunction = $_;
my $function = "main";
$foundFunction = 0;
while (<INFILE>) {
        my $line = $_;
        if($line =~ m/FNOP/){
                $line = "FNOP\n";
        }
        $line =~ s/(.)+-000/0x/;
        $line =~ s/  //;
        $line =~ s/ (.)+//;
        print OUTFILE "$line";
}
close(INFILE);
close(OUTFILE);