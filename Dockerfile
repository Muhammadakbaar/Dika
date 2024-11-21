# Gunakan image OpenJDK resmi sebagai image dasar
FROM openjdk:17-jdk-alpine

# Setel direktori kerja di dalam container
WORKDIR /app

# Salin file JAR proyek ke dalam container di /app
COPY target/test-0.0.1-SNAPSHOT.jar app.jar

# Buat port 8080 tersedia untuk dunia luar container ini
EXPOSE 8080

# Jalankan file JAR
ENTRYPOINT ["java", "-jar", "app.jar"]