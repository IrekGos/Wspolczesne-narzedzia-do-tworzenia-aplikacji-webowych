package com.example.demo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
public class Controller {

    Map<String, Integer> map = new HashMap<String, Integer>();

    public Map<String, Integer> Sort(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        return name + " parameter is missing!";
    }

    @GetMapping(value = "register")
    public Map<String, String> registerUser(@RequestParam String name) {
        map.put(name, map.getOrDefault(name, 0) + 1);
        Map<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put("status", "OK");
        tmpMap.put("count", map.get(name).toString());
        return tmpMap;
    }

    @GetMapping(value = "delete")
    public String deleteUser(@RequestParam String name) {
        map.remove(name);
        return "user " + name + " deleted";
    }

    @GetMapping(value = "stats")
    public Map<String, Integer> getStats(@RequestParam(required = false) String mode) {
        Map<String, Integer> sorted = new HashMap<String, Integer>();
        if (mode == null) {
            sorted = map.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        } else if (Objects.equals(mode, new String("ALL"))) {
            sorted = Sort(map);
        } else if (Objects.equals(mode, new String("IGNORE_CASE"))) {
            TreeMap<String, Integer> new_map = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
            for (var entry : map.entrySet()) {
                if (new_map.containsKey(entry.getKey()))
                    new_map.put(entry.getKey(), new_map.get(entry.getKey()) + entry.getValue());
                else
                    new_map.put(entry.getKey(), entry.getValue());
            }
            sorted = Sort(new_map);
        }

        return sorted;
    }

}
