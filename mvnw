#!/bin/bash
# Maven Wrapper Script
# This script downloads and runs Maven if it's not already available

MAVEN_VERSION="3.9.4"
MAVEN_HOME="$HOME/.m2/wrapper/maven-$MAVEN_VERSION"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz"

# Check if Maven is already installed in wrapper directory
if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven $MAVEN_VERSION..."
    mkdir -p "$HOME/.m2/wrapper"
    cd "$HOME/.m2/wrapper"
    
    # Download Maven
    curl -O "$MAVEN_URL"
    
    # Extract Maven
    tar -xzf "apache-maven-$MAVEN_VERSION-bin.tar.gz"
    mv "apache-maven-$MAVEN_VERSION" "maven-$MAVEN_VERSION"
    
    # Clean up
    rm "apache-maven-$MAVEN_VERSION-bin.tar.gz"
    
    echo "Maven $MAVEN_VERSION installed successfully!"
fi

# Run Maven with all arguments
"$MAVEN_HOME/bin/mvn" "$@"
