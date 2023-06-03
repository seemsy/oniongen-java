# oniongen-java
Generate custom v3 .onion addresses using Java.

## About
Generates v3 .onion addresses until an address matches a regex specified by the user. 
Matching addresses have their corresponding keys and hostname exported to the same folder the application is executed in. 

## Run latest release
- [Download and unzip latest release of oniongen-java](https://github.com/seemsy/oniongen-java/releases)
- [Download and install latest version of Java for your OS](https://www.java.com/en/download/manual.jsp)
- [Run oniongen.jar from unzipped contents.](#usage)


## Usage

```
java -jar oniongen.jar <regex> 
```

## Examples
**Matching an address starting with "test"**
```
java -jar oniongen.jar "^test.*$"
```

**Matching an address ending with "notbad". Keep in mind that v3 addresses must end in [a,y,q,i]d)**
```
java -jar oniongen.jar ".*notbad$"
```


## References & Libraries


[Bouncy Castle Crypto API](https://www.bouncycastle.org/)

[oniongen-go](https://github.com/rdkr/oniongen-go)

[oniongen-hs](https://github.com/ciehanski/oniongen-hs)

[Tor v3 spec](https://github.com/torproject/torspec/blob/main/rend-spec-v3.txt)

##

  Inspired by the existing plethora of Tor v3 .onion generators existing in nearly every language except Java - until now.
