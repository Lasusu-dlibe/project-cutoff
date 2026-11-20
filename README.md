# Thí nghiệm chọn CUTOFF cho Mergesort

Project DSA tìm giá trị CUTOFF phù hợp khi kết hợp Mergesort top-down với Insertion Sort. Khi độ dài mảng con không lớn hơn CUTOFF, chương trình dùng Insertion Sort; các đoạn lớn hơn tiếp tục được chia và trộn bằng Mergesort.

## Kết quả tóm tắt

Trên cấu hình đã ghi trong báo cáo, vùng CUTOFF có hiệu năng tốt là **12–14**. CUTOFF **13** được đề xuất dựa trên trung vị của ba lượt quét tinh, nhưng 12 và 14 có kết quả rất gần nhau.

Xem bảng số liệu, biểu đồ và giới hạn kết luận tại [report/report.md](report/report.md).

## Yêu cầu

- JDK 11 trở lên (thí nghiệm được chạy bằng OpenJDK 25).
- Bash trên Linux/macOS để chạy script có sẵn.

## Chạy lại vòng quét thô

```bash
./scripts/run-experiment.sh
```

Lệnh này biên dịch source vào thư mục tạm `out/`, chạy các CUTOFF thô `0, 2, 4, 6, 7, 8, 10, 12, 16, 20, 24, 32`, rồi tạo:

- `results/results.csv`: bảng kết quả đầy đủ.
- `results/random-cutoff-chart.svg`: biểu đồ median của dữ liệu ngẫu nhiên.

`out/` là file build tự sinh; không phải sản phẩm cần nộp và được Git bỏ qua.

## Chạy vòng quét tinh

Sau khi vòng thô cho thấy vùng ứng viên, có thể truyền tên file đầu ra và danh sách CUTOFF cho `Benchmark`:

```bash
javac -d out src/*.java
java -cp out Benchmark results/fine-results.csv "4,5,6,7,8,9,10,11,12,13,14"
java -cp out ChartGenerator results/fine-results.csv results/fine-random-cutoff-chart.svg
```

Để kiểm tra độ ổn định, chạy lại cùng danh sách và đổi tên output, ví dụ `fine-results-run2.csv` và `fine-results-run3.csv`.

## Cấu trúc project

- `src/MergeSortWithCutoff.java`: Mergesort top-down tự cài đặt, có mảng phụ `aux`, CUTOFF và Insertion Sort.
- `src/DataGenerator.java`: tạo dữ liệu `RANDOM`, `SORTED`, `REVERSED`, `FEW_UNIQUE` bằng seed cố định.
- `src/Benchmark.java`: warm-up 5 lần, đo 15 lần, kiểm tra kết quả sắp xếp và ghi CSV.
- `src/ChartGenerator.java`: tạo biểu đồ SVG từ cột `median_ms` của dữ liệu `RANDOM`.
- `scripts/run-experiment.sh`: biên dịch và chạy vòng quét thô mặc định.
- `results/`: CSV và biểu đồ của các lượt thí nghiệm.
- `report/report.md`: báo cáo thí nghiệm đầy đủ.

## Đọc kết quả

Ưu tiên cột `median_ms` để chọn CUTOFF vì nó ít bị ảnh hưởng bởi một lần chạy chậm bất thường hơn `mean_ms`. Mỗi lần benchmark phải dùng lại cùng input gốc; chương trình đã copy mảng trước mỗi lần đo để bảo đảm công bằng.
