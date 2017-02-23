[![License Apache 2.0](https://img.shields.io/badge/license-Apache%20License%202.0-green.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Java 6.0+](https://img.shields.io/badge/java-6.0%2b-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.igormaznitsa/char-sniffer/badge.svg)](http://search.maven.org/#artifactdetails|com.igormaznitsa|char-sniffer|1.0.0|jar)
[![Maven 3.0.3+](https://img.shields.io/badge/maven-3.0.3%2b-green.svg)](https://maven.apache.org/)
[![PayPal donation](https://img.shields.io/badge/donation-PayPal-red.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=AHWJHJFBAWGL2)
[![Yandex.Money donation](https://img.shields.io/badge/donation-Я.деньги-yellow.svg)](https://money.yandex.ru/embed/small.xml?account=41001158080699&quickpay=small&yamoney-payment-type=on&button-text=01&button-size=l&button-color=orange&targets=%D0%9F%D0%BE%D0%B6%D0%B5%D1%80%D1%82%D0%B2%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5+%D0%BD%D0%B0+%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D1%8B+%D1%81+%D0%BE%D1%82%D0%BA%D1%80%D1%8B%D1%82%D1%8B%D0%BC+%D0%B8%D1%81%D1%85%D0%BE%D0%B4%D0%BD%D1%8B%D0%BC+%D0%BA%D0%BE%D0%B4%D0%BE%D0%BC&default-sum=100&successURL=)

# Char Sniffer
A Small easy Maven plugin to check char codes in text files.

# How to use?
For instance to restrict char codes (0-255) in text file License.txt situated in the root project folder, you can use such configuration
```xml
<plugin>
  <groupId>com.igormaznitsa</groupId>
  <artifactId>char-sniffer</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <goals>
        <goal>sniff</goal>
      </goals>
      <configuration>
        <files>
          <file>${basedir}/License.txt</file>
        </files>
        <failForEmptyFile>true</failForEmptyFile>
        <minCharCode>0</minCharCode>
        <maxCharCode>255</maxCharCode>
      </configuration>
    </execution>
  </executions>
</plugin>
```