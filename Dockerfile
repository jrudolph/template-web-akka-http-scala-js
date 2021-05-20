ARG BASE_IMAGE_TAG
FROM hseeberger/scala-sbt:${BASE_IMAGE_TAG:-8u282_1.5.2_2.13.6} AS builder

RUN mkdir -p /tmp/project/project
WORKDIR /tmp/project

# prime sbt for cache
COPY project/build.properties /tmp/project/project/
RUN sbt exit

# warmup caches and create dependency jar, if build doesn't change, we will use caches in the next run
COPY build.sbt /tmp/project/
COPY project/* /tmp/project/project/
# for some reason we need frontend sources to make it work (which is fine because they almost never change)
COPY frontend /tmp/project/frontend

RUN sbt update "show web/assemblyPackageDependency"

COPY . /tmp/project/

RUN sbt "show web/assembly"

FROM adoptopenjdk/openjdk11:debian

# install additional runtime dependencies here
#RUN apt-get update && \
#    apt-get install --no-install-recommends -y <apt-package-name> && \
#    rm -rf /var/lib/apt/lists/*

COPY --from=builder /tmp/project/web/target/scala-2.13/deps.jar /deps.jar
COPY --from=builder /tmp/project/web/target/scala-2.13/app.jar /app.jar

EXPOSE 8080/tcp

CMD ["java", "-verbose:gc", "-cp", "/app.jar:/deps.jar", "example.akkawschat.web.Boot"]