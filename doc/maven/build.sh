clean package -Dmaven.test.skip=true -Pprod
clean package -Dmaven.test.skip=true -Ptest
clean package -Dmaven.test.skip=true -Pdev

mvn -f pom.xml clean package -DskipTests docker:build