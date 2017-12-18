# Copyright 2017 The Camlistore Authors.

FROM openjdk:8-jdk

MAINTAINER camlistore <camlistore@googlegroups.com>

# To enable running android tools such as aapt
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y lib32z1 lib32stdc++6
# For Go:
RUN apt-get -y --no-install-recommends install curl gcc
RUN apt-get -y --no-install-recommends install ca-certificates libc6-dev git

ENV GOPHER /root

# Get android sdk, ndk, and rest of the stuff needed to build the android app.
WORKDIR $GOPHER
RUN mkdir android-sdk
ENV ANDROID_HOME $GOPHER/android-sdk
WORKDIR $ANDROID_HOME
RUN curl -O https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip
RUN echo '444e22ce8ca0f67353bda4b85175ed3731cae3ffa695ca18119cbacef1c1bea0  sdk-tools-linux-3859397.zip' | sha256sum -c
RUN unzip sdk-tools-linux-3859397.zip
RUN echo y | $ANDROID_HOME/tools/bin/sdkmanager --update
RUN echo y | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-23'
RUN echo y | $ANDROID_HOME/tools/bin/sdkmanager 'build-tools;23.0.3'
RUN echo y | $ANDROID_HOME/tools/bin/sdkmanager 'extras;android;m2repository'
RUN echo y | $ANDROID_HOME/tools/bin/sdkmanager 'ndk-bundle'

# Get gradle. We don't actually need to build the app, but we need it to
# generate the gradle wrapper, since it's not included in the app's repo.
WORKDIR $GOPHER
ENV GRADLE_VERSION 2.10
ARG GRADLE_DOWNLOAD_SHA256=66406247f745fc6f05ab382d3f8d3e120c339f34ef54b86f6dc5f6efc18fbb13
RUN set -o errexit -o nounset \
	&& echo "Downloading Gradle" \
	&& wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
RUN echo "Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check -
RUN echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip
RUN mkdir $GOPHER/bin \
	&& ln --symbolic "${GOPHER}/gradle-${GRADLE_VERSION}/bin/gradle" $GOPHER/bin/gradle

# Get Go stable release
WORKDIR $GOPHER
RUN curl -O https://storage.googleapis.com/golang/go1.9.2.linux-amd64.tar.gz
RUN tar -xzf go1.9.2.linux-amd64.tar.gz
ENV GOPATH $GOPHER
ENV GOROOT $GOPHER/go
ENV PATH $PATH:$GOROOT/bin:$GOPHER/bin

# Get gomobile
RUN mkdir -p src/golang.org/x
WORKDIR src/golang.org/x
RUN git clone https://github.com/silvin-lubecki/mobile.git
RUN go install golang.org/x/mobile/cmd/gomobile

# init gomobile
RUN gomobile init -ndk $ANDROID_HOME/ndk-bundle

# build app
COPY src $GOPHER/src
COPY android $GOPHER/android

WORKDIR $GOPHER/android
RUN gradle wrapper --stacktrace --gradle-version 2.10

CMD ["/root/android/gradlew", "assembleDebug"]
