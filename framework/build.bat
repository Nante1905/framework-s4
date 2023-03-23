jar cvf framework-dist.jar -C WEB-INF/classes .
copy "framework-dist.jar" "./../test-framework/WEB-INF/lib"
del "framework-dist.jar"