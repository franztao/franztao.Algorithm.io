:: Copyright 2017, Gurobi Optimization, Inc.
::
:: Java run script for Windows.  Compiles and runs the Java examples:
::
:: runjava.bat Mip1 (for Mip1.java)
:: runjava.bat Mip2 ../data/p0033.mps (for Mip2.java)
::
:: Be sure to set your %PATH% so that java and javac can be found
::
javac -d . -classpath ../../lib/gurobi.jar;. ../java/%1.java
java -classpath ../../lib/gurobi.jar;. %1 %2
