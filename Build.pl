#!/usr/bin/perl
use warnings;
use strict;

use SetupEnv;
use File::Copy;
use XmosBuildLib;
use XmosArg;

my $MAKE_FLAGS;
my $CONFIG;
my $HOST;
my $DOMAIN;
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
    'depend' => [\&DoDepend, "Updates the dependency files. Can only be run in Linux/Cygwin."],
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
  chdir("$TOOLS_XTA${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta") 
    or die("Failed to move to $TOOLS_XTA${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta");

  unlink("RevaInstructionDecoder.java");
  unlink("RevaInstructions.java");
  unlink("RevbInstructionDecoder.java");
  unlink("RevbInstructions.java");

  chdir("$BUILDPATH") or die("oops");
}

sub DoClean
{
  chdir("$TOOLS_XTA") or die("Failed to find $TOOLS_XTA\n");
  system("$MAKE $MAKE_FLAGS clean");
  system("ant clean");
    die() if ($? != 0);
  chdir("$BUILDPATH") or die("oops");
}

sub DoRealClean
{
  XmosBuildLib::UnInstallFile("$DOMAIN", "xta.jar");
  XmosBuildLib::UnInstallFile("$DOMAIN", "xiss.jar");

  chdir("$BUILDPATH") or die("oops");
  DoPreClean();
  DoClean();
}

sub DoPreBuild
{
  print(" + Generating files from Databases\n");

  CreateXtaFiles("reva", "Reva");
  CreateXtaFiles("revb", "Revb");

  chdir("$BUILDPATH") or die("oops");
}

sub DoBuild
{
  chdir("$TOOLS_XTA") or die("Build failed to find $TOOLS_XTA\n");
  system("$MAKE $MAKE_FLAGS");
  system("ant");
  die() if ($? != 0);
  chdir("$BUILDPATH") or die("oops");
}

sub DoInstall
{
  XmosBuildLib::InstallFile("$DOMAIN", "${BIN}", "xta.exe");
  #XmosBuildLib::InstallReleaseFile("$DOMAIN", "${BIN}", "xta.exe", "bin");

  XmosBuildLib::InstallFile("$DOMAIN", "lib", "xta.jar");
  #XmosBuildLib::InstallReleaseFile("$DOMAIN", "lib", "xta.jar", "bin");

  XmosBuildLib::InstallFile("$DOMAIN", "lib", "xtaswing.jar");
  #XmosBuildLib::InstallReleaseFile("$DOMAIN", "lib", "xtaswing.jar", "bin");

  XmosBuildLib::InstallFile("$DOMAIN", "lib", "xtarcp.jar");
  #XmosBuildLib::InstallReleaseFile("$DOMAIN", "lib", "xtarcp.jar", "bin");

  chdir("$BUILDPATH") or die("oops");
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

sub CreateXtaFiles
{
  my $versionDir = shift;
  my $version = shift;
   
  chdir("$ARCH_DBS_XML${SLASH}xs1_$versionDir") or die("Generation failed to find $version databases\n");
  system("$INFR_TOOLS_CPP${SLASH}${BIN}${SLASH}XtaGeneration.exe XSystemDb${SLASH}XS1-040.xml $version");

  print(" + Installing $version generated xta files\n");
  rename("XtaInstructionDecoder.java", "$TOOLS_XTA${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta${SLASH}engine${SLASH}${version}XtaInstructionDecoder.java") 
    or die("Failed to move XtaInstructionDecoder.java\n");
  rename("XtaInstructions.java", "$TOOLS_XTA${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta${SLASH}engine${SLASH}${version}XtaInstructions.java") 
    or die("Failed to move XtaInstructions.java\n");
}

main()
