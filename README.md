javac -cp "lib/*" -d out -encoding UTF-8 src/client/*.java src/server/*.java src/server/commands/*.java src/server/util/*.java src/common/commands/*.java src/common/data/*.java src/common/network/*.java

java -cp out server.Server 8080 test_0.csv

java --enable-native-access=ALL-UNNAMED -cp "out;lib/*" client.Client localhost 8080             
