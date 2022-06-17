package task2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class IntersectionData {
    public static final int DATA_FILE_MAX_LINES_COUNT = 200_000;

    public static String generateString() {
        Random random = new Random();
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(random.nextInt(5), 5 + random.nextInt(5));
    }

    public static void generateData(String fileName1, String fileName2) {
        try (BufferedWriter brA = new BufferedWriter(new FileWriter(fileName1));
             BufferedWriter brB = new BufferedWriter(new FileWriter(fileName2))) {
            Random random = new Random();
            int file1LinesCount = random.nextInt(DATA_FILE_MAX_LINES_COUNT);
            int file2LinesCount = random.nextInt(DATA_FILE_MAX_LINES_COUNT);
            for (int i = 0; i < file1LinesCount; ++i) {
                brA.write(String.format("%d,%s\n", random.nextInt(DATA_FILE_MAX_LINES_COUNT / 4), generateString()));
            }
            for (int i = 0; i < file2LinesCount; ++i) {
                brB.write(String.format("%d,%s\n", random.nextInt(DATA_FILE_MAX_LINES_COUNT / 4), generateString()));
            }
        } catch (IOException e) {
            System.out.println("При генерации данных возникла ошибка.");
        }
    }

    public static ArrayList<Node> readFileData(String inputFileName) {
        ArrayList<Node> res = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            while (br.ready()) {
                String[] nodeData = br.readLine().split(",");
                if (nodeData.length != 2) continue;
                try {
                    Node node = new Node(Integer.parseInt(nodeData[0]), nodeData[1]);
                    res.add(node);
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            System.out.printf("Файл для '%s' поврежден или имеет неверный формат.\n", inputFileName);
        }
        return res;
    }

    public static LinkedList<Node> putDataIntoSortedLinkedList(ArrayList<Node> l) {
//        LinkedList<Node> res = new LinkedList<>(l);
//        res.sort(Comparator.comparingInt(Node::getId));
//        return res;

        return l.stream()
                .sorted(Comparator.comparingInt(Node::getId))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static HashMap<Integer, List<String>> putDataIntoHashMap(ArrayList<Node> l) {
//        HashMap<Integer, List<String>> res = new HashMap<>();
//        for (Node node : l) {
//            res.putIfAbsent(node.getId(), new ArrayList<>());
//            res.get(node.getId()).add(node.getValue());
//        }
//        return res;

        return l.stream()
                .collect(HashMap<Integer, List<String>>::new,
                        (map, key) -> {
                            map.putIfAbsent(key.getId(), new ArrayList<>());
                            map.get(key.getId()).add(key.getValue());
                        },
                        (m1, m2) -> {});
    }
}
