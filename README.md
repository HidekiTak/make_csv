# make_csv
Export DB Table/View to CSV File with Gzip, AES

## for Use
Copy JDBC Driver to JAVA_HOME/lib/ext/～ | JAVA_HOME/jre/lib/ext/～ (checked only mysql)

    $ java -jar make_csv-assembly-1.0.0.jar encode [encode.cnf] [export_file]
    $ java -jar make_csv-assembly-1.0.0.jar decode [decode.cnf] [import_file] [export_file]

### compile
    $ ./sbt.sh

## ConvertConfig
### Encode/Decode Common Config
```
# for check aes.key,aes.iv. datatype is Long. 
serial=201609101600
file.aes.key=[KeyStr]
file.aes.iv=[IVStr]
file.gzip=true
```

### Encode Config
    # export file charset
    file.charset=UTF-8
    # DateTimeFormat's TimeZone for sql.TimeStamp Column ("JST","Asia/Tokyo","+09:00")
    file.timezone=UTC
    # Format for sql.TimeStamp Column
    file.dtformat=yyyy/MM/dd HH:mm:ss
    # DB Driver Class
    #db.con=jdbc:mysql://;com.mysql.jdbc.Driver
    # DB ConnectionString
    db.con=jdbc:mysql://localhost/bspark?user=root&password=root&useSSL=false
    # Data Select SqlString. Create a view for a limited user.
    db.sql=SELECT * FROM `groups`

#### aes.key,aes.iv
    32byte HexaDecimalString -> byte[16]
    else                     -> MD5 Hash

## Gzip,AES Flow
    Encode: csv -> gzip -> aes

## AES FileHeader
    [0x00],[0x01]  Header Version
    byte[8]    AES Serial(serial of config file)
