package practice;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.*;

public class BaeldungMethodReference {
    public static void main(String[] args) {
        List<String> message = Arrays.asList("i", "am", "nitin", "jaswani");

//        message.forEach(word -> System.out.println(StringUtils.capitalize(word)));
        message.forEach(System.out::println);
    }
}
