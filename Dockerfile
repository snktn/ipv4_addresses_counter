FROM maven:3.8.1-openjdk-15-slim
COPY *.addr addr_file
RUN mvn clean install
CMD java -Xmx2560m -Xmn2g -jar target/ipv4_addresses_counter-1.0-SNAPSHOT.jar addr_file
