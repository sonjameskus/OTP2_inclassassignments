FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 \
    libxtst6 \
    libxrender1 \
    libxi6 \
    libxext6 \
    libx11-6 \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*
COPY pom.xml .
COPY . /app
RUN mvn clean package
RUN ls -l /app/src/main/resources/
CMD ["mvn", "test", "javafx:run"]