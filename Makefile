build-image:
	@echo "Building image..."
	@docker build -t gomobile .

build-apk:
	@echo "Building apk..."
	PWD=$(dir $(realpath $(firstword $(MAKEFILE_LIST))))
	@docker run -v "$(PWD)"/android/app/build:/root/android/app/build  gomobile

build: build-image build-apk

uninstall:
	@echo "Uninstalling previous apk"
	adb uninstall com.docker.android

install: uninstall
	@echo "Installing apk..."
	adb install android/app/build/outputs/apk/main-debug.apk

all: build
