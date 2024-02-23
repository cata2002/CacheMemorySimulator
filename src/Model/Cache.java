package Model;

public class Cache {
    private int size;
    private int offsetBits;
    private WritePolicy policy;

    public Cache(int size) {
        this.size = size;
    }

    public Cache() {
    }

    public Cache(int size, int offsetBits, WritePolicy policy) {
        this.size = size;
        this.offsetBits = offsetBits;
        this.policy = policy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffsetBits() {
        return offsetBits;
    }

    public void setOffsetBits(int offsetBits) {
        this.offsetBits = offsetBits;
    }

    public WritePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(WritePolicy policy) {
        this.policy = policy;
    }
}
