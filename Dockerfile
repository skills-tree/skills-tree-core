FROM openjdk:11-slim
MAINTAINER ilyavy

ENV JVM_OPTIONS="-Xmx128m -Xms64m -Xss512k -XX:MaxMetaspaceSize=128m -XX:+UseG1GC" \

ADD target/skills-update.jar app.jar

# Documentation ref https://docs.docker.com/engine/reference/builder/#entrypoint
ENTRYPOINT exec java -jar -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError \
            -XX:HeapDumpPath="app-heap-dump.hprof" $JVM_OPTIONS app.jar
