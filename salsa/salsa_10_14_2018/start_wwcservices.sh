#!/bin/sh
java -cp salsa_heat.jar:. -Dnetif=eth0 wwc.naming.WWCNamingServer &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 5050 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 4040 &

#java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho -Dual=rmsp://osl-server1.cs.illinois.edu:5050/myecholoc -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping &

# Run GC service
#if [ "$#" -eq 2 ]
#then
#	java -cp salsa_heat.jar:. gc.serverGC.SServerPRID 40 128.174.244.87:5050 192.17.148.132:4040 & 
#else
#	echo "Incorrect number of arguments to start garbage collector."
#fi
