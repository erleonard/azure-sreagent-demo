# Use the official .NET 9 SDK image for building
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /app

# Copy project file and restore dependencies
COPY SreAgentMemoryDemo.csproj .
RUN dotnet restore

# Copy source code and build the application
COPY . .
RUN dotnet publish -c Release -o out

# Use the official .NET 9 runtime image for the final image
FROM mcr.microsoft.com/dotnet/aspnet:9.0
WORKDIR /app

# Copy the published application
COPY --from=build /app/out .

# Expose port 8080 (standard for containerized apps)
EXPOSE 8080

# Set environment variable for ASP.NET Core to listen on port 8080
ENV ASPNETCORE_URLS=http://+:8080

# Run the application
ENTRYPOINT ["dotnet", "SreAgentMemoryDemo.dll"]