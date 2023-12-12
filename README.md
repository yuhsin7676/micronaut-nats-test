# Micronaut-nats-test

For issue!

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
