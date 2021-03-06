FROM maven:3-openjdk-15-slim
WORKDIR usr/addr_counter
COPY . .
RUN mvn test
ENTRYPOINT ["java", "-Xmn3g", "-cp", "./target/classes", "ru.snktn.ipv4AddressesCounter.Main", "file"]
