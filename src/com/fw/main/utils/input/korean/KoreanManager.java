package com.fw.main.utils.input.korean;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class KoreanManager {
    public static Map<UUID, KoreanObject> koreanObjectMap = new ConcurrentHashMap<>();
    public static Map<UUID, KoreanObject> activeObjectsMap = new ConcurrentHashMap<>();
    public static void koreanObjectPut(KoreanObject o) {koreanObjectMap.put(o.id,o);}
    public static void activeObjectPut(KoreanObject o) {activeObjectsMap.put(o.id,o);}
    public static void koreanObjectRemove(KoreanObject o) {koreanObjectMap.remove(o.id);}
    public static void activeObjectRemove(KoreanObject o) {activeObjectsMap.remove(o.id);}


}
