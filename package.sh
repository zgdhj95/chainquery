git stash
git pull
git stash pop
mvn clean package  -Dmaven.test.skip=true
./killtomcat.sh tomcat-test-server
cp ./target/integratest.war ../tomcat-test-server/webapps/
../tomcat-test-server/bin/startup.sh
