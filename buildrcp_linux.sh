ROOT=`pwd`
BUILD_DIR=/tmp/xta/linux_x86-64
BUILD_OUTPUT=$ROOT/build.output.linux_x86-64
TARGET=xta.linux_x86-64.zip
ECLIPSE_DIR=/tools/eclipse/eclipse-SDK-3.4.1-linux-gtk-x86_64/
LIB_DOMAIN=infr_libs_java
XTA_DOMAIN=tools_xta

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR/build/plugins/$LIB_DOMAIN
mkdir -p $BUILD_DIR/build/plugins/$XTA_DOMAIN
mkdir -p $BUILD_DIR/build_config

cp -r $ROOT/../$LIB_DOMAIN/* $BUILD_DIR/build/plugins/$LIB_DOMAIN
cp -r $ROOT/../$XTA_DOMAIN/* $BUILD_DIR/build/plugins/$XTA_DOMAIN
cp $ROOT/build.properties.linux $BUILD_DIR/build_config/build.properties

export CLASSPATH=

java -jar $ECLIPSE_DIR/plugins/org.eclipse.equinox.launcher_1.0.101.R34x_v20080819.jar -application org.eclipse.ant.core.antRunner -buildfile $ECLIPSE_DIR/plugins/org.eclipse.pde.build_3.4.1.R34x_v20080805/scripts/productBuild/productBuild.xml -Dbuilder=$BUILD_DIR/build_config -quiet &> $BUILD_OUTPUT

tail -n2 $BUILD_OUTPUT

mkdir -p $ROOT/bin
rm -rf $ROOT/bin/$TARGET
cp -f $BUILD_DIR/build/I.TestBuild/*.zip $ROOT/bin/$TARGET
cd $ROOT/bin
unzip -o $TARGET >> $BUILD_OUTPUT

