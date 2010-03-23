#-ix -org.netbeans.*
java -cp $XMOS_ROOT/infr_libs_java/com.xmos.libs/jars/emma.jar emma merge -out $XMOS_ROOT/verif_tests_sw/Tools/xta/coverage.dat `find . -name coveragedata -exec echo -in {} \;`
java  -cp $XMOS_ROOT/infr_libs_java/com.xmos.libs/jars/emma.jar emma report -in coverage.dat  -sp $XMOS_ROOT/infr_libs_java/com.xmos.libs/src:$XMOS_ROOT/tools_xta/com.xmos.xta.core/src -r html 
