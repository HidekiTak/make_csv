# make_csv
Export DB Table/View to CSV File with Gzip, AES

## for Use
Copy JDBC Driver to JAVA_HOME/~ (checked only mysql)

    $ java -jar make_csv-assembly-1.0.0.jar encode [encode.cnf] [export_file]
    $ java -jar make_csv-assembly-1.0.0.jar decode [decode.cnf] [import_file] [export_file]

### compile
    $ bin/activator assembly

## ConvertConfig
### Encode/Decode Common Config
    # for check aes.key,aes.iv. datatype is Long. 
    serial=201609101600
    aes.key=[KeyStr]
    aes.iv=[IVStr]
    gzip=true

### Encode Config
    # export file charset
    charset=UTF-8
    # DateTimeFormat's TimeZone "JST","Asia/Tokyo","+09:00"
    timezone=UTC
    # Format for sql.TimeStamp Column
    datetime.format=yyyy/MM/dd HH:mm:ss
    # DB ConnectionString
    db.con=jdbc:mysql://localhost/bspark?user=root&password=root&useSSL=false
    # Data Select SqlString. Create a view for a limited user.
    db.sql=SELECT * FROM `groups`

#### aes.key,aes.iv
    32byte HexDecimalString -> byte[16]
    else                    -> MD5 Hash

## Gzip,AES Flow
    Encode: csv -> gzip -> aes

## AES FileHeader
    [0x00],[0x01]  Header Version
    byte[8]    AES Serial
