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

my $maxdepth = 6;

my $count =0;

sub output_seq
{
  my $string = shift;
  print OUTFILE $string."\n";
  print OUTFILE "  bu empty$count\n";
  print OUTFILE "  empty$count:\n\n";
  $count = $count+1;
}


sub seq_inst
{
  my $string = shift;
  my $depth = shift;
  long_load($string, $depth);
  short_load($string, $depth);
  long_nonload($string, $depth);
  short_nonload($string, $depth);
}

sub long_load
{
  my $string = shift;
  my $depth = shift;
  $depth = $depth + 1;
  $string = $string."  ldw r0, cp[64]\n";
  if($depth == $maxdepth){
        output_seq($string);
        return;
  }
  seq_inst($string, $depth);
}
sub short_load
{
  my $string = shift;
  my $depth = shift;
  $depth = $depth + 1;
  $string = $string . "  ldw r0, cp[0]\n";
  if($depth == $maxdepth){
        output_seq($string);
        return;
  }
  seq_inst($string, $depth);
}
sub long_nonload
{
  my $string = shift;
  my $depth = shift;
  $depth = $depth + 1;
  $string = $string . "  xor r0, r0, r0\n";
  if($depth == $maxdepth){
        output_seq($string);
        return;
  }
  seq_inst($string, $depth);
}
sub short_nonload
{
  my $string = shift;
  my $depth = shift;
  $depth = $depth + 1;
  $string = $string . "  add r0, r0, r0\n";
  if($depth == $maxdepth){
        output_seq($string);
        return;
  }
  seq_inst($string, $depth);
}
my $depth =0;
my $string = "";


open(OUTFILE, ">test.s") or die "Can't open file to write";
print OUTFILE   ".text \n" .
                "data:\n" .
                ".word 0\n" .
                ".align 2\n" .
                ".globl _start\n" .
                "_start:\n" .
                "  bl main\n" .
                ".type main, \@function\n" .
                ".globl main\n" .
                "main:\n" .
                "  entsp 0\n" .
                "  # initialise data pointer\n" .
                "  ldap r11, data\n" . 
                "  set cp, r11\n"; 
                output_seq($string);
seq_inst($string, $depth);

print OUTFILE "  retsp 0\n";          
                
                
                
if (!$noBuild) {
        system("xcc -march=$arch -nostartfiles test.s -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}






system("@ARGV load $genDir/test.xe exit > $genDir/$suite.txt 2>&1");

