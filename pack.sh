#!/usr/bin/env bash

sbt universal:packageBin
cp -f target/universal/make_csv-1.0.0.zip .\
