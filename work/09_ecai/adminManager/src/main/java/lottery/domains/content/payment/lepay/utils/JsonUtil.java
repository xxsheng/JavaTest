package lottery.domains.content.payment.lepay.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

public class JsonUtil
{
  private static ObjectMapper JSON = new ObjectMapper();

  public static String toJsonString(Object obj) {
    if (obj == null) {
      return null;
    }
    try
    {
      return JSON.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T parseObject(String json, Class<T> valueType) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try
    {
      return JSON.readValue(json, valueType);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T parseObject(String json, TypeReference<T> valueTupeRef) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }
    try
    {
      return JSON.readValue(json, valueTupeRef);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}