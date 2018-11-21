#export CLASSPATH=./classes:$CLASSPATH 
export CLASSPATH=.:$CLASSPATH 
#DIST=./classes
VERSION=1.1.5

echo "SALSA Build Script v0.2.2"
echo "Please make sure the current directory is in your CLASSPATH"
echo ""

#if [ -d $DIST ]; then
#                rm -rf $DIST
#fi
#echo "Making dir: "$DIST
#mkdir $DIST
echo ""

echo "Compiling SALSA Compiler Code"
#javac -Xlint:none -d $DIST `find salsac/ | grep "java$"`
javac -Xlint:none `find salsac/ | grep "java$"`
echo ""

echo "Compiling Language Package"
echo "java source..."
#javac -Xlint:none -d $DIST `find salsa/ | grep "java$"`
javac -Xlint:none `find salsa/ | grep "java$"`
echo ""

echo "Compling WWC Package"
echo "java source..."
#javac -Xlint:none -d $DIST `find wwc/ | grep "java$"`
javac -Xlint:none `find wwc/ | grep "java$"`
echo ""

echo "Compling androidsalsa Package"
echo "java source..."
#javac -Xlint:none -cp android.jar:. -d $DIST `find androidsalsa/ | grep "java$"`
javac -Xlint:none -cp android.jar:. `find androidsalsa/ | grep "java$"`
echo ""

echo "Compiling GC Package"
echo "java source..."
#javac -Xlint:none -d $DIST `find gc/ | grep "java$"`
javac -Xlint:none `find gc/ | grep "java$"`
echo ""

echo "Compiling tests"
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find gctest/ | grep "salsa$"`
echo "java source..."
#javac -Xlint:none -d $DIST `find gctest/ | grep "java$"`
javac -Xlint:none `find gctest/ | grep "java$"`
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find tests/ | grep "salsa$"`
echo "java source..."
#javac -Xlint:none -d $DIST `find tests/ | grep "java$"`
javac -Xlint:none `find tests/ | grep "java$"`
echo ""

echo "Compiling Examples"
echo "salsa source..."
java -Dsilent salsac.SalsaCompiler `find examples/ | grep "salsa$"`
echo "java source..."
#javac -Xlint:none -d $DIST `find examples/ | grep "java$"`
javac -Xlint:none -cp android.jar:JJILCore.jar:JJIL-J2SE.jar:JJIL-Android.jar:. `find examples/ | grep "java$"`
echo ""

echo "Compiling image detection"
echo "java source..."
javac -Xlint:none -cp android.jar:JJILCore.jar:JJIL-J2SE.jar:JJIL-Android.jar:. `find image_detection_RSH/ | grep "java$"`
echo ""

echo "Generating jar file..."
#cd $DIST
#jar cvf ../salsa$VERSION.jar `find wwc` `find salsa` `find salsac` `find gc` `find androidsalsa` `find examples`
#cd ..
jar cf salsa$VERSION.jar `find wwc` `find salsa` `find salsac` `find gc` `find androidsalsa` `find examples` `find image_detection_RSH`

cp salsa$VERSION.jar salsa_heat.jar

echo ""
echo "Finished!"
