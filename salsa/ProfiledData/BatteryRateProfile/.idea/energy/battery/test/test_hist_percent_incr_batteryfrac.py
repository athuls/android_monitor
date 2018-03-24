import unittest
import sys
import os
import filecmp
from scipy.stats.stats import pearsonr
import matplotlib.pyplot as plt
import numpy as np
dir = os.path.dirname(__file__)
moduledirname = os.path.join(dir, '../../battery')
sys.path.append(moduledirname)

from histogram import hist_percent_incr_batteryfrac as batt
from histogram import hist_percent_fixed_size as fixed_size
from histogram import interface_test as interface
from histogram import hist_percent_incr_batteryfrac_mult as batt2

def predict(histchange, powerchange, sizechange, m, b):
    errors = []
    for i in range(0, len(histchange)):
        if sizechange[i] == 0:
            errors.append("no size change")
            continue
        prediction = (m * histchange[i] + b)/float(sizechange[i])
        # prediction = (m * histchange[i] + b)
        if powerchange[i] != 0:
            # errors.append("REL: " + str(abs(prediction - powerchange[i])/powerchange[i]))
            errors.append(abs(prediction - powerchange[i])/powerchange[i])
        else:
            errors.append("ABS: " + str(abs(prediction - powerchange[i])))



    # errors = []
    # for i in range(0, len(histchange)):
    #     if float(sizechange[i]) < 125 and slopes[0]:
    #         prediction = slopes[0] * histchange[i] + intercepts[0]
    #     elif float(sizechange[i]) < 250 and slopes[1]:
    #         prediction = slopes[1] * histchange[i] + intercepts[1]
    #     elif float(sizechange[i]) < 375 and slopes[2]:
    #         prediction = slopes[2] * histchange[i] + intercepts[2]
    #     elif float(sizechange[i]) < 500 and slopes[3]:
    #         prediction = slopes[3] * histchange[i] + intercepts[3]
    #     elif float(sizechange[i]) < 625 and slopes[4]:
    #         prediction = slopes[4] * histchange[i] + intercepts[4]
    #     elif float(sizechange[i]) < 750 and slopes[5]:
    #         prediction = slopes[5] * histchange[i] + intercepts[5]
    #     elif float(sizechange[i]) < 875 and slopes[6]:
    #         prediction = slopes[6] * histchange[i] + intercepts[6]
    #     elif float(sizechange[i]) < 1000 and slopes[7]:
    #         prediction = slopes[7] * histchange[i] + intercepts[7]
    #     elif slopes[8]:
    #         prediction = slopes[8] * histchange[i] + intercepts[8]
    #     else:
    #         prediction = None
    #     if not prediction:
    #         errors.append("No prediction")
    #     if powerchange[i] == 0:
    #         errors.append("powerchange 0, abs error printed: "+str(prediction))
    #     else:
    #         val = (prediction, powerchange[i], abs(prediction - powerchange[i])/powerchange[i])
    #
    #         errors.append(val)

    return errors

def get_predictors(histchange, powerchange, sizechange):
    powernorm = [powerchange[i]*float(sizechange[i]) for i in range(len(powerchange))]
    # powernorm = [powerchange[i]*1 for i in range(len(powerchange))]
    plt.scatter(histchange, powernorm)
    m, b = np.polyfit(histchange, powernorm, 1)
    print(m,b)
    plt.show()
    return m, b


    # lines_x = [ [] for i in range(0,9)]
    # lines_y = [ [] for i in range(0,9)]
    # slopes = [ None for i in range(0,9)]
    # intercepts = [ None for i in range(0,9)]
    #
    # colors = ['blue', 'green', 'pink', 'orange', 'red', 'purple', 'gold', 'brown', 'black']
    #
    # for i in range(0, len(histchange)):
    #     if float(sizechange[i]) < 125:
    #         lines_x[0].append(histchange[i])
    #         lines_y[0].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='blue')
    #     elif float(sizechange[i]) < 250:
    #         lines_x[1].append(histchange[i])
    #         lines_y[1].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='green')
    #     elif float(sizechange[i]) < 375:
    #         lines_x[2].append(histchange[i])
    #         lines_y[2].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='pink')
    #     elif float(sizechange[i]) < 500:
    #         lines_x[3].append(histchange[i])
    #         lines_y[3].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='orange')
    #     elif float(sizechange[i]) < 625:
    #         lines_x[4].append(histchange[i])
    #         lines_y[4].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='red')
    #     elif float(sizechange[i]) < 750:
    #         lines_x[5].append(histchange[i])
    #         lines_y[5].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='purple')
    #     elif float(sizechange[i]) < 875:
    #         lines_x[6].append(histchange[i])
    #         lines_y[6].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='gold')
    #     elif float(sizechange[i]) < 1000:
    #         lines_x[7].append(histchange[i])
    #         lines_y[7].append(powerchange[i])
    #         #plt.scatter([histchange[i]], [powerchange[i]], c='brown')
    #     else:
    #         lines_x[8].append(histchange[i])
    #         lines_y[8].append(powerchange[i])
    #         plt.scatter([histchange[i]], [powerchange[i]], c='black')
    #
    #
    # for i in range(0,9):
    #     if len(lines_x[i]) > 0:
    #         plt.scatter(lines_x[i], lines_y[i], c=colors[i])
    #         slopes[i], intercepts[i] = np.polyfit(lines_x[i], lines_y[i], 1)
    # plt.show()
    # return slopes, intercepts






class TestHistogramProfiling(unittest.TestCase):

    # This is not for testing, it is run to plot graphs for inferring
    # results from running variable window size approach
    # def test_runforvariablewindowoutputalternating(self):
    #     histpowerdata = batt2.generatePlotDataForHistProfileMultipleActors('../mobile_logs/log_alt_long.txt', 3)
    #
    #     # demo1.Nqueens2 is the heavier run
    #     for i in range(0, len(histpowerdata["demo1.Nqueens"])):
    #         histchange1= histpowerdata["demo1.Nqueens"][i][0]
    #         histchange2= histpowerdata["demo1.Nqueens2"][i][0]
    #         print(histpowerdata["demo1.Nqueens"][i][1])
    #         if histchange1 != 0:
    #             plt.plot(histchange1, histpowerdata["demo1.Nqueens"][i][1], 'bo', c="red")
    #         elif histchange2 != 0:
    #             plt.plot(histchange2, histpowerdata["demo1.Nqueens2"][i][1], 'bo', c="blue")
    #
    #     plt.show()


        # Plot the generated output file
        # histchange = [x[0] for x in histpowerdata]
        # powerchange = [x[1] for x in histpowerdata]
        # sizechange = [x[2] for x in histpowerdata]
        # print(pearsonr(histchange,powerchange))
        # #print(pearsonr(histchange,sizechange))
        # plt.plot(histchange, powerchange, 'bo')
        # #plt.plot(histchange, sizechange, 'ro')
        # plt.xlabel('Histogram distance measure from a reference')
        # plt.ylabel('Corresponding change in power consumption')
        # plt.title('NQueens-heavy_2, window of 5: Variable window size incremental')
        # plt.xlabel('Histogram distance measure from a reference')
        # plt.ylabel('Corresponding change in power consumption')
        # plt.show()
        # pairs = [(x[0], x[1]) for x in histpowerdata]

    # This is not for testing, it is run to plot graphs for inferring
    # results from running variable window size approach
    # def test_runforvariablewindowoutput(self):
    #     histpowerdata = batt.generatePlotDataForHistProfile('../mobile_logs/log_alt_long.txt', 3)
    #
    #     # Plot the generated output file
    #     histchange = [x[0] for x in histpowerdata]
    #     powerchange = [x[1] for x in histpowerdata]
    #     sizechange = [x[2] for x in histpowerdata]
    #     print(pearsonr(histchange,powerchange))
    #     #print(pearsonr(histchange,sizechange))
    #     plt.plot(histchange, powerchange, 'bo')
    #     #plt.plot(histchange, sizechange, 'ro')
    #     plt.xlabel('Histogram distance measure from a reference')
    #     plt.ylabel('Corresponding change in power consumption')
    #     plt.title('NQueens-heavy_2, window of 5: Variable window size incremental')
    #     plt.xlabel('Histogram distance measure from a reference')
    #     plt.ylabel('Corresponding change in power consumption')
    #     plt.show()

    def test_predictor(self):
        ### READ INITIAL DATA
        dir = os.path.dirname(__file__)
        filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
        in_window_size = 3
        actor_name='demo1.Nqueen'
        # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('../mobile_logs/Nqueens_heavy.txt', in_window_size, filename, range=(.50,.60))
        # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(.4,.5))
        newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(0.3,0.4))
        newSplittingInstance.extract_windows()
        histpowerprof = interface.generateHistogramPowerInfo(filename)

        ### INITIAL DATA
        histchange = [x[0] for x in histpowerprof]
        powerchange = [x[1] for x in histpowerprof]
        sizechange = [x[2] for x in histpowerprof]


        plt.title('NQueens-heavy_2, window of 3: Fixed window size tumbling (key weighted mean for each histogram)')
        plt.xlabel('Histogram distance measure from a reference')
        plt.ylabel('Corresponding change in power consumption')
        print('Plotting data is ready')

        m, b = get_predictors(histchange, powerchange, sizechange)


        newSplittingInstance2 = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(.55,.6))
        newSplittingInstance2.extract_windows()
        histpowerprof2 = interface.generateHistogramPowerInfo(filename)

        histchange2 = [x[0] for x in histpowerprof2]
        powerchange2 = [x[1] for x in histpowerprof2]
        sizechange2 = [x[2] for x in histpowerprof2]

        errors = predict(histchange2, powerchange2, sizechange2, m, b)

        print ("ERROR VALS:")
        x = 0
        errors_plot = []
        for e in errors:
            print(e)
            x += 1
            errors_plot.append(e)
        print(np.mean(errors_plot), np.std(errors_plot))
        plt.scatter(range(0,x), errors_plot)
        plt.show()


    # This is not for testing, it is run to plot graphs for inferring
    # results from running fixed window size approach
    def test_runforfixedwindowoutput(self):
        dir = os.path.dirname(__file__)
        filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
        in_window_size = 5
        actor_name = ""
        # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('../mobile_logs/Nqueens_heavy.txt', actor_name, in_window_size, filename, range=(.15,.65))
        newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('../mobile_logs/Nqueens_heavy.txt', actor_name, in_window_size, filename)
        newSplittingInstance.extract_windows()
        histpowerprof = interface.generateHistogramPowerInfo(filename)

        # Plot the generated output file
        histchange = [x[0] for x in histpowerprof]
        powerchange = [x[1] for x in histpowerprof]
        totalpower = sum(powerchange)
        print("Total power is " + str(totalpower))
        sizechange = [x[2] for x in histpowerprof]
        # print(pearsonr(histchange,powerchange))


        print("Total energy calculated: ", sum(powerchange)*in_window_size)

        #plt.plot(histchange, powerchange, 'bo')
        plt.title('NQueens-heavy_2, window of 3: Fixed window size tumbling (key weighted mean for each histogram)')
        plt.xlabel('Histogram distance measure from a reference')
        plt.ylabel('Corresponding change in power consumption')
        print('Plotting data is ready')
        # print(powerchange)




        powernorm = [powerchange[i]*float(sizechange[i]) for i in range(len(histchange))]

        plt.scatter(histchange, powernorm)
        m, b = np.polyfit(histchange, powernorm, 1)
        print(m,b)
        plt.show()

        lines_x = [ [] for i in range(0,9)]
        lines_y = [ [] for i in range(0,9)]
        colors = ['blue', 'green', 'pink', 'orange', 'red', 'purple', 'gold', 'brown', 'black']


        for i in range(0, len(histchange)):
        #     print(histchange[i])
        #     print(curr)
        #     if(histchange[i] <= 2.2 and powerchange[i] <= 0.1):
        #     plt.plot()
            if float(sizechange[i]) < 125:
                lines_x[0].append(histchange[i])
                lines_y[0].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='blue')
            elif float(sizechange[i]) < 250:
                lines_x[1].append(histchange[i])
                lines_y[1].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='green')
            elif float(sizechange[i]) < 375:
                lines_x[2].append(histchange[i])
                lines_y[2].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='pink')
            elif float(sizechange[i]) < 500:
                lines_x[3].append(histchange[i])
                lines_y[3].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='orange')
            elif float(sizechange[i]) < 625:
                lines_x[4].append(histchange[i])
                lines_y[4].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='red')
            elif float(sizechange[i]) < 750:
                lines_x[5].append(histchange[i])
                lines_y[5].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='purple')
            elif float(sizechange[i]) < 875:
                lines_x[6].append(histchange[i])
                lines_y[6].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='gold')
            elif float(sizechange[i]) < 1000:
                lines_x[7].append(histchange[i])
                lines_y[7].append(powerchange[i])
                #plt.scatter([histchange[i]], [powerchange[i]], c='brown')
            else:
                lines_x[8].append(histchange[i])
                lines_y[8].append(powerchange[i])
                plt.scatter([histchange[i]], [powerchange[i]], c='black')

        #
        slopes = []
        intercepts = []

        for i in range(0,9):
            if len(lines_x[i]) > 0:
                plt.scatter(lines_x[i], lines_y[i], c=colors[i])
                m, b = np.polyfit(lines_x[i], lines_y[i], 1)
                print(m,b)
        plt.show()

        #
        #
        # # m, b = np.polyfit(histchange, powerchange, 1)
        # # print(m,b)
        # # x = np.array([min(histchange), max(histchange)])
        # # plt.plot(x, m*x + b, '-')
        # plt.show()

        # for i in range(0, len(histchange)):
        #     #plt.plot()
        #     if drop_times[curr] < 60:
        #         # plt.scatter([histchange[i]], [powerchange[i]], c='m')
        #         print("")
        #     elif drop_times[curr] < 70:
        #         #plt.scatter([histchange[i]], [powerchange[i]], c='r')
        #         print("")
        #     elif drop_times[curr] < 80:
        #         # plt.scatter([histchange[i]], [powerchange[i]], c='g')
        #         print("")
        #     elif drop_times[curr] < 85:
        #         plt.scatter([histchange[i]], [powerchange[i]], c='g')
        #         # print("")
        #     elif drop_times[curr] < 88:
        #         plt.scatter([histchange[i]], [powerchange[i]], c='r')
        #         # print("")
        #     elif drop_times[curr] < 90:
        #         # plt.scatter([histchange[i]], [powerchange[i]], c='r')
        #         print("")
        #     else:
        #         plt.scatter([histchange[i]], [powerchange[i]], c='y')
        #
        #     print(in_window_size, total + drop_times[curr], i)
        #     if total + drop_times[curr] == i:
        #         total += drop_times[curr]
        #         curr += 1

        # plt.figure()
        # plt.plot(histchange[0:-1], powerchange[1:], 'bo')
        # plt.title('NQueens-heavy_2, window of 3: Fixed window size tumbling (key weighted mean for each histogram)')
        # plt.xlabel('Histogram distance measure from a reference')
        # plt.ylabel('Corresponding change in power consumption')
        # print('Plotting data is ready')
        #
        # plt.show()

    # # Test for variable interval histogram generation
    # def test_variablewindowsize(self):
    #     histpowerdata = batt.generatePlotDataForHistProfile('TestLog_VariableWindowSize.txt', 5)
    #     histchanges = [x[0] for x in histpowerdata]
    #     powerchanges = [x[1] for x in histpowerdata]
    #     print('Printing hist and power changes')
    #     print(histchanges,powerchanges)
    #     print('Done printing hist and power changes')
    #     self.assertEqual(len(histchanges),15)
    #     self.assertEqual(len(powerchanges),15)
    #     expected_histchanges = [226.0, 229.0, 232.0, 235.0, 238.0, 240.5, 243.5, 246.5, 249.5, 252.5, 240.5, 228.5, 216.5, 204.5, 192.5]
    #     expected_powerchages = [0.002213381483392198, 0.0021853643252981374, 0.002158071748878924, 0.0021314760041980726, 0.0021055507404755622, 0.002079002079002079, 0.002053388090349076, 0.002028397565922921, 0.002004008016032064, 0.0019801980198019802, 0.0020779248758523374, 0.0021859162594528407, 0.0023058789742607905, 0.002439920442884832, 0.0025906735751295338]
    #     i = 0
    #     for histchange in histchanges:
    #         self.assertEqual(histchange, expected_histchanges[i])
    #         i += 1
    #     i = 0
    #     for powerchange in powerchanges:
    #         self.assertEqual(powerchange, expected_powerchages[i])
    #         i += 1
    #
    # # Test for fixed interval size histogram generation
    # def test_fixedwindowsize(self):
    #     newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('TestLog_FixedWindowSize.txt',3, 'ActualLog_FixedWindowSize.txt')
    #     newSplittingInstance.extract_windows()
    #     dir = os.path.dirname(__file__)
    #     filename =  os.path.join(dir, 'ActualLog_FixedWindowSize.txt')
    #     self.assertTrue(filecmp.cmp('ActualLog_FixedWindowSize.txt', 'ExpectedOutput_FixedWindowSize.txt'))

if __name__ == '__main__':
    #unittest.main()
    unittest.main()
