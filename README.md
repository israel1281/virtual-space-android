# Virtual Space Android App

A minimal but functional Android Virtual Space application that provides app sandboxing, multi-account convenience, and testing capabilities through application-level virtualization.

## Features

- **App Sandboxing**: Isolate third-party apps in virtual environments
- **Multi-Account Support**: Run multiple instances of the same app
- **Testing Environment**: Safe testing of APKs without affecting the host system
- **Educational Research**: Study app behavior in controlled environments

## Architecture

### Core Components

- **VirtualCore**: Central management system for virtual apps
- **VirtualEnvironment**: Isolated file system and runtime environment
- **VirtualAppLoader**: Dynamic APK loading using DexClassLoader
- **VirtualService**: Background service for virtual app management

### Security Features

- Isolated data directories per virtual app
- Sandboxed execution environment
- Permission isolation from host system
- Controlled resource access

## Build Instructions

### Local Build
```bash
./gradlew assembleRelease
```

### GitHub Actions
Push to main branch to trigger automatic APK build. The APK will be available as an artifact in the Actions tab.

## Usage

1. Launch Virtual Space app
2. Tap "Install APK" to add third-party APKs
3. Select installed virtual apps to launch them in isolated environments
4. Each virtual app runs independently with its own data space

## Technical Implementation

The app uses Android's DexClassLoader for dynamic APK loading and creates isolated environments using:
- Separate data directories
- Custom ClassLoader hierarchy  
- Virtualized Android API calls
- Sandboxed file system access

## Legal Notice

This application is designed for legitimate purposes including:
- App development and testing
- Educational research
- Multi-account convenience
- Security analysis in controlled environments

Users are responsible for complying with all applicable laws and terms of service.
