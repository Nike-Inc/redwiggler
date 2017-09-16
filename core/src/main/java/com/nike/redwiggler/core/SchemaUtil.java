package com.nike.redwiggler.core;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SchemaUtil {

    public static final Schema EMPTY = fromString("");

    public static Schema fromString(String json) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(turnBlankIntoEmptyJson(json)));
        return SchemaLoader.load(rawSchema);
    }

    public static String turnBlankIntoEmptyJson(String s) {
        if (s == null || s.equals("")) {
            return "{}";
        } else {
            return s;
        }
    }

    public static void validate(Schema schema, String json) throws ValidationException {
        JSONTokener tokenized = new JSONTokener(turnBlankIntoEmptyJson(json));
        if(json.startsWith("[")) {
            schema.validate(new JSONArray(tokenized)); // throws a ValidationException if this object is invalid
        } else {
            schema.validate(new JSONObject(tokenized)); // throws a ValidationException if this object is invalid
        }
    }
}
