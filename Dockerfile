FROM maven:3-openjdk-15-slim
WORKDIR usr/ipv4_addresses_counter
COPY . .
RUN mvn test
RUN mvn dependency:go-offline
ENTRYPOINT ["java", "-Xmn1g", "-Xmx1280m", "-cp", "./target/classes", "ru.snktn.ipv4AddressesCounter.Main", "addresses"]
