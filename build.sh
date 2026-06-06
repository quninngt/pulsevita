#!/bin/bash
set -e

JAVA_HOME=/home/ubuntu/jdk-17.0.2
export JAVA_HOME

# Use system-installed Gradle directly (avoids wrapper network download)
GRADLE_HOME=/home/ubuntu/gradle/gradle-8.5
export GRADLE_HOME

echo "🔨 Building PulseVita APK..."
$GRADLE_HOME/bin/gradle assembleDebug

echo ""
echo "✅ Build successful!"
echo "📦 APK: app/build/outputs/apk/debug/app-debug.apk"
