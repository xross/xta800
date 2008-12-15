#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "printstrln";
my $function = "main";

if (!$noBuild) {
       system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/$directory.log 2>&1");
}

my $filename = "trace";
my $compareFnnops = 0;

system("@ARGV load $genDir/test.xe " .
        "mode DYNAMIC " .
        "function $function " .
        "summary COMPLETE_INSTRUCTIONS " .
        "exit > $genDir/$directory.txt 2>&1");

system("simulator -c config.txt -t $genDir/test.xe > trace.out");

open(INFILE, "$filename.out") or die "Can't open input file";
open(OUTFILE, ">$filename.parsed") or die "Can't open file to write";

my $foundFunction = $_;
$foundFunction = 0;
while (<INFILE>) {
        my $line = $_;
        $line =~ s/hello//;
        if($line =~ m/$function/){
                if($line =~ m/$function/ && $line =~ m/retsp/){
                        $foundFunction = 2;
                } else {
                        $foundFunction = 1;
                }   
        }     
        $line =~ s/(.)+-000/0x/;
        $line =~ s/ (.)+//;
        if($line !~ m/0x[0-9a-fA-F]{5}/ and $line =~ /0\@0\@0/){
                if($compareFnnops == 1){
                        $line = "FNOP\n";
                } else {
                        $line = "";
                }
        } 
        if($line eq "\n"){
                next;
        }
        
        if($foundFunction==1){
        
                print OUTFILE "$line";
        }
        if($foundFunction==2){
                print OUTFILE "$line";
                $foundFunction = 0;
        }
}
close(INFILE);
close(OUTFILE);
open(INFILE, "$genDir/$directory.txt") or die "Can't open input file";
open(OUTFILE, ">$directory.parsed") or die "Can't open file to write";

while (<INFILE>) {
        my $line = $_;
        if($line =~ m/0x[0-9a-fA-F]{5}/ ){
        
                $line =~ s/ (.)+\) //;
                
                if($line =~ m/FNOP/ ){
                        if($compareFnnops == 1){
                                $line = "FNOP\n";
                                print OUTFILE "$line";
                        }
                } else {
                        $line =~ s/ (.)+//;
                        print OUTFILE "$line";
                }
        }       
}
close(INFILE);
close(OUTFILE);
system("rm $filename.out");
system("rm $genDir/$directory.txt");
system("mv $directory.parsed $genDir/$directory.txt");
system("mv $filename.parsed $directory.txt.expect");