FROM maven:3.8.1-openjdk-15-slim
WORKDIR counter
COPY . .
CMD ls