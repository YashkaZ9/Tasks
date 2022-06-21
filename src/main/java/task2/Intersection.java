package task2;

import java.io.*;
import java.util.*;

public class Intersection {
    public static final String ARRAYLISTS_INTERSECTION_FILENAME = "output/intersections/arrayListsIntersection.txt";
    public static final String SORTED_LINKEDLISTS_INTERSECTION_FILENAME = "output/intersections/linkedListsIntersection.txt";
    public static final String HASHMAPS_INTERSECTION_FILENAME = "output/intersections/hashMapsIntersection.txt";

    //O(n^2)
    public static void findArrayListsIntersectionSeq(ArrayList<Node> l1, ArrayList<Node> l2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARRAYLISTS_INTERSECTION_FILENAME))) {
            for (Node a : l1) {
                for (Node b : l2) {
                    if (a.getId() == b.getId()) {
                        bw.write(String.format("%d,%s,%s\n", a.getId(), a.getValue(), b.getValue()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("В методе findArrayListIntersectionSeq произошла ошибка записи в файл.");
        }
    }

    //O(n^2)
    public static void findArrayListsIntersectionPar(ArrayList<Node> l1, ArrayList<Node> l2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARRAYLISTS_INTERSECTION_FILENAME))) {
            l1.stream().parallel().forEach(a -> l2.stream().parallel().forEach(b -> {
                if (a.getId() == b.getId()) {
                    try {
                        bw.write(String.format("%d,%s,%s\n", a.getId(), a.getValue(), b.getValue()));
                    } catch (IOException ignored) {
                    }
                }
            }));
        } catch (IOException e) {
            System.out.println("В методе findArrayListIntersectionPar произошла ошибка записи в файл.");
        }
    }

    //O(n)
    public static void findSortedLinkedListsIntersection(LinkedList<Node> sl1, LinkedList<Node> sl2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SORTED_LINKEDLISTS_INTERSECTION_FILENAME))) {
            ListIterator<Node> iterA = sl1.listIterator();
            ListIterator<Node> iterB = sl2.listIterator();
            while (iterA.hasNext() && iterB.hasNext()) {
                Node a = iterA.next();
                Node b = iterB.next();
                while (a.getId() < b.getId()) {
                    if (iterA.hasNext()) {
                        a = iterA.next();
                    } else break;
                }
                while (b.getId() < a.getId()) {
                    if (iterB.hasNext()) {
                        b = iterB.next();
                    } else break;
                }
                int i = 0;
                while (a.getId() == b.getId()) {
                    bw.write(String.format("%d,%s,%s\n", a.getId(), a.getValue(), b.getValue()));
                    if (iterB.hasNext()) {
                        b = iterB.next();
                        i++;
                    } else break;
                }
                while (i-- >= 0) {
                    iterB.previous();
                }
            }
        } catch (IOException e) {
            System.out.println("В методе findSortedLinkedListsIntersection произошла ошибка записи в файл.");
        }
    }

    //O(n)
    public static void findSortedLinkedListsIntersectionWithCache(LinkedList<Node> sl1, LinkedList<Node> sl2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SORTED_LINKEDLISTS_INTERSECTION_FILENAME))) {
            ListIterator<Node> iterA = sl1.listIterator();
            ListIterator<Node> iterB = sl2.listIterator();
            List<String> bValuesCache = new LinkedList<>();
            int prevAId = -1;
            while (iterA.hasNext() && iterB.hasNext()) {
                Node a = iterA.next();
                Node b = iterB.next();
                if (a.getId() < b.getId()) {
                    iterB.previous();
                    continue;
                }
                if (b.getId() < a.getId()) {
                    iterA.previous();
                    continue;
                }
                if (a.getId() != prevAId) {
                    bValuesCache = new LinkedList<>();
                    while (a.getId() == b.getId()) {
                        bValuesCache.add(b.getValue());
                        if (iterB.hasNext()) {
                            b = iterB.next();
                        } else break;
                    }
                }
                iterB.previous();
                if (iterB.hasPrevious()) iterB.previous();
                for (String bValue : bValuesCache) {
                    bw.write(String.format("%d,%s,%s\n", a.getId(), a.getValue(), bValue));
                }
                prevAId = a.getId();
            }
        } catch (IOException e) {
            System.out.println("В методе findSortedLinkedListsIntersectionWithCache произошла ошибка записи в файл.");
        }
    }

    //O(n)
    public static void findHashMapsIntersection(Map<Integer, List<String>> m1, Map<Integer, List<String>> m2) {
        if (m1.size() > m2.size()) {
            findHashMapsIntersection(m2, m1);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HASHMAPS_INTERSECTION_FILENAME))) {
            for (Integer id : m1.keySet()) {
                if (m2.containsKey(id)) {
                    for (String valueA : m1.get(id)) {
                        for (String valueB : m2.get(id)) {
                            bw.write(String.format("%d,%s,%s\n", id, valueA, valueB));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("В методе findHashMapsIntersection произошла ошибка записи в файл.");
        }
    }
}
