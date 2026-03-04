package me.junioraww.textonhead.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PassengerRegistry {
  private static final Map<Integer, Set<Integer>> registry = new ConcurrentHashMap<>();

  public static void register(int playerEntityId, int displayId) {
    registry.computeIfAbsent(playerEntityId, k -> ConcurrentHashMap.newKeySet()).add(displayId);
  }

  public static void unregister(int playerEntityId, int displayId) {
    Set<Integer> ids = registry.get(playerEntityId);
    if (ids != null) ids.remove(displayId);
  }

  public static Set<Integer> getForEntityId(int entityId) {
    return registry.getOrDefault(entityId, Collections.emptySet());
  }

  public static void clear(int entityId) {
    registry.remove(entityId);
  }
}