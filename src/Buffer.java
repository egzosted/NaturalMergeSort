import java.io.*;
import java.nio.ByteBuffer;

public class Buffer {
    public static final int DOUBLE_SIZE = 8;
    public static final int INT_SIZE = 4;
    public static final int EMPTY = 0;
    private final int SIZE = 5;
    private final String PATH = "/home/egzosted/JavaProjects/NaturalMergeSort/tmp/";
    public int position;
    private int fill;
    private int nextRecord;
    private final Record[] records;
    boolean append;
    private final String filename;

    public Buffer(String filename) {
        this.filename = PATH + filename;
        append = false;
        fill = EMPTY;
        nextRecord = 0;
        position = 0;
        records = new Record[SIZE];
        for (int i=0;i<SIZE;i++) {
            records[i] = new Record(0.0,0);
        }
    }

    public void insert(Record record) {
        records[fill].setHeight(record.getHeight());
        records[fill].setWeight(record.getWeight());
        records[fill].updateBMI();

        fill++;
        if (fill == SIZE) {
            write();
        }
    }

    public void write() {
        try (OutputStream os = new FileOutputStream(filename, append)) {
            append = true;
            byte[] bytes;
            for (int i=0;i<fill;i++) {
                os.write(ByteBuffer.allocate(Double.BYTES).putDouble(records[i].getHeight()).array());
                os.write(ByteBuffer.allocate(Integer.BYTES).putInt(records[i].getWeight()).array());
                os.write(ByteBuffer.allocate(Double.BYTES).putDouble(records[i].getBMI()).array());

            }
        }
        catch (IOException ex) {
            System.out.println("Buffer couldn't have been written");
        }
        fill = EMPTY;
    }

    public void read() {
        fill = 0;
        int endOfData = 0;
        try (InputStream is = new FileInputStream(filename)) {
            is.skip(position);
            byte[] bytes = new byte[DOUBLE_SIZE];
            for (int i=0;i<SIZE;i++) {
                bytes = new byte[DOUBLE_SIZE];
                endOfData = is.read(bytes);
                if (endOfData == -1) {
                    break;
                }
                records[i].setHeight(ByteBuffer.wrap(bytes).getDouble());
                position += Double.BYTES;
                bytes = new byte[INT_SIZE];
                is.read(bytes);
                records[i].setWeight(ByteBuffer.wrap(bytes).getInt());
                position += Integer.BYTES;
                bytes = new byte[DOUBLE_SIZE];
                is.read(bytes);
                records[i].setBMI(ByteBuffer.wrap(bytes).getDouble());
                position += Double.BYTES;
                fill++;
            }
        }
        catch (IOException ex) {
            System.out.println("Buffer couldn't have been read");
        }
    }

    public Record getNext() {
        if (fill == EMPTY || nextRecord == SIZE) {
            read();
            nextRecord = 0;
        }
        if (nextRecord < fill) {
            return records[nextRecord++];
        }
        else {
            return new Record(1.0, 0);
        }

    }

    public void print() {
        read();
        int displayCount = 0;
        for (int i=0;i<fill;i++) {
            System.out.printf("%d\t%f\t%d\t%f\n", displayCount++, records[i].getHeight(), records[i].getWeight(), records[i].getBMI());
            if (i + 1 == fill) {
                read();
                i = -1;
                if (fill == EMPTY) {
                    break;
                }
            }
        }
        System.out.println();
    }

    public int getNextRecord() {
        return nextRecord;
    }

    public void setNextRecord(int nextRecord) {
        this.nextRecord = nextRecord;
    }
}
