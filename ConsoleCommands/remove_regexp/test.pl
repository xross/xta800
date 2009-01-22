#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/remove_regexp.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "echo required " .
       "add required done " .
       "add required case_1 " .
       "add required case_2 " .
       "add required case_3 " .
       "list required " .
       "remove required case.* " .
       "list required " .
       "add required case_1 " .
       "add required case_2 " .
       "add required case_3 " .
       "list required " .
       "remove required .* " .
       "list required " .

       "echo prohibited " .
       "add prohibited done " .
       "add prohibited case_1 " .
       "add prohibited case_2 " .
       "add prohibited case_3 " .
       "list prohibited " .
       "remove prohibited case.* " .
       "list prohibited " .
       "add prohibited case_1 " .
       "add prohibited case_2 " .
       "add prohibited case_3 " .
       "list prohibited " .
       "remove prohibited .* " .
       "list prohibited " .

       "echo filter " .
       "add filter done " .
       "add filter case_1 " .
       "add filter case_2 " .
       "add filter case_3 " .
       "list filter " .
       "remove filter case.* " .
       "list filter " .
       "add filter case_1 " .
       "add filter case_2 " .
       "add filter case_3 " .
       "list filter " .
       "remove filter .* " .
       "list filter " .

       "exit > $genDir/remove_regexp.txt 2>&1");
