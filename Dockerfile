FROM maven:3.8.1-openjdk-15-slim
WORKDIR usr/ipv4_addresses_counter
COPY . .
RUN mvn clean install
CMD ls