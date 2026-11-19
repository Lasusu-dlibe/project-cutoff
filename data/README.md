# Bo du lieu thi nghiem

`DataGenerator.java` tao du lieu xac dinh (deterministic) tu seed `20261103`, nen co the tai lap ket qua tren cung moi truong.

| Pattern | Cach tao | Muc dich |
|---|---|---|
| RANDOM | so nguyen ngau nhien | truong hop tong quat |
| SORTED | 0, 1, 2, ... | mang da co thu tu |
| REVERSED | n, n-1, ... | mang nguoc thu tu |
| FEW_UNIQUE | 10 gia tri lap lai | nhieu key trung |

Kich thuoc mac dinh: 1,000; 10,000; 100,000. Thay doi hang `SIZES` trong `Benchmark.java` neu can them du lieu.
