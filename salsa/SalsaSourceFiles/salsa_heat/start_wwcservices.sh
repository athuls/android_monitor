#!/bin/sh
java -cp salsa_heat.jar:. -Dnetif=eth0 wwc.naming.WWCNamingServer &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 5050 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 4040 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 6060 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 7070 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 8080 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 9090 &
java -cp salsa_heat.jar:JJILCore.jar:android.jar:JJIL-J2SE.jar:JJIL-Android.jar:. -Dnetif=eth0 -Dnodie wwc.messaging.Theater 9595 &

java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho -Dual=rmsp://osl-server1.cs.illinois.edu:5050/myecholoc -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho1 -Dual=rmsp://osl-server1.cs.illinois.edu:4040/myecholoc1 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping1 &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho2 -Dual=rmsp://osl-server1.cs.illinois.edu:6060/myecholoc2 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping2 &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho3 -Dual=rmsp://osl-server1.cs.illinois.edu:7070/myecholoc3 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping3 &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho4 -Dual=rmsp://osl-server1.cs.illinois.edu:8080/myecholoc4 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping4 &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho5 -Dual=rmsp://osl-server1.cs.illinois.edu:9090/myecholoc5 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping5 &
java -cp salsa_heat.jar:. -Duan=uan://osl-server1.cs.illinois.edu:3030/myecho6 -Dual=rmsp://osl-server1.cs.illinois.edu:9595/myecholoc6 -Dnetif=eth0 -Dnodie examples.ping.EchoAgent uan://osl-server1.cs.illinois.edu:3030/myping6 &

# Run GC service
#if [ "$#" -eq 2 ]
#then
#	java -cp salsa_heat.jar:. gc.serverGC.SServerPRID 40 128.174.244.87:5050 192.17.148.132:4040 & 
#else
#	echo "Incorrect number of arguments to start garbage collector."
#fi
