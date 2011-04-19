my $argc = scalar(@ARGV);

if ($argc != 1) {
    printf("Usage: perl Regify.pl <file>\n");
    exit(1);
}

my $fp;
open($fp, "<$ARGV[0]") 
    or die("Error: Cannot open file: $ARGV[0]");

while (my $line = <$fp>) {
      $line =~ s/\+/\\+/g;                    # Plus - do first so that it doesn't break the \d+
      $line =~ s/\(\d+\)/\(\\d+\)/g;          # Node id number
      $line =~ s/\d+\.\d+ ns/\\d+\.\\d+ ns/g; # Time Taken (ns)
      $line =~ s/\d+\.\d+ us/\\d+\.\\d+ us/g; # Time Taken (us)
      $line =~ s/\d+\.\d+ ms/\\d+\.\\d+ ms/g; # Time Taken (ms)
      $line =~ s/\(/\\(/g;                    # Right bracket
      $line =~ s/\)/\\)/g;                    # Left bracket
      $line =~ s/\s+:/\\s+:/g;                # Space before :
      $line =~ s/\*/\\*/g;                    # escape the '*'s (unknowns)
      $line =~ s/\./\\\./g;                   # Full stop
      $line =~ s/\[/\\[/g;                    # Right [
      $line =~ s/\]/\\]/g;                    # Left ]

      print $line;
}
