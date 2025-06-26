# Stage 1: Build minimal JRE with jlink
FROM eclipse-temurin:21-jdk-alpine-3.21 AS jre-builder

WORKDIR /app

# Copy the application JAR
COPY build/libs/minioService-0.0.1-SNAPSHOT.jar /app/minioService.jar

# Install required tools for jlink and unpacking JAR
RUN apk update && apk add --no-cache tar binutils

# Extract the application JAR
RUN jar xf minioService.jar

# Generate module list needed for the app
RUN jdeps \
    --ignore-missing-deps \
    --print-module-deps \
    -q \
    --recursive \
    --multi-release 21 \
    --class-path "BOOT-INF/lib/*" \
    --module-path "BOOT-INF/lib/*" \
    minioService.jar > modules.info

# Create custom JRE using jlink
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules $(cat modules.info) \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /optimized-jdk-21

# Stage 2: Create minimal runtime image
FROM alpine:latest

ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Copy the custom JRE
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

# Add a non-root user for running the app
ARG APPLICATION_USER=minio
RUN adduser --no-create-home -u 1000 -D $APPLICATION_USER

# Create the application directory and set permissions
RUN mkdir /app && chown -R $APPLICATION_USER /app

# Copy the application JAR
COPY --chown=$APPLICATION_USER:$APPLICATION_USER build/libs/minioService-0.0.1-SNAPSHOT.jar /app/minioService.jar

WORKDIR /app
USER $APPLICATION_USER

EXPOSE 8085

ENV JAVA_OPTS="-Xmx512m"

ENTRYPOINT ["java", "-jar", "/app/minioService.jar"]
