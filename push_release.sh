#!/bin/bash

if [ "${1}x" == "x" ]; then echo "Must pass new version"; exit 1; fi

sed -ri '0,/(<version>)[0-9]+\.[0-9]+\.[0-9]+(<\/version>)/s//\1'"$1"'\2/' pom.xml

git add pom.xml

git commit --allow-empty -m 'v'"${1}"' Release'

git tag -a -m 'v'"${1}"' Release' "${1}"

git push --tags origin dev
