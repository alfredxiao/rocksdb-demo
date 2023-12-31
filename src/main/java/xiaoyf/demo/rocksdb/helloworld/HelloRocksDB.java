package xiaoyf.demo.rocksdb.helloworld;

import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class HelloRocksDB {

    private final static String NAME = "hello-db";
    File dbDir;
    RocksDB db;

    //Logger logger = null;

    public void demo() {
        RocksDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);
        dbDir = new File("/tmp/rocks-db", NAME);
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

        log.info("find");
        String result = null;
        try {
            byte[] bytes = db.get(key.getBytes());
            assert(bytes != null);
            result = new String(bytes);
        } catch (RocksDBException e) {
            log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
        }

        log.info("update");
        try {
            byte[] bytes = db.get(key.getBytes());
            assert(bytes != null);
            db.put(key.getBytes(), (value+".updated").getBytes());
        } catch (RocksDBException e) {
            log.error("Error retrieving the entry in RocksDB from key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
        }

//        log.info("delete");
//        try {
//            db.delete(key.getBytes());
//        } catch (RocksDBException e) {
//            log.error("Error deleting entry in RocksDB, cause: {}, message: {}", e.getCause(), e.getMessage());
//        }
    }

    public static void main(String[] args) {
        HelloRocksDB demo = new HelloRocksDB();
        demo.demo();
    }
}
