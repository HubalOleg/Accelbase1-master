package com.oleg.hubal.accelbase.utility;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * Created by User on 02.11.2016.
 */

public class GraphXLabelFormat extends Format {

    private String[] mDateLabels;

    public GraphXLabelFormat(String[] dateLabels) {
        mDateLabels = dateLabels;
    }

    @Override
    public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
        int parsedInt = Math.round(Float.parseFloat(object.toString()));
        String labelString = mDateLabels[parsedInt];
        buffer.append(labelString);
        return buffer;
    }

    @Override
    public Object parseObject(String string, ParsePosition position) {
        return Arrays.asList(mDateLabels).indexOf(string);
    }
}
