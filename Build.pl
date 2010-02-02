#!/usr/bin/perl
use warnings;
use strict;

use SetupEnv;
use File::Copy;
use XmosBuildLib;
use XmosArg;
use File::Temp qw/ :mktemp  /;
use File::Path;

my $MAKE_FLAGS;
my $CONFIG;
my $HOST;
my $DOMAIN;
my $BUILD_NUM;
my $BIN;

my $buildDir = "";
my $tailCmd = "";
my $platform = "";
my $config = "";
my $buildDirProperties = "";

my $eclipseDir = "";
my $buildOutput = "";
my $target = "";
my $exeName = "";

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
  chdir("$TOOLS_XTA${SLASH}com.xmos.xta.core${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta${SLASH}core") 
    or die("Failed to move to $TOOLS_XTA${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta");

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
  system("ant clean"); 
    die() if ($? != 0);
  chdir("../");

  SetupVars();
  rmtree([<"$TOOLS_XTA/export/*.zip">, "$TOOLS_XTA/export/xta_bin", "$TOOLS_XTA/export/xta_bin64", "$TOOLS_XTA/tmp", "$INSTALLPATH${SLASH}xta_bin", "$INSTALLPATH${SLASH}xta_bin64"]);

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
  print(" + Building Generation Tools\n");
  chdir("$TOOLS_XTA${SLASH}com.xmos.xta.core${SLASH}generation") 
    or confess("Build failed to find $TOOLS_XTA${SLASH}com.xmos.xta.core${SLASH}generation\n");

  system("$MAKE $MAKE_FLAGS");
  confess() if ($? != 0);

  print(" + Generating files from Databases\n");

  CreateXtaFiles("xs1b", "xs1b");

  chdir("$BUILDPATH") or die("oops");
}

sub DoBuild
{
  chdir("$TOOLS_XTA") 
    or die("Build failed to find $TOOLS_XTA\n");

  system("$MAKE $MAKE_FLAGS");

  UpdateBuildNumber();

  chdir("com.xmos.xta.core") 
    or die("Failed to find com.xmos.xta.core\n");
  system("ant"); 
    die() if ($? != 0);
  chdir("../");

  BuildEclipse();

  chdir("$BUILDPATH") or die("oops");
}

sub DoInstall
{
  SetupVars();

  if ($HOST eq "PC") {
    $ENV{PATH} = "C:/cygwin/bin;" . $ENV{PATH};
  }

  if ($HOST eq "PC") {
    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

  } elsif ($HOST eq "Mac") {
    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

  } else {
    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin${SLASH}configuration${SLASH}config.ini");

    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin64 $INSTALLPATH${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}xta_bin64");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}xta_bin64${SLASH}configuration${SLASH}config.ini");

    system("cp -r $TOOLS_XTA${SLASH}export${SLASH}xta_bin64 $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}");
    system("cp -r $TOOLS_XTA${SLASH}com.xmos.xta.ui${SLASH}xtagui.ini $INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin64");
    ConfigureWorkspaceDirectory("$INSTALLPATH${SLASH}External${SLASH}Product${SLASH}xta_bin64${SLASH}configuration${SLASH}config.ini");
  }

  XmosBuildLib::InstallFile("$DOMAIN", "${BIN}", "xta.exe");
  XmosBuildLib::InstallReleaseFile("$DOMAIN", "${BIN}", "xta.exe", "bin");

  XmosBuildLib::InstallFile("$DOMAIN", "export${SLASH}jars", "xta.jar");
  XmosBuildLib::InstallReleaseFile("$DOMAIN", "export${SLASH}jars", "xta.jar", "bin");
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
   
  chdir("$ARCH_DBS_XML${SLASH}$versionDir") or die("Generation failed to find $version databases\n");
  system("$TOOLS_XTA${SLASH}${BIN}${SLASH}XtaGeneration.exe XSystemDb${SLASH}XS1-040.xml $version");

  print(" + Installing $version generated xta files\n");
  rename("XtaInstructionDecoder.java", "$TOOLS_XTA${SLASH}com.xmos.xta.core${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta${SLASH}core${SLASH}engine${SLASH}instructions${SLASH}${version}XtaInstructionDecoder.java") 
    or die("Failed to move XtaInstructionDecoder.java\n");
  rename("XtaInstructions.java",       "$TOOLS_XTA${SLASH}com.xmos.xta.core${SLASH}src${SLASH}com${SLASH}xmos${SLASH}xta${SLASH}core${SLASH}engine${SLASH}instructions${SLASH}${version}XtaInstructions.java") 
    or die("Failed to move XtaInstructions.java\n");
}

sub SetupVars
{
  $buildDir    = "$TOOLS_XTA/tmp";
  $exeName     = "xtagui";

  if ($HOST eq "PC") {
    $tailCmd    = "tail -2";
    $platform   = "win32";
    $config     = "win32, win32, x86";
    $buildDirProperties = $buildDir;
  } elsif ($HOST eq "Cygwin") {
    $tailCmd    = "tail -n2";
    $platform   = "cygwin";
    $config     = "win32, win32, x86";
    $buildDirProperties = `cygpath -m $buildDir`;
    chomp($buildDirProperties);
  } elsif ($HOST eq "Mac") {
    $tailCmd    = "tail -n2";
    $platform   = "mac";
    $config     = "macosx, carbon, x86";
    $buildDirProperties = $buildDir;
  } else {
    $tailCmd    = "tail -n2";
    $platform   = "linux";
    $buildDirProperties = $buildDir;
    $exeName    = "$exeName.exe";
    $config = "linux, gtk, x86_64 & linux, gtk, x86";
  }

  $eclipseDir  = "$ENV{'ECLIPSE_HOME'}";
  $buildOutput = "$buildDir/build.output.$platform";
  $target      = "xta.$platform.zip";
}

sub UpdateBuildNumber
{
  if (defined($BUILD_NUM)) 
  {
    UpdateBuildNumberInFile("<com.xmos.xta.ui${SLASH}plugin.xml");
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

sub BuildEclipse
{
  SetupVars();

  rmtree($buildDir);
  MakeDirectory("$buildDir/build/plugins");
  MakeDirectory("$buildDir/build_config");
  
  unlink($buildOutput);

  system("cp -r $TOOLS_XTA/../infr_libs_java/com.xmos.libs $buildDir/build/plugins/com.xmos.libs        >> $buildOutput");
  system("cp -r $TOOLS_XTA/com.xmos.xta.core               $buildDir/build/plugins/com.xmos.xta.core    >> $buildOutput");
  system("cp -r $TOOLS_XTA/com.xmos.xta.ui                 $buildDir/build/plugins/com.xmos.xta.ui      >> $buildOutput");
  system("cp -r $TOOLS_XTA/com.xmos.xta.ui.extn            $buildDir/build/plugins/com.xmos.xta.ui.extn >> $buildOutput");

  # Creates the target specific build properties file..
  my $buildPropertiesFile = CreateBuildPropertiesFile($platform, $config, $buildDirProperties, $eclipseDir, $exeName);
  system("cp $buildPropertiesFile $buildDir/build_config/build.properties >> $buildOutput");

  my $buildFile=`echo $eclipseDir/plugins/org.eclipse.pde.build_*`;
  $buildFile = substr($buildFile, 0, length($buildFile) -1); # strip off newline
  $buildFile = "$buildFile/scripts/productBuild/productBuild.xml";
 
  my $launcherJar=`echo $eclipseDir/plugins/org.eclipse.equinox.launcher_*.jar`;
  $launcherJar = substr($launcherJar, 0, length($launcherJar) -1);


  my $buildCmd = "java -jar $launcherJar " .
                 "-application org.eclipse.ant.core.antRunner " .
                 "-buildfile $buildFile " .
                 "-Dbuilder=$buildDirProperties/build_config -quiet";

  system("$buildCmd >> $buildOutput");
    die() if ($? != 0);

  system($tailCmd . " $buildOutput");
    die() if ($? != 0);

  # Installs the zip file into the export directory.
  unlink("$TOOLS_XTA/export/$target");

  if ($HOST ne "PC" && $HOST ne "Mac") {
      system("mv $buildDir/build/I.TestBuild/*.zip $TOOLS_XTA/export/");
  } else {
    system("mv $buildDir/build/I.TestBuild/*.zip $TOOLS_XTA/export/$target");
  }

  # Unzips the zip file..
  chdir("$TOOLS_XTA/export") 
    or die("Unable to chdir to: $TOOLS_XTA/export\n");

  if ($HOST ne "PC" && $HOST ne "Mac") {
    rmtree(["xta_bin", "xta_bin64"]);
    system("unzip `ls | grep 64` >> $buildOutput; mv xta_bin xta_bin64 >> $buildOutput; unzip `ls | grep -v 64 | grep .zip` >> $buildOutput");
  } else {
    system("unzip -o $target >> $buildOutput");
  }
}

sub CreateBuildPropertiesFile
{
  my $platform   = shift;
  my $config     = shift;
  my $buildDir   = shift;
  my $eclipseDir = shift;
  my $exeName    = shift;

  # Only use forward slahes in the properties file.
  $buildDir =~ s/\\/\//g; 
  $eclipseDir =~ s/\\/\//g; 

  my $buildPropertiesTemplate = "$TOOLS_XTA/com.xmos.xta.ui/build.properties.template";
  my $buildPropertiesFile     = "$TOOLS_XTA/com.xmos.xta.ui/build.properties.$platform";

  open(my $infp, "<$buildPropertiesTemplate") 
    or die("Cannot open file: $buildPropertiesTemplate for reading");

  open(my $outfp, ">$buildPropertiesFile") 
    or die("Cannot open file: $buildPropertiesFile for writing");

  while(my $line = <$infp>) {
    $line =~ s/\[HOST\]/$platform/;
    $line =~ s/\[CONFIG\]/$config/;
    $line =~ s/\[BUILD_DIRECTORY\]/$buildDir\/build/;
    $line =~ s/\[ECLIPSE_DIRECTORY\]/$eclipseDir/;
    $line =~ s/\[EXECUTABLE_NAME\]/$exeName/;
    print $outfp "$line"; 
  }
  close($infp);
  close($outfp);

  return $buildPropertiesFile;
}

sub MakeDirectory
{
  my $dir = shift;
  mkpath("$dir") 
    or die("Unable to create directory: $dir\n");
}

sub ConfigureWorkspaceDirectory
{
  my $configFile = shift;
  open LOGFILE, ">>$configFile" 
    or die "Error: Cannot open config file $configFile";

  print LOGFILE "osgi.instance.area=xtaworkspace\n";
  close LOGFILE;
}

main()
