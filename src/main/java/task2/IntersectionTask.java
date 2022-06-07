package task2;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class IntersectionTask {
    public static final String ARRAYLISTS_INTERSECTION_FILENAME = "intersections/arrayListsIntersection.txt";
    public static final String SORTED_LINKEDLISTS_INTERSECTION_FILENAME = "intersections/linkedListsIntersection.txt";
    public static final String HASHMAPS_INTERSECTION_FILENAME = "intersections/hashMapsIntersection.txt";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Неверное количество параметров. " +
                    "Их должно быть два: [fileName1] [fileName2].");
            return;
        }
        IntersectionTask intersection = new IntersectionTask();
        //intersection.generateTest(args[0], args[1]);
        ArrayList<Node> d1;
        try {
            d1 = intersection.readFileData(args[0]);
        } catch (IOException | NumberFormatException e) {
            System.out.printf("Файл '%s' поврежден или имеет неверный формат.\n", args[0]);
            return;
        }
        ArrayList<Node> d2;
        try {
            d2 = intersection.readFileData(args[1]);
        } catch (IOException | NumberFormatException e) {
            System.out.printf("Файл '%s' поврежден или имеет неверный формат.\n", args[1]);
            return;
        }
        intersection.benchmark(intersection::findArrayListsIntersection, d1, d2);
        LinkedList<Node> sl1 = intersection.putDataIntoSortedLinkedList(d1);
        LinkedList<Node> sl2 = intersection.putDataIntoSortedLinkedList(d2);
        intersection.benchmark(intersection::findSortedLinkedListsIntersection, sl1, sl2);
        HashMap<Integer, List<String>> m1 = intersection.putDataIntoHashMap(d1);
        HashMap<Integer, List<String>> m2 = intersection.putDataIntoHashMap(d2);
        intersection.benchmark(intersection::findHashMapsIntersection, m1, m2);
    }

    //O(n^2)
    public void findArrayListsIntersection(ArrayList<Node> l1, ArrayList<Node> l2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARRAYLISTS_INTERSECTION_FILENAME))) {
            for (Node a : l1) {
                for (Node b : l2) {
                    if (a.getId() == b.getId()) {
                        bw.write(String.format("%d,%s,%s\n", a.getId(), a.getValue(), b.getValue()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("В методе findArrayListIntersection произошла ошибка записи в файл.");
        }
    }

    //O(n)
    public void findSortedLinkedListsIntersection(LinkedList<Node> sl1, LinkedList<Node> sl2) {
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
    public void findHashMapsIntersection(Map<Integer, List<String>> m1, Map<Integer, List<String>> m2) {
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

    public ArrayList<Node> readFileData(String inputFileName) throws NumberFormatException, IOException {
        ArrayList<Node> res = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            while (br.ready()) {
                String[] nodeData = br.readLine().split(",");
                Node node = new Node(Integer.parseInt(nodeData[0]), nodeData[1]);
                res.add(node);
            }
        }
        return res;
    }

    public LinkedList<Node> putDataIntoSortedLinkedList(ArrayList<Node> l) {
        LinkedList<Node> res = new LinkedList<>(l);
        res.sort(Comparator.comparingInt(Node::getId));
        return res;
    }

    public HashMap<Integer, List<String>> putDataIntoHashMap(ArrayList<Node> l) {
        HashMap<Integer, List<String>> res = new HashMap<>();
        for (Node node : l) {
            res.putIfAbsent(node.getId(), new ArrayList<>());
            res.get(node.getId()).add(node.getValue());
        }
        return res;
    }

    public <T> void benchmark(BiConsumer<T, T> intersector, T a, T b) {
        long start = System.currentTimeMillis();
        intersector.accept(a, b);
        long finish = System.currentTimeMillis();
        System.out.printf("Поиск пересечения %s'ов занял %d ms\n", a.getClass().getSimpleName(), finish - start);
    }

    public void generateTest(String fileName1, String fileName2) {
        try (BufferedWriter brA = new BufferedWriter(new FileWriter(fileName1));
             BufferedWriter brB = new BufferedWriter(new FileWriter(fileName2))
        ) {
            Random random = new Random();
            int file1LinesCount = random.nextInt(10_000);
            int file2LinesCount = random.nextInt(10_000);
            for (int i = 0; i < file1LinesCount; ++i) {
                brA.write(String.format("%d,%s\n",
                        random.nextInt(5_000),
                        UUID.randomUUID().toString()
                                .replaceAll("-", "")
                                .substring(random.nextInt(5), 5 + random.nextInt(5))));
            }
            for (int i = 0; i < file2LinesCount; ++i) {
                brB.write(String.format("%d,%s\n",
                        random.nextInt(5_000),
                        UUID.randomUUID().toString()
                                .replaceAll("-", "")
                                .substring(random.nextInt(5), 5 + random.nextInt(5))));
            }
        } catch (IOException e) {
            System.out.println("При генерации теста возникла ошибка.");
        }
    }
}
