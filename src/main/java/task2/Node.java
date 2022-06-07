package task2;

public class Node {
    private final int id;
    private final String value;

    public Node(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (getId() != node.getId()) return false;
        return getValue().equals(node.getValue());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getValue().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
