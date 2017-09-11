#!/usr/bin/perl
use warnings;
use strict;
use DateTime;
use DateTime::Format::Strptime;

#ASSUMPTION: Each new battery level is ATMOST one level below the previous one that was read. If the sampling was not frequent enough to miss multiple battery level changes, this script might break
my $fileName = $ARGV[0];
my $FH;
open($FH, "<", $fileName);
my $counter = 0;
print "ActorCount\n";
while(<$FH>)
{
	if($_ =~ m/(.*)demo1.Nqueens(.*): (\d+),/) {
		print $3."\n";
		$counter++;
	}
}
