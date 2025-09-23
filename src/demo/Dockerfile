# Build stage
FROM mcr.microsoft.com/openjdk/jdk:21-azurelinux AS builder

WORKDIR /app
COPY . .

# Fix maven wrapper permissions and run build
RUN chmod +x ./mvnw && \
    ./mvnw clean package -DskipTests

# Runtime stage
FROM mcr.microsoft.com/openjdk/jdk:21-azurelinux
WORKDIR /app
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]