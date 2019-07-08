/**
 * This custom assertion compares JSON with expected data using JSONAssert.
 */
public class JSONAssertion implements MunitAssertion {

private Object expected;
// [...]

public JSONAssertion(final Object expected) {
    super();
    this.expected = expected;
}
// [...]

/**
 * Check if the expected object's String representation matches the asserted object's one.
 * @param target The asserted data
 * @return true. Or throw an exception.
 */
public boolean check(final Object target) {

    String expectedString = convertToString(expected, () -> expected.toString());
    String assertedString = convertToString(target, () -> target.toString());

    try {
        JSONAssert.assertEquals(expectedString, assertedString, strict);
    } catch (final JSONException e) {
        final AssertionError error = new AssertionError("JSON assertion failed");
        error.initCause(e);
        throw error;
    }
    return true;
}

@Override
public MuleEvent execute(final MuleEvent muleEvent) throws AssertionError {
    String payloadAsString;
    try {
        payloadAsString = muleEvent.getMessage().getPayloadAsString();
    } catch (final Exception e) {
        throw new RuntimeException(e);
    }
    check(payloadAsString);
    return muleEvent;
}

// could make use of this :
public String convertToString(final Object object, final Supplier<String> defaultResult) {
  try {
      if (object == null) {
          return null;
      }
      if (object instanceof String) {
          return (String) object;
      }
      if (object instanceof byte[]) {
          return new String((byte[]) object, "utf-8");
      }
      if (object instanceof char[]) {
          return new String((char[]) object);
      }
      if (object instanceof InputStream) {
          return IOUtils.toString((InputStream) object, StandardCharsets.UTF_8);
      }
      if (object instanceof Reader) {
          return IOUtils.toString((Reader) object);
      }
      if (object.getClass().getSimpleName().equals("Date")
                      || object.getClass().getSimpleName().endsWith("Calendar")
                      || object instanceof Character
                      || object.getClass() == char.class) {
          return object.toString();
      }
      if (defaultResult == null) {
          return object.toString();
      }
      // Redirect to Jaxon or some other JSON formatting tool
      return defaultResult.get();
  } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
  } catch (final IOException e) {
      throw new RuntimeException(e);
  } catch (final Exception e) {
      throw new RuntimeException(e);
  }
}
