# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres 
to [Semantic Versioning](https://semver.org/).

## [2.0.0] - Upcomming

### Removed

* For safety relevant reasons some methods ```init()```, ```encrypt(String)``` and ```decryptToString(String)```
  were removed from class ```SimpleCrypt```.

### Changed

* Project [spps-jbc]https://github.com/elomagic/spps-jbc and [spps-jshiro]https://github.com/elomagic/spps-jshiro merged
  into this project as modules
* Classpath of class ```SimpleCrypt``` was changed from ```de.elomagic.spps.[bc|shiro]``` to
  ```de.elomagic.spps.shared```.
* Format of the settings file ```~/.spps/settings``` normalized.

### Deprecated

* Class ```de.elomagic.spps.SimpleCrypt``` is deprecated

### Added

* Web application tool "spps-wet" for encrypting secret in application server context.
* Command line tool "spps-shell" for encrypting secret in the user context.
* DataSource password cipher support for Apache Tomee added 
* C3PO connection provider support for Hibernate ORM added 

## Previous Releases

* For spps-jbc version 1.x see https://github.com/elomagic/spps-jbc/blob/main/CHANGELOG.md
* For spps-jshiro version 1.x see https://github.com/elomagic/spps-jshiro/blob/main/CHANGELOG.md
