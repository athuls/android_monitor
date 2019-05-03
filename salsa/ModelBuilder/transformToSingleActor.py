import sys

with open(sys.argv[1]) as fp:
	for line in fp:
		rows = line.split(",")	
		total_count = 0
		for i in range(0, len(rows) - 1):
			actor_count = float(rows[i])
			#if(actor_count > 0):
			#	total_count += 1
			total_count += actor_count
		print(str(total_count)+",",end="")
		for i in range(0,len(rows)-2):
			print("0,",end="")
		print(rows[-1].rstrip()) 
