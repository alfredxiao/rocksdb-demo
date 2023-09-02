package xiaoyf.demo.rocksdb.helloworld;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.CompactionStyle;
import org.rocksdb.MutableColumnFamilyOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.LongStream;

@Slf4j
public class HelloSstRocksDB {
    File dbDir;
    RocksDB db;

    public void demo(final long updateCount) {
        RocksDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);
        options.setDisableAutoCompactions(true);
        options.setCompactionStyle(CompactionStyle.NONE);
        options.setPeriodicCompactionSeconds(0);
        dbDir = new File("/tmp/rocks-db", "hello." + updateCount);
        try {
            Files.createDirectories(dbDir.getParentFile().toPath());
            Files.createDirectories(dbDir.getAbsoluteFile().toPath());
            db = RocksDB.open(options, dbDir.getAbsolutePath());
        } catch(IOException | RocksDBException ex) {
            log.error("Error initialing RocksDB, check configurations and permissions, exception: {}, message: {}, stackTrace: {}",
                    ex.getCause(), ex.getMessage(), ex.getStackTrace());
        }
        log.info("RocksDB initialized and ready to use");

        String key = "hello";
        String value = "world";

        log.info("save");
        try {
            db.put(key.getBytes(), value.getBytes());
        } catch (RocksDBException e) {
            log.error("Error saving entry in RocksDB, cause: {}, message: {}", e.getCause(), e.getMessage());
        }

        log.info("update");
        LongStream.rangeClosed(1, updateCount).boxed().forEach(n -> {
            try {
                byte[] bytes = db.get(key.getBytes());
                assert (bytes != null);

                final String str10 = String.format("%10d", n);
                db.put(key.getBytes(), str10.getBytes());
            } catch (RocksDBException e) {
                log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
            }
        });

    }

    public static void main(String[] args) {

        List<Long> testCounts = List.of(
                1L, 10L, 100L, 1000L,
                10000L, 100000L,
                1000000L, 5000000L
        );

        for (Long count : testCounts) {
            final long start = System.currentTimeMillis();
            HelloSstRocksDB demo = new HelloSstRocksDB();
            demo.demo(count);
            final long end = System.currentTimeMillis();

            log.info("demo for {} records took {} ms", count, (end - start));
        }
    }

    static long pow(long a, long p) {
        int result = 1;
        for (int i=0; i<p; i++) {
            result *= a;
        }

        return result;
    }
}
