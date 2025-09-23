# Build stage
FROM mcr.microsoft.com/openjdk/jdk:21-azurelinux AS builder

# Install Maven as a fallback if the wrapper fails
RUN yum update -y && \
    yum install -y wget && \
    wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz -O /tmp/maven.tar.gz && \
    tar -xf /tmp/maven.tar.gz -C /opt/ && \
    ln -s /opt/apache-maven-3.9.9/bin/mvn /usr/local/bin/mvn && \
    rm /tmp/maven.tar.gz

WORKDIR /app
COPY . .

# Fix maven wrapper permissions and run build
RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Runtime stage
FROM mcr.microsoft.com/openjdk/jdk:21-azurelinux
WORKDIR /app
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]