import unittest
import sys
import os
dir = os.path.dirname(__file__)
moduledirname = os.path.join(dir, '../../battery')
sys.path.append(moduledirname)

from histogram import hist_percent_incr_batteryfrac as batt
class TestHistogramProfiling(unittest.TestCase):

    def test_endtoend(self):
        #histpowerdata = batt.generatePlotDataForHistProfile('testlog.txt', 3)
        histpowerdata = batt.generatePlotDataForHistProfile('/home/athuls89/Desktop/OSL/osl/MobileCloud/android_monitor/salsa/ProfiledData/BatteryRateProfile/.idea/energy/battery/mobile_logs/Nqueens_light.txt', 5)
        histchanges = [x[0] for x in histpowerdata]
        powerchanges = [x[1] for x in histpowerdata]
        self.assertEqual(len(histchanges),3)
        self.assertEqual(len(powerchanges),3)
        expected_histchanges = [0.875, 0.875, 0.125]
        expected_powerchages = [0.0075, 0.015, 0.0030000000000000027]
        i = 0
        for histchange in histchanges:
            self.assertEqual(histchange, expected_histchanges[i])
            i += 1
        i = 0
        for powerchange in powerchanges:
            self.assertEqual(powerchange, expected_powerchages[i])
            i += 1

if __name__ == '__main__':
    unittest.main()
