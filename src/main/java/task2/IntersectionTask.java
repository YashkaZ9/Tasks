package task2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class IntersectionTask {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Неверное количество параметров. " +
                    "Их должно быть два: [fileName1] [fileName2].");
            return;
        }
//        IntersectionData.generateData(args[0], args[1]);
        ArrayList<Node> d1 = IntersectionData.readFileData(args[0]);
        ArrayList<Node> d2 = IntersectionData.readFileData(args[1]);
//        benchmark(Intersection::findArrayListsIntersectionSeq, d1, d2);
        benchmark(Intersection::findArrayListsIntersectionPar, d1, d2);
        LinkedList<Node> sl1 = IntersectionData.putDataIntoSortedLinkedList(d1);
        LinkedList<Node> sl2 = IntersectionData.putDataIntoSortedLinkedList(d2);
        benchmark(Intersection::findSortedLinkedListsIntersection, sl1, sl2);
//        benchmark(Intersection::findSortedLinkedListsIntersectionWithCache, sl1, sl2);
        HashMap<Integer, List<String>> m1 = IntersectionData.putDataIntoHashMap(d1);
        HashMap<Integer, List<String>> m2 = IntersectionData.putDataIntoHashMap(d2);
        benchmark(Intersection::findHashMapsIntersection, m1, m2);
    }

    public static <T> void benchmark(BiConsumer<T, T> intersector, T a, T b) {
        long start = System.currentTimeMillis();
        intersector.accept(a, b);
        long finish = System.currentTimeMillis();
        System.out.printf("Поиск пересечения %s'ов занял %d ms\n", a.getClass().getSimpleName(), finish - start);
    }
}
