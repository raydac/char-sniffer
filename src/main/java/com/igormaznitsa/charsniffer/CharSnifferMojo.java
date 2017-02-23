/* 
 * Copyright 2017 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.charsniffer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;

@Mojo(name = "sniff", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class CharSnifferMojo extends AbstractMojo {

  /**
   * Text files to be processed.
   */
  @Parameter(property = "files", required = true)
  private File[] files;

  /**
   * Minimal char code allowed.
   */
  @Parameter(property = "minCharCode", required = false, defaultValue = "-1")
  private int minCharCode;

  /**
   * Maximum char code allowed.
   */
  @Parameter(property = "maxCharCode", required = false, defaultValue = "-1")
  private int maxCharCode;

  /**
   * Char set for files.
   */
  @Parameter(property = "charSet", required = false, defaultValue = "UTF-8")
  private String charSet;

  /**
   * String of chars which only allowed to be presented in text files.
   */
  @Parameter(property = "abc", required = false)
  private String abc;

  /**
   * String of prohibited chars.
   */
  @Parameter(property = "noAbc", required = false)
  private String noAbc;

  /**
   * Fail if a file has zero length.
   */
  @Parameter(property = "failForEmptyFile", defaultValue = "false")
  private boolean failForEmptyFile;

  /**
   * Validate UTF8 chars.
   */
  @Parameter(property = "validateUtf8", defaultValue = "false")
  private boolean validateUtf8;

  /**
   * Ignore checking ISO special chars for ABC checking.
   */
  @Parameter(property = "ignoreAbcForISOControl", defaultValue = "true")
  private boolean ignoreAbcForISOControl;

  /**
   * Required End-Of-Line codes.
   */
  @Parameter(property = "eol", required = false, defaultValue = "UNDEFINED")
  private EndOfLine eol;

  @Parameter(property = "missingFilesAllowed", defaultValue = "false")
  private boolean missingFilesAllowed;

  private enum FileStatus {
    OK, BAD, MISSED
  }

  private void printStatus(@Nonnull final File file, @Nonnull final FileStatus status) {
    final String fileName = file.getName();
    final int len = 64 - fileName.length();

    final StringBuilder buffer = new StringBuilder(128);
    buffer.append(fileName);
    for (int i = 0; i < len; i++) {
      buffer.append('.');
    }
    buffer.append(status.name());

    switch (status) {
      case BAD:
        getLog().error(buffer.toString());
        break;
      case MISSED:
        getLog().warn(buffer.toString());
        break;
      default:
        getLog().info(buffer.toString());
        break;
    }
  }

  static boolean checkForCodes(@Nonnull final String text, @Nonnull final CheckConfig config, @Nonnull final StringBuilder errorBuffer) {
    final Set<Character> errorChars = new HashSet<Character>();

    if (config.minCode >= 0 || config.maxCode >= 0) {
      for (int i = 0; i < text.length(); i++) {
        final char c = text.charAt(i);
        if (config.minCode >= 0) {
          if (c < config.minCode) {
            if (!errorChars.contains(c)) {
              errorChars.add(c);
              if (errorBuffer.length() > 0) {
                errorBuffer.append(',');
              }
              errorBuffer.append('\'').append(c).append('\'');
            }
          }
        }

        if (config.maxCode >= 0) {
          if (c > config.maxCode) {
            if (!errorChars.contains(c)) {
              errorChars.add(c);
              if (errorBuffer.length() > 0) {
                errorBuffer.append(',');
              }
              errorBuffer.append('\'').append(c).append('\'');
            }
          }
        }
      }
    }
    return errorChars.isEmpty();
  }

  static boolean checkForAbc(@Nonnull final String text, @Nonnull final CheckConfig config) {
    final String allowed = config.abc;
    final String disallowed = config.noAbc;

    boolean result = true;

    if (allowed != null || disallowed != null) {
      for (int i = 0; i < text.length(); i++) {
        final char c = text.charAt(i);

        if (config.ignoreAbcForISOControl && Character.isISOControl(c)) {
          continue;
        }

        if (allowed != null) {
          result &= allowed.indexOf(c) >= 0;
        }

        if (disallowed != null) {
          result &= !(disallowed.indexOf(c) >= 0);
        }

        if (!result) {
          break;
        }
      }
      return result;

    }

    return result;
  }

  static boolean isValidUTF8(@Nonnull final byte[] input) {
    final CharsetDecoder cs = Charset.forName("UTF-8").newDecoder();
    try {
      cs.decode(ByteBuffer.wrap(input));
      return true;
    }
    catch (CharacterCodingException e) {
      return false;
    }
  }

  static boolean checkForEOL(@Nonnull final String text, @Nonnull final CheckConfig config) {
    boolean result = true;

    if (config.eol != EndOfLine.UNDEFINED) {
      final EndOfLine detected = findFirstEOL(text);
      result = (detected == EndOfLine.UNDEFINED) || (detected == config.eol);
    }

    return result;
  }

  @Nonnull
  static EndOfLine findFirstEOL(@Nonnull final String text) {
    char prev = ' ';

    EndOfLine result = EndOfLine.UNDEFINED;

    for (int i = 0; i < text.length(); i++) {
      final char curChar = text.charAt(i);
      if (curChar == '\n') {
        if (prev == '\r') {
          result = EndOfLine.CRLF;
        } else {
          result = EndOfLine.LF;
        }
        break;
      } else if (prev == '\r') {
        result = EndOfLine.CR;
        break;
      }
      prev = curChar;
    }

    if (result == EndOfLine.UNDEFINED) {
      switch (prev) {
        case '\n':
          result = EndOfLine.LF;
          break;
        case '\r':
          result = EndOfLine.CR;
          break;
      }
    }

    return result;
  }

  private boolean checkFile(@Nonnull final File file, @Nonnull final CheckConfig config) {
    try {
      final String textBody = FileUtils.readFileToString(file, config.charSet);

      final StringBuilder errorMessageBuffer = new StringBuilder();

      boolean result = checkForCodes(textBody, config, errorMessageBuffer);

      if (!result && getLog().isDebugEnabled()) {
        getLog().debug("Detected wrong chars : " + errorMessageBuffer.toString());
      }

      errorMessageBuffer.setLength(0);

      if (result) {
        result &= checkForAbc(textBody, config);
      }

      if (result) {
        result &= checkForEOL(textBody, config);
      }

      if (result && config.validateUtf8) {
        result &= isValidUTF8(FileUtils.readFileToByteArray(file));
        if (!result && getLog().isDebugEnabled()) {
          getLog().debug("File '" + file + "' contains wrong UTF8 byte sequence");
        }
      }

      return result;
    }
    catch (IOException ex) {
      getLog().error("Can't read text file : " + file, ex);
      return false;
    }
  }

  @Override
  public void execute() throws MojoExecutionException {
    final CheckConfig config = CheckConfig.build().
        setAbc(this.abc).
        setNoAbc(this.noAbc).
        setCharSet(this.charSet).
        setEol(this.eol).
        setMinCode(this.minCharCode).
        setMaxCode(this.maxCharCode).
        setValidateUtf8(this.validateUtf8).
        setIgnoreAbcForISOControl(this.ignoreAbcForISOControl).
        build();

    int errors = 0;

    for (final File file : this.files) {
      if (file.isFile()) {
        if (file.length() == 0L && this.failForEmptyFile) {
          printStatus(file, FileStatus.BAD);
          if (getLog().isDebugEnabled()) {
            getLog().debug("File '" + file + "' has zero length");
          }
          errors++;
        } else if (checkFile(file, config)) {
          printStatus(file, FileStatus.OK);
        } else {
          printStatus(file, FileStatus.BAD);
          errors++;
        }
      } else {
        printStatus(file, FileStatus.MISSED);
        if (getLog().isDebugEnabled()) {
          getLog().debug("File '" + file + "' not found");
        }

        if (!this.missingFilesAllowed) {
          throw new MojoExecutionException("Can't find file : " + file);
        }
      }
    }

    if (errors > 0) {
      throw new MojoExecutionException("Detected bad files, check log");
    }
  }
}
