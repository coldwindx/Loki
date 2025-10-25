g++ -shared -fPIC -o native.dll \
    -I"D:/environment/jdk-21.0.4/include" \
    -I"D:/environment/jdk-21.0.4/include/win32" \
    -I"./include" \
    ./src/com_coldwindx_plugin_NativeLib.cpp