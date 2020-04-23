package com.rbkmoney.clickhousenotificator.formatter;

import java.util.List;
import java.util.Map;

public interface Formatter {

    String format(String form, List<Map<String, String>> parameters);

}
