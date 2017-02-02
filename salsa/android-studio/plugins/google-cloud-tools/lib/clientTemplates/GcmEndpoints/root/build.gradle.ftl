dependencies {
    // Make sure that you have installed "Extras -> Google Play services" component in Android SDK Manager
    compile 'com.google.android.gms:play-services-gcm:8.3.0'
    compile project(path: ':${moduleName}', configuration: 'android-endpoints')
    compile 'com.google.code.findbugs:jsr305:2.0.1'
}