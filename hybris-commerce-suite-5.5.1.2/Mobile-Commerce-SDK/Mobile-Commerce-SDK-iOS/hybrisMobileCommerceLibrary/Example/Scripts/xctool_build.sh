#!/bin/bash
set -v #echo on

# Setup
cd hybrisMobileCommerceLibrary/Example
export http_proxy='http://proxy:8080'
git config --global http.proxy http://proxy:8080
http_proxy='http://proxy:8080' brew install xctool
bundle
pod install
unset http_proxy
git config --global --unset core.gitproxy

#needed for instruments access...
#security unlock-keychain -p Hybris
#security unlock-keychain -p hybris1234

#BUILD scheme
xctool \
    -workspace hybrisMobileCommerceLibrary.xcworkspace \
    -scheme hybrisMobileCommerceLibrary \
    -configuration Release \
    -sdk iphonesimulator \
    GCC_PREPROCESSOR_DEFINITIONS="BUILD_USER_ID=$HOSTNAME" \
    build