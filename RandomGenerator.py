import math
import random
import sys

nActors = int(sys.argv[1])

def ftime(n):
	if n > 4:
		return 55
	elif n == 4:
		return 95
	elif n == 3:
		return 115
	elif n == 2:
		return 135
	elif n == 1:
		return 155
	else :
		return 175

def entry(output):
	actorCount = random.randint(1,nActors)
	time = ftime(actorCount)
	actors = range(nActors)
	selectActors = random.sample(actors,actorCount)
	selectActors.sort()
	maxTime = 0
	actorLoc = 0
	for i in range(nActors):
		if actorCount > 0:
			if actorLoc < actorCount:
				if i == selectActors[actorLoc]:
					time_n = time + random.randint(0,10)
					if time_n > maxTime:
						maxTime = time_n
					output += str(time_n)+"\t"
					actorLoc += 1
				else:
					output += str(0)+"\t"
			else:
				output += str(0)+"\t"
		else:
			output += str(0)+"\t"
	output +=str(maxTime)
	return output

def main():

	batteryPct = 0.98
	top_cmd = ''
	for x in range(nActors):
		top_cmd += "Actor"+str(x+1)+"\t"
	top_cmd += "batteryPct"
		
	print (top_cmd)
	for x in range(95):
		output = ''
		output= entry(output)
		# output += str(batteryPct)
		print(output)
		batteryPct -= 0.01
		




if __name__ == '__main__':
	main()