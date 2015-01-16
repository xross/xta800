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
    system("xcc -target=$target -march=$arch test.xc -O1 -o $genDir/test.xe >  $genDir/$suite.log 2>&1");
    system("xsim --max-cycles 10000 -t --enable-fnop-tracing $genDir/test.xe >  $suite.trace ");
}

unlink("$suite.log.expect");
unlink("$suite.txt.expect");

open(LOG, ">$suite.log.expect") or die "Can't open input file";
close(LOG);

open(INFILE, "$suite.trace") or die "Can't open input file";
open(OUTFILE, ">$suite.txt.expect") or die "Can't open file to write";

my $foundFunction = $_;

$foundFunction = 0;
while (<INFILE>) {
    my $line = $_;
    my $temp = $line;
    if($temp =~ m/test/ and $foundFunction == 0){
        $foundFunction = 1;  
    }    
   if($foundFunction == 1){
        if($line =~ m/FNOP/){
            $line =~ s/(.)+F/F/;
            $line =~ s/ \@(.)+//;
            print OUTFILE "$line";
        } else {
            $line =~ s/(.)+-000/0x/;
            $line =~ s/ (.)+//;
            print OUTFILE "$line";
        }
    }
    if($temp =~ m/test/ and $temp =~ m/retsp/){
        $foundFunction = 2; 
    }    
}

close(INFILE);
close(OUTFILE);

system("@ARGV load $genDir/test.xe " .
        "analyse function test " .
        "print trace - " .
        "-exit > $genDir/$suite.in 2>&1");

open(INFILE, "$genDir/$suite.in") or die "Can't open input file";
open(OUTFILE, ">$genDir/$suite.txt") or die "Can't open file to write";

while (<INFILE>) {
    my $line = $_;
    if($line =~ m/0x[0-9a-fA-F]{5}/ ){
  
        # Removes leading whitespace.
        $line =~ s/^\s+//;

        # Gets the pc from the trace. 
        my @lineArray = split('\s+', $line);
        my $pc = $lineArray[3]; 
                
        if($line =~ m/FNOP/ ){
           
                $line = "FNOP\n";
                print OUTFILE "$line";

        } else {
            print OUTFILE "$pc\n";
        }
    }       
}
close(INFILE);
close(OUTFILE);


