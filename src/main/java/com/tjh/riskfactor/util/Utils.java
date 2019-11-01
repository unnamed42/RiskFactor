package com.tjh.riskfactor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import static com.tjh.riskfactor.error.ResponseErrors.invalidArg;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toSet;

public class Utils {

    public static class MapBuilder {
        private Map<String, Object> map = new HashMap<>();

        public MapBuilder add(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return map;
        }

        public Optional<String> buildJson() {
            return toJson(map);
        }
    }

    public static <K, V> V require(Map<K, V> map, K key) {
        return optional(map, key).orElseThrow(() -> invalidArg(key.toString(), "null"));
    }

    public static <K, V> V require(Map<K, ?> map, K key, Class<? extends V> clazz) {
        Object value = map.get(key);
        if(value == null)
            throw invalidArg(key.toString(), "null");
        if(!clazz.isInstance(value))
            throw invalidArg(String.format("value of key [%s] has unexpected type", key));
        return clazz.cast(value);
    }

    public static <K, V> Optional<V> optional(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

    public static Stream<Field> annotatedDeclaredFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
               .filter(field -> field.isAnnotationPresent(annotation))
               .peek(field -> field.setAccessible(true));
    }

    public static Optional<String> toJson(Object obj) {
        try {
            return Optional.of(new ObjectMapper().writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public static Map<String, Object> fromJson(String json) throws JsonProcessingException {
        var type = new TypeReference<Map<String, Object>>() {};
        return new ObjectMapper().readValue(json, type);
    }

    public static String join(Collection<?> collection) {
        if(collection == null)
            return "[]";
        final var sb = new StringBuilder("[");
        collection.forEach(item -> sb.append(' ').append(item));
        return sb.append(" ]").toString();
    }

    public static MapBuilder kvMap() {
        return new MapBuilder();
    }

    public static MapBuilder kvMap(String key1, Object value1) {
        return new MapBuilder().add(key1, value1);
    }

    public static <T> T readTreeAsType(ObjectMapper mapper, TreeNode node, TypeReference<T> type) throws IOException {
        return mapper.readValue(
            mapper.treeAsTokens(node),
            mapper.getTypeFactory().constructType(type)
        );
    }

    public static boolean hasAnyAuthority(Authentication auth, String ...authorities) {
        final var set = Arrays.stream(authorities).collect(toSet());
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(set::contains);
    }

}
