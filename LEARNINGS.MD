Install
on Linux 
`sudo apt-get install rocksdb-tools`

Mac 
`brew install rocksdb`

Mac from Source (problematic with linking issue when running, you may need to use `otool` `change_name_tool`)
brew install gflags
git clone git@github.com:facebook/rocksdb.git
cd rocksdb
make ldb sst_dump


Reference:
- https://blog.mitchseymour.com/using-ldb-to-inspect-kafka-streams-state-stores/