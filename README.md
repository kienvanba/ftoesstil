# Fossil Test

A simple test that contains 2 android library: 
- Logger: help with android logging. 
- Protocol Buffer Decoder: decode protobuf encoded message. 

## Logger

A simple logger that can: 
- [x] support multi log types: `info`, `warning`, `debug`, `wtf`, `error`. 
- [x] show time when log is called with customizable date time pattern. 
- [x] show thread id. 
- [x] support log `file`, `array`, `throwable`. 
- [x] show `stacktrace` of where the log is being called. 
- [x] customizable tag. 

### Usage

Setup the Logger with it's config:
```Kotlin
val config = BnazConfig(
  traceEnabled = true, 
  dateTimePattern = "yyyy-MM-dd HH:mm:ss", 
  showThreadId = true, 
  logEnabled = BuildConfig.DEBUG
)
Bnaz.setup(config)
```
Use it like normal log:
```Kotlin
Bnaz.i("test")
// normal log with default tag is the class name, with date time, thread id and stacktrace

Bnaz.tag("custom tag").d("test)
// log with custom tag

Bnaz.datePattern("HH:mm").e("test pattern")
// log with custom date pattern 

Bnaz.wtf(arrayOf(1, 2, 3, 4, 5))
// [1, 2, 3, 4, 5]

Bnaz.i(file)
// log file info (path, exsitance...)
```

## Protocol Buffer Decoder

A decoder that decode Protobuf encoded message
- [x] decode `varint` type. 
- [ ] decode `64bit` type.
- [x] decode `length-delimited` type. 

### Usage

```Kotlin
ProtoBuf.decode("0a0d08f92712024f4b188a8c06204e120774657374696e67")
/*
{
  embedded message=1: {
    int=1: 5113
    string=2: "OK"
    int=3: 99850
    int=4: 78
  }
  string=2: "testing"
}
*/
```

### Code Structure

```
- Android App           # android application
- Logger Library:       # logger module
  - Bnaz, BnazConfig    # logger and it's config
- ProtoBuf Library:     # protocol buffer decoded module
  - ProtoBuf            # decoder
```
