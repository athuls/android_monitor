import unittest
import sys
import os
import filecmp
from scipy.stats.stats import pearsonr
import matplotlib.pyplot as plt
dir = os.path.dirname(__file__)
moduledirname = os.path.join(dir, '../../battery')
sys.path.append(moduledirname)

from histogram import hist_percent_incr_batteryfrac as batt
from histogram import hist_percent_fixed_size as fixed_size

class TestHistogramProfiling(unittest.TestCase):

    # This is not a test, it is run to plot graphs for inferring results
    def test_runforoutput(self):
        histpowerdata = batt.generatePlotDataForHistProfile('/home/athuls89/Desktop/OSL/osl/MobileCloud/android_monitor/salsa/ProfiledData/BatteryRateProfile/.idea/energy/battery/mobile_logs/Nqueens_heavy_2.txt', 5)

        # Plot the generated output file
        histchange = [x[0] for x in histpowerdata]
        powerchange = [x[1] for x in histpowerdata]
        print(pearsonr(histchange,powerchange))
        plt.plot(histchange, powerchange, 'bo')
        plt.xlabel('Histogram distance measure from a reference')
        plt.ylabel('Corresponding change in power consumption')
        print('Plotting data is ready')
        plt.show()

    # Test for variable interval histogram generation
    def test_variablewindowsize(self):
        histpowerdata = batt.generatePlotDataForHistProfile('TestLog_VariableWindowSize.txt', 5)
        histchanges = [x[0] for x in histpowerdata]
        powerchanges = [x[1] for x in histpowerdata]
        print('Printing hist and power changes')
        print(histchanges,powerchanges)
        print('Done printing hist and power changes')
        self.assertEqual(len(histchanges),15)
        self.assertEqual(len(powerchanges),15)
        expected_histchanges = [226.0, 229.0, 232.0, 235.0, 238.0, 240.5, 243.5, 246.5, 249.5, 252.5, 240.5, 228.5, 216.5, 204.5, 192.5]
        expected_powerchages = [0.002213381483392198, 0.0021853643252981374, 0.002158071748878924, 0.0021314760041980726, 0.0021055507404755622, 0.002079002079002079, 0.002053388090349076, 0.002028397565922921, 0.002004008016032064, 0.0019801980198019802, 0.0020779248758523374, 0.0021859162594528407, 0.0023058789742607905, 0.002439920442884832, 0.0025906735751295338]
        i = 0
        for histchange in histchanges:
            self.assertEqual(histchange, expected_histchanges[i])
            i += 1
        i = 0
        for powerchange in powerchanges:
            self.assertEqual(powerchange, expected_powerchages[i])
            i += 1

    # Test for fixed interval size histogram generation
    def test_fixedwindowsize(self):
        newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('TestLog_FixedWindowSize.txt',3, 'ActualLog_FixedWindowSize.txt')
        newSplittingInstance.extract_windows()
        dir = os.path.dirname(__file__)
        filename =  os.path.join(dir, 'ActualLog_FixedWindowSize.txt')
        self.assertTrue(filecmp.cmp('ActualLog_FixedWindowSize.txt', 'ExpectedOutput_FixedWindowSize.txt'))

if __name__ == '__main__':
    unittest.main()
