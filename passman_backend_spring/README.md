# passman
> Password manager backend server + H2 database

This is the core backend server for the password manager.

## Requirements

JDK 21  
OpenSSL

## Installation

Create required keys:
```
openssl genpkey -algorithm RSA -out private.key -pkeyopt rsa_keygen_bits:2048 # create a private key
openssl req -new -x509 -key private.key -out cert.crt -days 3650 # fill in your details
openssl pkcs12 -export -out /src/main/java/resources/keystore/{name}.p12 -inkey private.key -in cert.crt -name {name} -passout pass:{password} # replace name and password
```

To build project
```
./graldew build
```

Test project
```
./graldew test
```

Run project

```
./graldew bootRun
```

## Features

- JUnit: For writing tests.
- Lombok: Simplifies code by auto-generating boilerplate code.
- JCA/JCE: Handles cryptographic operations.
- Jackson-databind: Parses JSON data.
- Google Guava: Caches login attempts to mitigate brute force attacks.
- JJWT: Manages JSON Web Tokens (JWTs) for secure session handling.
- Hibernate-validator: Validates user inputs and ensures they meet specific rules.
 



## Author

Innar Viinamäe