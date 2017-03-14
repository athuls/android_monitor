#!/usr/bin/perl
use warnings;
use strict;
use DateTime;
use DateTime::Format::Strptime;

#use File::Basename qw(dirname);
#use Cwd  qw(abs_path);
#use lib dirname(dirname abs_path $0) . '/lib';

#use LogProcess qw(ConvertToDate);

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

#ASSUMPTION: Each new battery level is ATMOST one level below the previous one that was read. If the sampling was not frequent enough to miss multiple battery level changes, this script might break
my $fileName = $ARGV[0];
my $FH;
open($FH, "<", $fileName);
my $counter = 0;
my $currentBattery = "";
my $previousTime = undef;
my $duration = 0;
my $duration_sec = 0;
my $dateFormatted = undef;
while(<$FH>)
{
	if($_ =~ m/\[(.*)\] Battery level is (\d+\.\d+) a[nc]/) {
		if($currentBattery ne $2) {
			$dateFormatted = ConvertToDate($1);
			if(($currentBattery ne "") && (defined $previousTime)) {
				$duration = $dateFormatted->subtract_datetime_absolute($previousTime);
				$duration_sec = $duration->seconds();
				print $currentBattery." ".$duration_sec."\n";
			}

			$previousTime = $dateFormatted;
			$currentBattery = $2;	
		}

		$counter++;
	}
}
