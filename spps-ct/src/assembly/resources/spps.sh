#!/bin/bash

HOME=.

$JAVA_HOME/bin/java -cp "$HOME/libs/*" -Dlog4j.configurationFile="$HOME/conf/log4j.xml" de.elomagic.spps.shared.SimpleCrypt $1 $2 $3 $4 $5 $6 $7 $8 $9
