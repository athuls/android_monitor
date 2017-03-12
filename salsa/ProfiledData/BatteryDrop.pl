#!/usr/bin/perl
use warnings;
use strict;

my $fileName = $ARGV[0];
my $FH;
open($FH, "<", $fileName);
my $counter = 0;
while(<$FH>)
{
	if($_ =~ m/Battery level is (.*) and/) {
		print $counter."\t".$1."\n";
		$counter++;
	}
}
