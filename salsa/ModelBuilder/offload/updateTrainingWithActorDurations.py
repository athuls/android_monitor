import sys

fileName = sys.argv[1]
newFileName = fileName + "_actorDurationsAppended"
newfp = file(newFileName, "w")
with open(fileName, "r") as fp:
	for line in fp:
		drainSize = line.rstrip()
		appendedActorDurations = "0"
		for i in range(0, 23):
			appendedActorDurations += ",0"
		appendedActorDurations += "," + drainSize + "\n"
		newfp.write(appendedActorDurations)
newfp.close()
