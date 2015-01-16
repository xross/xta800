#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $suite   = shift(@ARGV);

open(INFILE, "$genDir/$suite.tmp") or die "Can't open input file";
open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open output file";

my $foundFunction = $_;
my $function = "main";
$foundFunction = 0;
my $linecount = $_;
$linecount =0;

while (<INFILE>) {
        my $line = $_;
        chomp($line);      
        if ($line eq "") {
            next;
        }  

        if($line =~ m/FNOP/){
            $line = "FNOP";
        } else {
            $line =~ s/^\s+//;
            my @fields = split /\s+/, $line;
            $line = $fields[3];
        }
        $linecount++;
        if($linecount==1 && $line =~ m/FNOP/){
            
        } else {
            print OUTFILE "$line\n";
        }
        
}
close(INFILE);
close(OUTFILE);
