package com.fw.main;

import com.fw.main.utils.collision.Hitbox;

import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Fw {
    private static Map<Integer, Base> integerBaseMap = new ConcurrentHashMap<>();
    private static Map<String, Base> stringBaseMap = new ConcurrentHashMap<>();
    static void add(String key,Base base) {
        stringBaseMap.put(key,base); }
    static void add(int integer, Base base) {
        integerBaseMap.put(integer,base); }
    public static Base get(String key) {return stringBaseMap.get(key);}
    public static Base get(int key) {return integerBaseMap.get(key);}

    public static class Debugger {
        public static boolean showHitbox;
        private static Map<UUID, Hitbox> hitboxMap = new ConcurrentHashMap<>();
        static void renderHitbox(Graphics g) {
            for (Hitbox h : hitboxMap.values()) {
                h.renderHitbox(g);
            }
        }
        static void addObject(Hitbox hitbox) {
            hitboxMap.put(hitbox.id,hitbox);
        }
        static void freeObject(Hitbox hitbox) {
            hitboxMap.remove(hitbox.id);
        }
        @Deprecated
        public static class Internal {
            public static void addObject(Hitbox hitbox) {
                Debugger.addObject(hitbox);
            }
            public static void freeObject(Hitbox hitbox) {
                Debugger.freeObject(hitbox);
            }
            public static void renderHitbox(Graphics g) {
                Debugger.renderHitbox(g);
            }
        }
    }
}
