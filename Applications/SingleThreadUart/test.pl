#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g swc_uart_client.xc swc_uart_server.xc uart_example.xc -o $genDir/test.xe > $genDir/SingleThreadUart.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "mode DYNAMIC" .
        "exclude 0x107a8" .
        "exclude 0x106d8" .
        "exclude 0x10482" .
        "path 0x106da 0x10792" .
        "exit > $genDir/SingleThreadUart.txt 2>&1");

