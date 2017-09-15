import sys
import json
import re
import bisect
from datetime import datetime
from datetime import timedelta

class LogIntervalSplitter:
    start_datetime = None
    end_datetime = None
    batteryChangeIntervals = []

    def returnBatteryChangeIntervals(self):
        currentBattery = 0
        previousBattery = 0
        with open(sys.argv[1]) as fp:
            for line in fp:
                matchObj = re.match(r'\[(.*)\] Battery level is (\d+\.\d+)', line, re.I)
                if matchObj:
                    if(self.start_datetime is None):
                        self.start_datetime = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
                    self.end_datetime = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
                    currentBattery = matchObj.group(2)
                    if (currentBattery != previousBattery):
                        previousBattery = currentBattery
                        datetime_obj = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
                        self.batteryChangeIntervals.append(datetime_obj)

        self.batteryChangeIntervals.append(self.end_datetime)
        return self.batteryChangeIntervals