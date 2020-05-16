package com.greymass.esr.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.greymass.esr.ESRException;

import java.util.List;
import java.util.Map;

public class InfoPair {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private String gKey;
    private String gStringValue;
    private byte[] gBytesValue;

    public InfoPair(String key, String value) {
        gKey = key;
        gStringValue = value;
    }

    public InfoPair(String key, byte[] value) {
        gKey = key;
        gBytesValue = value;
    }

    public String getKey() {
        return gKey;
    }

    public boolean isStringValue() {
        return gStringValue != null;
    }

    public String getStringValue() {
        return gStringValue;
    }

    public byte[] getBytesValue() {
        return gBytesValue;
    }

    public static InfoPair fromJsonObject(JsonObject obj) {
        String key = obj.get(KEY).getAsString();
        JsonElement value = obj.get(VALUE);
        if (value.getAsJsonPrimitive().isString())
            return new InfoPair(key, value.getAsString());

        JsonArray array = value.getAsJsonArray();
        byte[] bytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            bytes[i] = array.get(i).getAsByte();
        }

        return new InfoPair(key, bytes);
    }

    public static List<InfoPair> listFromJsonArray(JsonArray pairs) throws ESRException {
        List<InfoPair> infoPairs = Lists.newArrayList();

        for (JsonElement el : pairs) {
            if (!(el instanceof JsonObject))
                throw new ESRException("Info pairs must be objects");

            infoPairs.add(InfoPair.fromJsonObject((JsonObject) el));
        }

        return infoPairs;
    }

    private Map<String, Integer> bytesToMap() {
        Map<String, Integer> bytesMap = Maps.newHashMap();
        for (int i = 0; i < gBytesValue.length; i++) {
            bytesMap.put(String.valueOf(i), (int) gBytesValue[i]);
        }
        return bytesMap;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> pair = Maps.newHashMap();
        pair.put(KEY, gKey);
        pair.put(VALUE, gStringValue != null ? gStringValue : bytesToMap());
        return pair;
    }
}
