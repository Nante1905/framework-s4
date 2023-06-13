set path=".\test-framework-build"

if exist %path% rmdir /S /Q %path%

mkdir %path% %path%\WEB-INF %path%\WEB-INF\classes %path%\WEB-INF\lib %path%\views

copy .\views %path%\views

copy .\*.jsp %path%

copy .\WEB-INF\lib %path%\WEB-INF\lib

"c:\Windows\System32\xcopy.exe" /s .\WEB-INF\classes %path%\WEB-INF\classes

cd %path%
"C:\Program Files\Java\jdk-18.0.1.1\bin\jar.exe" cvf test-framework.war .
copy ".\test-framework.war" "C:\Program Files\Apache Software Foundation\Tomcat 10.0\webapps"
cd ..
rmdir /S %path%