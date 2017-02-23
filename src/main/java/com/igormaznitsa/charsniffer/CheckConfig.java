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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CheckConfig {

  public static class Builder {

    private String abc;
    private String noAbc;
    private EndOfLine eol = EndOfLine.UNDEFINED;
    private int minCode = -1;
    private int maxCode = -1;
    private String charSet = "UTF-8";
    private boolean validateUtf8 = false;
    private boolean ignoreAbcForISOControl = true;

    private Builder() {

    }

    @Nonnull
    public Builder setIgnoreAbcForISOControl(final boolean value) {
      this.ignoreAbcForISOControl = value;
      return this;
    }
    
    @Nonnull
    public Builder setValidateUtf8(final boolean value) {
      this.validateUtf8 = value;
      return this;
    }
    
    @Nonnull
    public Builder setMinCode(final int code) {
      this.minCode = code;
      return this;
    }

    @Nonnull
    public Builder setMaxCode(final int code) {
      this.maxCode = code;
      return this;
    }

    @Nonnull
    public Builder setAbc(@Nullable final String value) {
      this.abc = value;
      return this;
    }

    @Nonnull
    public Builder setNoAbc(@Nullable final String value) {
      this.noAbc = value;
      return this;
    }

    @Nonnull
    public Builder setEol(@Nullable final EndOfLine value) {
      this.eol = value == null ? EndOfLine.UNDEFINED : value;
      return this;
    }

    @Nonnull
    public Builder setCharSet(@Nullable final String value) {
      this.charSet = value == null ? "UTF-8" : value;
      return this;
    }

    @Nonnull
    public CheckConfig build() {
      return new CheckConfig(this.abc, this.noAbc, this.minCode, this.maxCode, this.eol, this.charSet, this.validateUtf8, this.ignoreAbcForISOControl);
    }

  }

  @Nullable
  public final String abc;
  
  @Nullable
  public final String noAbc;
  
  public final int minCode;
  public final int maxCode;

  @Nonnull
  public final EndOfLine eol;
  
  @Nonnull
  public final String charSet;

  public final boolean validateUtf8;
  
  public final boolean ignoreAbcForISOControl;
  
  private CheckConfig(
      @Nullable final String abc, 
      @Nullable final String noAbc, 
      final int minCode, 
      final int maxCode, 
      @Nonnull final EndOfLine eol, 
      @Nonnull final String charSet,
      final boolean validateUtf8,
      final boolean ignoreAbcForISOControl
  ) {
    this.abc = abc;
    this.noAbc = noAbc;
    this.minCode = minCode;
    this.maxCode = maxCode;
    this.eol = eol;
    this.charSet = charSet;
    this.validateUtf8 = validateUtf8;
    this.ignoreAbcForISOControl = ignoreAbcForISOControl;
  }

  @Nonnull
  public static CheckConfig.Builder build() {
    return new CheckConfig.Builder();
  }
}
