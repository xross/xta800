#!/usr/bin/perl
use strict;
use warnings;
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("cp test.xc $genDir/test.xc");
        system("xcc -target=$target -march=$arch -g $genDir/test.xc -o $genDir/test.xe > $genDir/test70.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit > $genDir/test70.txt 2>&1");
my $fail = 0;
open(INFILE, "$genDir/test70.txt") or die "Can't open input file";
while (<INFILE>) {
    my $line = $_;
    if($line =~ m/AssertionError/ or $line =~ m/java.lang/){
        $fail = 1;
    }        
}
close(INFILE);
if ($fail) {
	open(LOG, ">>$genDir/test70.log");
	print LOG "FAIL";
	close(LOG);
}
