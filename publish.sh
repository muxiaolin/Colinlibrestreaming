#!/usr/bin/env bash

gradlew clean build bintrayUpload -PbintrayUser=xiaoke -PbintrayKey=b67ceda6d33f513aa92240e5ad48ebd4c09396ee -PdryRun=false