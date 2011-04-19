#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/script.log 2>&1");
}

my $cmds = "source cmds.txt " .
           "history " .
           "exit > $genDir/script.txt 2>&1";

if (join("", @ARGV) =~ m/emma\.jar/) {
  my $xmosRoot = $ENV{'XMOS_ROOT'};
  system("java -DConsole=Basic -cp $xmosRoot/infr_libs_java/com.xmos.libs/jars/emma.jar emma run -ix -org.* -quiet -raw -outfile ./coveragedata  -jar \$INSTALLS_DIR/xta.jar $cmds");
} else {
  my $installsDir = $ENV{'INSTALLS_DIR'};
  system("java -DConsole=Basic -jar $installsDir/xta.jar $cmds");
}
