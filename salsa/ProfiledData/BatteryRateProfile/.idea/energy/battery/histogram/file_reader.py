
# retuns dictionary
# key is actor name, value is a list of lists
# each list is the actor counts for each interval
# takse in file name, and list of actor names
def parseLog(filename, actors):
    vals_per_drop = {}
    curr = {}

    for name in actors:
        curr[name] = []
        vals_per_drop[name] = []

    curr_bat = None

    with open(filename) as fp:
        lines = []
        for l in fp:
            lines.append(l)
        k = 0
        while k < len(lines):
            line = lines[k]
            text = "Battery level is "
            bat_ind = line.find(text)
            if(bat_ind != -1):
                bat = float(line[bat_ind+len(text):].split(' ')[0])
                if(curr_bat == None): # first iteration
                    curr_bat = bat
                if(bat < curr_bat):
                    print "drop"
                    for key in curr:
                        vals_per_drop[key].append(curr[key])
                        curr[key] = []
                    curr_bat = bat

            ind = line.find('$State')


            if(line[0] == '[' and line.find('no') != -1):
                for key in curr:
                    curr[key].append(0)
            elif(ind != -1):
                indeces = []
                found = {}
                while k < len(lines) and lines[k] != '\n':
                    # print lines[st]
                    ind = lines[k].find('$State')
                    actor = lines[k][0:ind]
                    comma_ind = lines[k].find(',', ind)
                    # print lines[st][ind + 8 : comma_ind]
                    num = int(lines[k][ind + 8 : comma_ind])
                    print actor, num

                    found[actor] = num
                    k += 1
                for key in curr:
                    if key in found:
                        curr[key].append(found[key])
                    else:
                        curr[key].append(0)
            k += 1
        for key in curr:
            vals_per_drop[key].append(curr[key])
            curr[key] = []
        print vals_per_drop

parseLog("../mobile_logs/log_mult.txt", ["demo1.Nqueens", "demo1.Fibonacci"])
