#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "AesAssembly";

if (!$noBuild) {
    system("xmake>  $genDir/$directory.log 2>&1");
    system("mv  aes_test_assembly.xe $genDir/test.xe >  $genDir/$directory.log 2>&1");
}

my $filename = "trace";
my $compareFnnops = 0;

system("@ARGV load $genDir/test.xe " .
        "analyse function _Encrypt " .
        "print completed_instructions " .
        "-exit > $genDir/$directory.txt 2>&1");

system("xobjdump -r config.xml $genDir/test.xe > $filename.out");
system("xsim -t $genDir/test.xe > $filename.out");

open(INFILE, "$filename.out") or die "Can't open input file";
open(OUTFILE, ">$filename.parsed") or die "Can't open file to write";

my $foundFunction = $_;
$foundFunction = 0;
while (<INFILE>) {
    my $line = $_;
    if($line =~ m/_Encrypt/ and $foundFunction <2){
        if($foundFunction == 0) {
            $foundFunction = 1;
        }
        $line =~ s/(.)+-000/0x/;
        $line =~ s/ (.)+//;
        print OUTFILE "$line";
    } else  {
        if($foundFunction == 1 and $line =~ m/FNOP/ and $compareFnnops == 1){
            $line =~ s/0(.)+F/F/;
            $line =~ s/ \@(.)+//;
            print OUTFILE "$line";
        } else {
            if($line !~ m/FNOP/ and $foundFunction == 1){
                $foundFunction = 2;
            }
        }
    }        
}
close(INFILE);
close(OUTFILE);
open(INFILE, "$genDir/$directory.txt") or die "Can't open input file";
open(OUTFILE, ">$directory.parsed") or die "Can't open file to write";

while (<INFILE>) {
    my $line = $_;
    if($line =~ m/0x[0-9a-fA-F]{5}/ ){
  
        # Removes leading whitespace.
        $line =~ s/^\s+//;

        # Gets the pc from the trace. 
        my @lineArray = split('\s+', $line);
        my $pc = $lineArray[1]; 
                
        if($line =~ m/FNOP/ ){
            if($compareFnnops == 1){
                $line = "FNOP\n";
                print OUTFILE "$line";
            }
        } else {
            print OUTFILE "$pc\n";
        }
    }       
}
close(INFILE);
close(OUTFILE);
system("rm $filename.out");
system("rm $genDir/$directory.txt");
system("mv $directory.parsed $genDir/$directory.txt");
system("mv $filename.parsed $directory.txt.expect");
