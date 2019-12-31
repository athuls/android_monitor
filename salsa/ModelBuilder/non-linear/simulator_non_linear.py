import numpy as np
import sys


pidle = 0.717
#pidle = 1.485
p1 = 3.23
#p1 = 3.13
k= 562.26
#k= 271.64
count = int(sys.argv[1])
default_range = (20,100)
error = 0
print("V1,V2")
while count >0:
	if error == 1:
		error = 0
		duration = np.random.uniform(*new_range)
	else:
		duration = np.random.uniform(*default_range)

	if duration < 40:
		new_range = (20,40)
		#tmean = ((pidle - 1.0*p1)/pidle) * duration + k
		#tmean = ((pidle - 0.5*p1)/pidle) * duration + k
		#tmean = ((pidle - 0.25*p1)/pidle) * duration + k
		tmean = ((pidle - 0.25*p1)/pidle) * duration + k
	elif duration >=40 and duration <=45:
		new_range = (40,45)
		#tmean = ((pidle - 3.0*p1)/pidle) * duration + k
		#tmean = ((pidle - 0.25*p1)/pidle) * duration + k
		#tmean = ((pidle - 1.25*p1)/pidle) * duration + k
		tmean = ((pidle - 0.75*p1)/pidle) * duration + k
	else:
		continue
	#else:
	#	new_range = (60,100)
	#	tmean = ((pidle - 2.0*p1)/pidle) * duration + k
	tsd = 1000/(duration)
	#t = tmean
	t = np.random.normal(tmean, tsd)
	if t < duration:
		error = 1
		#print duration
		continue
#	elif duration >=75:
	else:
		count -=1
		print str(duration) + "," + str(t)
