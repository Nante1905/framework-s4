jar cvf framework-dist.jar -C bin .
copy "framework-dist.jar" "./../test-framework/WEB-INF/lib"
del "framework-dist.jar"