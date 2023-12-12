# Micronaut-nats-test

File application.yml has the configuration stream "test-stream" with subject "test1.sub1".
Application progress:
1) When launched, the application creates a "test-stream" stream with subjects "test1.sub1" and "test1.sub2" in nats using the io.nats:jnats library.
2) Then it scans the "test-stream" stream and checks which subjects are present in it. The result is written to the before line.
3) A separate thread is launched with delay = 10s.
4) Micronaut is launched in the main thread.
5) In a separate stream, scans the “test-stream” stream and checks which subjects are present in it. The result is written to the after line.
6) The before and after values are output to the console.

Start Nats:
```
cd docker
docker-compose up
```

Start this program from any IDE!

Expected output:
```
before: [test1.sub1, test1.sub2]
after: [test1.sub1, test1.sub2]
```

Actual output:
```
before: [test1.sub1, test1.sub2]
after: [test1.sub1]
```
