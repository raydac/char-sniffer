package com.igormaznitsa.charsniffer;

import org.junit.Test;
import static org.junit.Assert.*;

public class CharSnifferMojoTest {

  @Test
  public void testEolLF() {
    final CheckConfig config = CheckConfig.build().setEol(EndOfLine.LF).build();
    assertTrue(CharSnifferMojo.checkForEOL("", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\nWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\rWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r\nWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\n", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r\n", config));
  }

  @Test
  public void testEolCR() {
    final CheckConfig config = CheckConfig.build().setEol(EndOfLine.CR).build();
    assertTrue(CharSnifferMojo.checkForEOL("", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\nWorld", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\rWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r\nWorld", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\r", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\n", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r\n", config));
  }

  @Test
  public void testEolCRLF() {
    final CheckConfig config = CheckConfig.build().setEol(EndOfLine.CRLF).build();
    assertTrue(CharSnifferMojo.checkForEOL("", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\nWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\rWorld", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\r\nWorld", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\r", config));
    assertFalse(CharSnifferMojo.checkForEOL("Hello\n", config));
    assertTrue(CharSnifferMojo.checkForEOL("Hello\r\n", config));
  }

  @Test
  public void testCheckForAbc_IgnoreISOControl() {
    final CheckConfig config = CheckConfig.build().setAbc("abcdef").build();

    final StringBuilder errorChars = new StringBuilder();

    assertTrue(CharSnifferMojo.checkForAbc("aabc\n\rddeff", config, errorChars));
    assertFalse(CharSnifferMojo.checkForAbc("aabccoddeff", config, errorChars));
    assertTrue(CharSnifferMojo.checkForAbc("", config, errorChars));
  }

  @Test
  public void testCheckForAbc_DontIgnoreISOControl() {
    final CheckConfig config = CheckConfig.build().setAbc("abcdef").setIgnoreAbcForISOControl(false).build();

    final StringBuilder errorChars = new StringBuilder();

    assertFalse(CharSnifferMojo.checkForAbc("aabc\n\rddeff", config, errorChars));
    assertFalse(CharSnifferMojo.checkForAbc("aabccoddeff", config, errorChars));
    assertTrue(CharSnifferMojo.checkForAbc("", config, errorChars));
  }

  @Test
  public void testCheckForNoAbc() {
    final CheckConfig config = CheckConfig.build().setNoAbc("abcdef").build();

    final StringBuilder errorChars = new StringBuilder();

    assertTrue(CharSnifferMojo.checkForAbc("hrynzy", config, errorChars));
    assertFalse(CharSnifferMojo.checkForAbc("aabccoddeff", config, errorChars));
    assertTrue(CharSnifferMojo.checkForAbc("", config, errorChars));
  }

  @Test
  public void testCheckForCodes_Numbers() {
    final CheckConfig config = CheckConfig.build().setMinCode('0').setMaxCode('9').build();

    final StringBuilder errorChars = new StringBuilder();

    assertFalse(CharSnifferMojo.checkForCodes("1236217642121a23213", config, errorChars));
    assertTrue(CharSnifferMojo.checkForCodes("1123001323319931", config, errorChars));
    assertTrue(CharSnifferMojo.checkForCodes("", config, errorChars));
  }

  @Test
  public void testCheckForCodes_ASCII() {
    final CheckConfig config = CheckConfig.build().setMinCode(0).setMaxCode(0xFF).build();

    final StringBuilder errorChars = new StringBuilder();

    assertTrue(CharSnifferMojo.checkForCodes("askjhsadkjhsadoiqwueoiiUOIUOIkjH~OIUWQYEIQUWDKHSAKDHAK", config, errorChars));
    assertFalse(CharSnifferMojo.checkForCodes("askjhsadkjhsadoiqwueoiiUOIUOIkjH~OIUWQYEIQUWDKHSAKDHAKП", config, errorChars));
    assertTrue(CharSnifferMojo.checkForCodes("", config, errorChars));
  }

  @Test
  public void testIsValidUtf8() throws Exception {
    assertTrue(CharSnifferMojo.isValidUTF8(new byte[]{(byte) 0xD0, (byte) 0x80, (byte) 0xD1, (byte) 0x8b}));
    assertTrue(CharSnifferMojo.isValidUTF8(new byte[0]));
    assertFalse(CharSnifferMojo.isValidUTF8(new byte[]{(byte) 0xD0, (byte) 0x80, (byte) 0xD1}));
  }

}
