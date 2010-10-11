#!/usr/bin/perl
use warnings;
use strict;

use SetupEnv;
use File::Copy;
use XmosBuildLib;
use XmosArg;
use File::Temp qw/ :mktemp  /;
use File::Path;
use Cwd;

use File::Find;

my $MAKE_FLAGS;
my $CONFIG;
my $HOST;
my $DOMAIN;
my $BUILD_NUM;
my $BIN;

my %ALIASES =
  (
    'all' => ['realclean', 'prebuild', 'build', 'install'],
  );

my %TARGETS =
  (
    'preclean' => [\&DoPreClean, "Delete generated source code\n"],
    'clean' => [\&DoClean, "Clean java tools"],
    'realclean' => [\&DoRealClean, "Delete installed java tools from installs"],
    'prebuild' => [\&DoPreBuild, "Create generated files."],
    'build' => [\&DoBuild, "Build java tools"],
    'install' => [\&DoInstall, "Install java tools"],
    'lint' => [\&DoLint, "Run Lint"]
  );

my $arguments;

sub main
{
  my $xmosArg = XmosArg::new(\@ARGV);
  SetupEnv::SetupPaths();

  my @targets =
    sort { XmosBuildLib::ByTarget($a, $b) }
      (@{ $xmosArg->GetTargets() });

  $MAKE_FLAGS = $xmosArg->GetMakeFlags();
  $CONFIG = $xmosArg->GetOption("CONFIG");
  $DOMAIN = $xmosArg->GetOption("DOMAIN");
  $BUILD_NUM = $xmosArg->GetOption("BUILD_NUM");
  $HOST = $xmosArg->GetOption("HOST");
  $BIN = $xmosArg->GetBinDir();

  foreach my $target (@targets) {
    DoTarget($target);
  }
  return 0;
}

sub DoTarget
{
  my $target = $_[0];
  if ($target eq "list_targets") {
    ListTargets();
  } else {
    my $targets = $ALIASES{$target};
    if (defined($targets)) {

      # Target is an alias
      foreach my $target (@$targets) {
        DoTarget($target);
      }
    } else {
      my $function = $TARGETS{$target}[0];
      if (defined($function)) {
        print(" ++ $target\n");
        &$function();
      }
    }
  }
}

sub ListTargets
{
  foreach my $target (keys(%TARGETS)) {
    print("$target\n");
  }
  foreach my $alias (keys(%ALIASES)) {
    print("$alias\n");
  }
}

sub PrintUsage
{
  my $target;
  my $alias;
  my $targets;
  my $targetTable;
  my $description;

  print("Usage:\n");
  print("   Build.pl targets\n");
  print("\n");
  print("   Available targets:\n");
  while (($target, $targetTable) = each(%TARGETS)) {
    $description = @$targetTable[1];
    print("   $target - $description\n");
  }
  while (($alias, $targets) = each(%ALIASES)) {
    $description = "does";
    foreach $target (@$targets) {
      $description = "$description \'$target\' ";
    }
    print("   $alias - $description\n");
  }
  print("   list_targets - List all targets\n");
  exit(1);
}

sub DoPreClean
{
  chdir("$TOOLS_XTA/com.xmos.xta.core/src/com/xmos/xta/core")
    or die("Failed to move to $TOOLS_XTA/src/com/xmos/xta");

  unlink("instructions/xs1bInstructionDecoder.java");
  unlink("instructions/xs1bInstructions.java");

  chdir("$BUILDPATH") or die("oops");
}

sub DoClean
{
  chdir("$TOOLS_XTA") 
    or die("Failed to find $TOOLS_XTA\n");

  system("$MAKE $MAKE_FLAGS clean");
  
  chdir("com.xmos.xta.core") 
    or die("Failed to find com.xmos.xta.core\n");
  system("ant -f ant.xml clean"); 
    die() if ($? != 0);
  chdir("../");

  chdir("$BUILDPATH") or die("oops");
}

sub DoRealClean
{
  XmosBuildLib::UnInstallFile("xta.exe");
  XmosBuildLib::UnInstallFile("xta.jar");

  chdir("$BUILDPATH") or die("oops");
  DoPreClean();
  DoClean();
}

sub DoPreBuild
{
}

sub DoBuild
{
  chdir("$TOOLS_XTA") 
    or die("Build failed to find $TOOLS_XTA\n");

  system("$MAKE $MAKE_FLAGS");

  UpdateBuildNumber();

  chdir("com.xmos.xta.core") 
    or die("Failed to find com.xmos.xta.core\n");

  if ($MAKE_FLAGS =~ m/CONFIG=Debug/) {
    system("ant -f ant.xml -Ddebug=\"true\""); 
    die() if ($? != 0);
  } else {
    system("ant -f ant.xml -Ddebug=\"false\""); 
    die() if ($? != 0);
  }

  SignOffTests();
  chdir("../");

  chdir("$BUILDPATH") or die("oops");
}

sub SignOffTests {
  find(\&d, cwd);
}

sub d {

  my $file = $File::Find::name;
 
  return unless -e $file;
  return unless $file =~ m/java$/;
  open(F, $file) or print "couldn't open $file\n" && return;
  while (my $line = <F>) {
    if ($line =~ m/HashSet/ or 
        $line =~ m/HashMap/ or 
        $line =~ m/TreeSet/ or 
        $line =~ m/TreeMap/) {

      print "Warning: $file contains $line\n";

      last;
    } 
  }
  close F;

}

sub DoInstall
{
  chmod 0755, $BIN;
  XmosBuildLib::InstallFile($DOMAIN, $BIN, "xta.exe");
  XmosBuildLib::InstallReleaseFile($DOMAIN, $BIN, "xta.exe", "bin");

  XmosBuildLib::InstallFile($DOMAIN, "export${SLASH}jars", "xta.jar");
  XmosBuildLib::InstallReleaseFile($DOMAIN, "export${SLASH}jars", "xta.jar", "bin");
}

sub DoLint
{
  if ($HOST eq "Linux") {
    chdir("$TOOLS_XTA") or die("Build failed to find $TOOLS_XTA\n");
    system("$MAKE $MAKE_FLAGS lint");
    chdir("$BUILDPATH") or die("oops");
  }
  else {
    print "Error: Lint unsupported on this platform.\n";
  }
}

sub UpdateBuildNumber
{
  if (defined($BUILD_NUM)) 
  {
    UpdateBuildNumberInFile("<com.xmos.xta.core${SLASH}version.xml");
  }
}

sub UpdateBuildNumberInFile
{
  my $file = shift;

  # Reads in the plugin.xml and patches the build number..
  open(my $infp, "+$file") 
    or die("Cannot open file: $file for reading");
  my @lines = <$infp>;

  seek $infp,0,0;
  foreach my $line (@lines)
  {
    $line =~ s/\(build ....\)/\(build $BUILD_NUM\)/g;
    print $infp $line;
  }
  close $infp;
}

main()
