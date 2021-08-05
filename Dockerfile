FROM maven:3.8.1-openjdk-15-slim
RUN ls
CMD java -Xmx2560m -Xmn2g -jar target/ipv4_addresses_counter-1.0-SNAPSHOT.jar file