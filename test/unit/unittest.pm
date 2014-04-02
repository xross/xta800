#!/usr/bin/perl

package unittest;

use strict;
use warnings;
use Env;

my $infrLibsJava = "$ENV{'XMOS_ROOT'}/infr_libs_java";
my $separator = ($^O eq "MSWin32") ? ";" : ":";

sub getJar($) {
    my ($name) = @_;
    my @jars = <$infrLibsJava/export/jars/${name}*.jar>;
    if (scalar(@jars) != 1) {
        die "Cannot find a unique jar for: $name";
    }
    return $jars[0] . "$separator";  
}

sub getClassPath {
    return getJar("junit") .
           getJar("commons-io") .
           getJar("commons-lang") .
           getJar("jython") .
           getJar("commons-collections") .
           getJar("instructiondecoder");
}


