#!/usr/bin/env bash

sbt clean update assembly
cp -f target/scala-2.11/make_csv-assembly-1.0.0.jar .\
