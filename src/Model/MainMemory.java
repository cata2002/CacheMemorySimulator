package Model;

public class MainMemory {
    private int size;
    private int offset;
    private int [][]memory;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int[][] getMemory() {
        return memory;
    }

    public void setMemory(int[][] memory) {
        this.memory = memory;
    }

    public MainMemory() {
    }

    public MainMemory(int size, int offset) {
        this.size = size;
        this.offset = offset;
        int row=0, col=0;
        col=(int)Math.pow(2,offset);
        row=size/(int)Math.pow(2,offset);
        this.memory=new int[row][col];
    }
}
