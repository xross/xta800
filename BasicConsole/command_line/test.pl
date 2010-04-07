#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/command_line.log 2>&1");
}

my $cmds = 
       "-add branch 0x10000 0x10000 " .
       "-analyze function main " .
       "-set functiontime - main 8801 ns " .
       "-analyze path label1 label2 " .
       "-analyze path 0x10000 0x10002 " .
       "-set required - 10000 ns " .
       "-set pathtime - 0x10000 0x10002 5003 us " .
       "-add functiontime main 10001 ns " .
       "-add pathtime 0x10000 0x10002 10001 ns " .
       "-config freq 0 999 " .
       "-config threads 0 0 99 " .
       "-config case BEST " .
       "-config case WORST " .
       "-config srcpaths /test/path/srcpath/ " .
       "-clear " .
       "-echo hello " .
       "-pwd " .
       "-help " .
       "-help history " .
       "-load invalid.xe " .
       "-list branches " .
       "-list labels " .
       "-list srclabels " .
       "-list endpoints " .
       "-list allsrclabels " .
       "-list allendpoints " .
       "-list functions " .
       "-list sources " .
       "-list exclusions " .
       "-list unknowns - " .
       "-list knowns - " .
       "-print asm - " .
       "-print src - " .
       "-print trace - " .
       "-print structure - " .
       "-print summary " .
       "-print nodeinfo - 0 " .
       "-print routeinfo 0 " .
       "-remove branch branch1 branch1 " .
       "-set loop - a 99 " .
       "-set instructiontime - test.xc:12 9901 ns " .
       "-set instructiontime - label1 99 cycles " .
       "-set instructiontime - 0x1000 50000 cycles " .
       "-add instructiontime 0x10000 50008 us " .
       "-source invalid.txt " .
       "-status " .
       "-version " .
       "-history " .
       "-exit > $genDir/command_line.txt 2>&1";

if (join("", @ARGV) =~ m/emma\.jar/) {
  system("java -DConsole=Basic -cp \$XMOS_ROOT/infr_libs_java/com.xmos.libs/jars/emma.jar emma run -ix -org.* -quiet -raw -outfile ./coveragedata  -jar \$INSTALLS_DIR/xta.jar $cmds");
} else {
  system("java -DConsole=Basic -jar \$INSTALLS_DIR/xta.jar $cmds");
}
      
