#!/usr/bin/perl
use warnings;
use strict;
use DateTime;
use DateTime::Format::Strptime;

sub ConvertToDate {
	my $parser = DateTime::Format::Strptime->new(
		pattern => '%b%n%d,%Y%n%T',
		time_zone => 'local',
		on_error => 'croak',
	);

	my $input = $_[0];
	my $toDate = $parser->parse_datetime($input);
	return $toDate;
	
}

#ASSUMPTIONS: 
#1) Each new battery level is ATMOST one level below the previous one that was read. If the sampling was not frequent enough to miss multiple battery level changes, this script might break
#2) Supports only actors of a single type running
my $fileName = $ARGV[0];
my $FH;
open($FH, "<", $fileName);
my $counter = 0;
my $currentBattery = "";
my $previousTime = undef;
my $duration = 0;
my $duration_sec = 0;
my $dateFormatted = undef;
my $actorCount = 0;
while(<$FH>) {
	if($_ =~ m/\[(.*)\] Battery level is (\d+\.\d+) a[nc]/) {
		if($currentBattery ne $2) {
			$dateFormatted = ConvertToDate($1);
			if(($currentBattery ne "") && (defined $previousTime)) {
				$duration = $dateFormatted->subtract_datetime_absolute($previousTime);
				$duration_sec = $duration->seconds();
				my $perActorTime = $duration_sec * $actorCount; 
				#print $currentBattery." ".$perActorTime."\n";
				#print $currentBattery." ".$actorCount."\n";
				print $currentBattery." ".$duration_sec." ".$actorCount."\n";
				
				#Number of actors and time for 1% drop
				#print $actorCount." ".$duration_sec."\n";
				$actorCount = 0;
			}

			$previousTime = $dateFormatted;
			$currentBattery = $2;	
		}

		$counter++;
	}

	if($_ =~ m/\[(.*)\] Battery level is (\d+\.\d+) actor counts/) {
		$_ = <$FH>;
		if($_ =~ m/(.*)\: (\d+)/) {
			$actorCount += $2;
			#print $currentBattery." ".$2."\n";
		}
	}

	if($_ =~ m/\[(.*)\] Battery level is (\d+\.\d+) and/) {
		#print "$currentBattery 0\n";
	}
}
