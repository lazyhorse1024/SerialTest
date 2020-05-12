java -jar signapk.jar platform.x509.pem platform.pk8 ../bin/SerialTest.apk SerialTest.apk
move SerialTest.apk ../bin/SerialTest.apk
adb remount
adb install -r ../bin/SerialTest.apk

pause
