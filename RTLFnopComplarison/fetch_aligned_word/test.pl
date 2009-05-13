#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
        system("xcc -target=$target -march=$arch -nostartfiles test.S -I ../ -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}

system("@ARGV load $genDir/test.xe analyse function main print trace - 0 exit > $genDir/$suite.tmp 2>&1");

open(INFILE, "$genDir/$suite.tmp") or die "Can't open input file";
open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open file to write";

my $foundFunction = $_;
my $function = "main";
$foundFunction = 0;
my $linecount = $_;
$linecount =0;

while (<INFILE>) {
        my $line = $_;
        if($line =~ m/FNOP/){
            $line = "FNOP";
        } else {
            $line =~ s/^\s+//;
            my @fields = split /\s+/, $line;
            $line = $fields[2];
        }
        $linecount++;
        if($linecount==1 && $line =~ m/FNOP/){
            
        } else {
            print OUTFILE "$line\n";
        }
        
}
close(INFILE);
close(OUTFILE);
