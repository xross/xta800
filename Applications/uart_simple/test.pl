#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -fxta-info -target=$target -march=$arch -g Uart.xc UartReceive.xc UartTransmit.xc -o $genDir/test.xe > $genDir/uart_simple.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "> $genDir/uart_simple.txt 2>&1");

