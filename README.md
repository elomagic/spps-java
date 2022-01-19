# spps - Simple Password Protection Solution for Java 

---

[![GitHub tag](https://img.shields.io/github/tag/elomagic/spps-java.svg)](https://GitHub.com/elomagic/spps-java/tags/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://img.shields.io/travis/com/elomagic/spps-java)](https://travis-ci.com/github/elomagic/spps-java)
[![Coverage Status](https://coveralls.io/repos/github/elomagic/spps-java/badge.svg)](https://coveralls.io/github/elomagic/spps-java)
[![GitHub issues](https://img.shields.io/github/issues-raw/elomagic/spps-java)](https://github.com/elomagic/spps-java/issues)

The SPPS is a lightweight solution to protect / hide your password or anything else from your code.

## Table of Contents

- [Features](#features)
- [Concept](#concept)
- [Using the library](#using-the-library)
  * [Maven](#Maven)
  * [JAR libraries](#jar-libraries)
- [Using the API](#using-the-api)
  * [Create a private key file](#create-a-private-key-file)
  * [Encrypt a secret](#encrypt-a-secret)
  * [Decrypt a secret](#decrypt-a-secret)
  * [Apache Tomee Password Cipher](#apache-tomee-password-cipher)
  * [Hibernate C3PO Connection Provider](#hibernate-c3po-connection-provider)
- [WebEncryption Tool](#webencryption-tool)
- [Shell Tool](#shell-tool)
- [Migration](#migration)
- [Contributing](#contributing)

## Features

* AES 256 GCM en-/decryption
* Bouncy Castle support
* Apache Shiro support
* Apache Tomee - DataSource password cipher support
* Hibernate ORM - C3PO connection provider support
* Cross programming languages support
  * [Java](https://github.com/elomagic/spps-java)
  * [Python](https://github.com/elomagic/spps-py)
  * [Node.js](https://github.com/elomagic/spps-npm)

## Concept

This solution helps you to accidentally publish secrets unintentionally by splitting the secret into an encrypted part and a private key.
The private key is kept separately from the rest, in a secure location for the authorized user only.

The private key is randomized for each user on each system and is therefore unique. This means that if someone has the encrypted secret,
they can only read it if they also have the private key. You can check this by trying to decrypt the encrypted secret with another user or another system. You will not succeed.

A symmetrical encryption based on the AES-GCM 256 method is used. See also https://en.wikipedia.org/wiki/Galois/Counter_Mode

By default, the private key is stored in a file "/.spps/settings" of the user home folder.

Keep in mind that anyone who has access to the user home or relocation folder also has access to the private key !!!!

## Using the library

### Maven

Add following dependency to your project. Replace the value of the attribute ```artefactId``` according to the used 
crypto engine in your project.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/maven-v4_0_0.xsd">

    ...

    <dependencies>
        <dependency>
            <groupId>de.elomagic</groupId>
            <artifactId>[spps-jbc | spps-jshiro]</artifactId>
            <version>2.0.0</version>
        </dependency>
    </dependencies>
    
    ...
    
</project>
```

### JAR libraries

Instead of using Maven, put all these JAR files in the latest version into the lib folder of your application like Tomee.

* SPPS with Bouncy Castle support
  * spps-jbc-2.x.x.jar - https://github.com/elomagic/spps-jbc
  * spps-shared-2.x.x.jar - https://github.com/elomagic/spps-jbc
  * bcprov-jdk15on-170.jar - https://www.bouncycastle.org/latest_releases.html
  * log4j-core-2.x.x.jar - https://logging.apache.org/log4j/2.x/download.html
  * log4j-api-2.x.x.jar - https://logging.apache.org/log4j/2.x/download.html
  * disruptor-3.x.x.jar - https://github.com/LMAX-Exchange/disruptor/releases

* SPPS with Apache Shiro support
  * spps-jshiro-2.x.x.jar - https://github.com/elomagic/spps-jbc
  * spps-shared-2.x.x.jar - https://github.com/elomagic/spps-jbc
  * shiro-all-1.x.0.jar - https://shiro.apache.org/download.html#latestBinary
  * log4j-core-2.x.x.jar - https://logging.apache.org/log4j/2.x/download.html
  * log4j-api-2.x.x.jar - https://logging.apache.org/log4j/2.x/download.html
  * disruptor-3.x.x.jar - https://github.com/LMAX-Exchange/disruptor/releases

## Using the API

### Create a private key file

To create a private key via shell, you need the console binary package which can be downloaded 
[here](../../releases).

#### Create a private in your home folder:

As a feature, if the private key does not exist, it will be created automatically when you encrypt a new secret!

If you want to create it manually then continue with the following steps.

Enter following command in your shell:

```bash
spps -CreatePrivateKey
```

The settings file ```'~/.spps/settings'``` in your home folder will look like:

```properties
key=5C/Yi6+hbgRwIBhXT9PQGi83EVw2Oe6uttRSl4/kLzc=
relocation=
```

As a feature, if the private key does not exist, it will be created automatically when you encrypt a new secret!

#### Alternative, create a private key file on a removable device:

Enter following command in your shell:

```bash
spps -CreatePrivateKey -Relocation /Volumes/usb-stick
```

The settings file ```'~/.spps/settings'``` in your home folder will look like:

```properties
key=
relocation=/Volumes/usb-stick
```

...and in the relocation folder look like:

```properties
key=5C/Yi6+hbgRwIBhXT9PQGi83EVw2Oe6uttRSl4/kLzc=
relocation=
```

### Encrypt a secret

To encrypt a secret via shell, you need the console binary package which can be downloaded 
[here](../../releases).

Important Note: Usually you do not need to execute this command unless you want to create a new private key. 
Remember, secrets which are already encrypted with the old key cannot be decrypted with the new key!

Enter following command in your shell:

```bash
spps -Secret

Enter secret to encrypt: ********* 
```

Output should look like:
```
{MLaFzwpNyKJbJSCg4xY5g70WDAKnOhVe3oaaDAGWtH4KXR4=}
```

### Decrypt a secret

Secrets can only be decrypted by API.

```java
import de.elomagic.spps.shared.SimpleCryptFactory;

class Sample {

    void testEncryptDecryptWithString() throws Exception {
        String value = "My Secret";
        
        SimpleCryptProvider provider = SimpleCryptFactory.getInstance(); 

        String encrypted = provider.encrypt(value);

        System.out.println("My encrypted secret is " + encryptedSecret);

        String decrypted = provider.decryptToString(encrypted);

        System.out.println("...and my secret is " + decrypted);
    }
    
}
```

### Using an alternative settings file instead of the default 

*Supported since version 1.1.0*

The method ```SimpleCryptFactory.getInstance().setSettingsFile([file])``` can be used to set application wide an 
alternative settings file instead of "~/.spps/settings" in the users home folder.

```java
import de.elomagic.spps.shared.SimpleCryptFactory;

import java.nio.file.Paths;

class Sample {

    void testEncryptDecryptWithString() throws Exception {

        SimpleCryptProvider provider = SimpleCryptFactory.getInstance();

        provider.setSettingsFile(Paths.get("./configuration/privateKey"));

        String decrypted = provider.decryptToString(SimpleCrypt.encrypt("secret"));
        System.out.println("...and my secret is " + decrypted);
        
    }

}
```

### Apache Tomee Password Cipher

*Supported since version 1.3.0*

SPPS provides another password cipher implementation.

Set ```spps``` as password cipher and the encrypted secret in property ```password``` WITHOUT the surrounding brackets
in the ```[tomme_inst_folder]\conf\tomee.xml``` file.

Note if your Tomee run with a different account then yours. In this case you have to encrypt your secret in context of 
the account which will run the service in the future. One solution idea is to deploy the spps-wet web application tool.
This app provides a simple UI for encryption secrets.  

For some unknown reason, Tomee removes the closing bracket from the encrypted SPPS secret when try to decrypt, so we 
have to remove the brackets in the ```tomee.xml``` file.

#### Example resource in the tomee.xml

```xml
<Resource id="MySQL Database" type="DataSource">
    #  MySQL example
    #
    #  This connector will not work until you download the driver at:
    #  https://dev.mysql.com/downloads/connector/j/

    JdbcDriver  com.mysql.jdbc.Driver
    JdbcUrl jdbc:mysql://localhost/test
    UserName    test

    # Use "spps" as password cipher and remove the brackets from the encrypted password.
    Password    1K2UqEGtaz1xktKScCvRLHmPjNe1tE51Clt+2prUn/nonA7yvF0bhw==
    PasswordCipher spps
</Resource>
```

For more information see https://tomee.apache.org/latest/docs/datasource-password-encryption.html or

### Hibernate C3PO Connection Provider

*Supported since version 2.0.0*

Add Maven artefact as described [above](#maven) or add dependencies to your lib folder of your application as described 
in chapter [Requirements](#jar-libraries).

Supported attributes in your ```persistence.xml``` are:
* hibernate.connection.driver_class
* hibernate.connection.url
* hibernate.connection.username
* hibernate.connection.password

## WebEncryption Tool

*Supported since version 2.0.0*

The tool offers the feature to generate passwords in the service context of the application server.
Just deploy the latest version of the tool to your application server and open web-browser with the
URL ```[BASE_URL]/spps-wet-[VERSION]```, enter your secret, press the "Encrypt" button and the encrypted secret will be generated
and presented.

## Shell Tool

*Supported since version 2.0.0*

Another tool for creating a private key or to encrypt a secret in your user context is the shell tool and can 
be downloaded [here](../../releases).

To get an overview of support commands just enter following command:

```bash
spps
```

## Migration

In summary, please use the class ```de.elomagic.spps.shared.SimpleCryptFactory``` instead 
```de.elomagic.spps.SimpleCrypt``` in the future.  

### Classpath changed

Classpath of class ```SimpleCrypt``` was changed from ```de.elomagic.spps.[bc|shiro]``` to ```de.elomagic.spps.shared```.

### Methods removed

For safety relevant reasons some methods were removed from class ```SimpleCrypt```.

* ```boolean init()```
* ```String encrypt(String decrypted)```
* ```String decryptToString(String encryptedBase64)```

## Contributing

Pull requests and stars are always welcome. For bugs and feature requests, [please create an issue](../../issues/new).

### Versioning

Versioning follows the semantic of [Semantic Versioning 2.0.0](https://semver.org/)

### Releasing new version / hotfix (Only for users who have repository permissions)

#### Releasing new version / hotfix

Execute following steps:

```bash
mvn clean install release:prepare -P release
mvn release:perform -P release
```

## License

Copyright © 2022, [C. Rambow](https://github.com/elomagic).
Released under the [Apache License, Version 2.0](LICENSE).

