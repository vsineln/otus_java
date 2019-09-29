package otus;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;

public class HelloOtus {

    public static void main(String[] args) {
        Iterable<String> iterable = Lists.newArrayList("test1", "test2");
        Collection<String> collection = Lists.newArrayList("test3", "test4");
        Iterables.addAll(collection, iterable);

        System.out.println(collection);
    }
}
