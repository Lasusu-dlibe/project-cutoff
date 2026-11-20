# Báo cáo thí nghiệm: chọn CUTOFF cho Mergesort

## 1. Mục tiêu

Mục tiêu của thí nghiệm là tìm giá trị CUTOFF phù hợp cho Mergesort top-down trên cấu hình máy chạy thử. Khi độ dài mảng con không lớn hơn CUTOFF, chương trình dùng Insertion Sort thay vì tiếp tục chia nhỏ mảng.

## 2. Môi trường thí nghiệm

- Hệ điều hành: Fedora Linux 44 (kernel 7.2.8).
- Java: OpenJDK 25.0.4, JVM mixed mode.
- CPU: 11th Gen Intel Core i7-11850H @ 2.50 GHz (8 nhân, 16 luồng).
- RAM: 16 GB.

## 3. Cài đặt thuật toán

Class `MergeSortWithCutoff` cài đặt Mergesort top-down theo ký hiệu của giáo trình: mảng cần sắp xếp là `a`, mảng phụ là `aux`, và đoạn đang xử lý là `a[lo..hi]`. Mỗi lần gọi đệ quy chia đoạn tại `mid`, sắp xếp hai nửa rồi gọi `merge(a, aux, lo, mid, hi)` để trộn chúng.

Khi `hi <= lo + CUTOFF - 1`, chương trình chuyển sang Insertion Sort cho đoạn con. Code dùng `Comparable[]`, `less()` và `exch()`; không dùng thư viện để thực hiện thao tác sắp xếp. Chương trình cũng có cải tiến bỏ qua `merge` khi `a[mid] <= a[mid + 1]`, tức hai nửa đã nối đúng thứ tự.

## 4. Thiết kế thí nghiệm

`Benchmark` tạo lại cùng dữ liệu đầu vào bằng seed cố định cho mọi CUTOFF. Bốn kiểu dữ liệu là `RANDOM`, `SORTED`, `REVERSED` và `FEW_UNIQUE`; mỗi kiểu được thử với kích thước 1.000, 10.000 và 100.000 phần tử.

Với mỗi cấu hình, chương trình warm-up 5 lần, sau đó đo 15 lần bằng `System.nanoTime()`. Thời gian tạo/copy mảng, kiểm tra kết quả và ghi file không được tính vào thời gian sort. Sau mỗi lần chạy, chương trình kiểm tra mảng đã được sắp xếp tăng dần.

Vì một vài lần chạy có thể bị ảnh hưởng bởi JVM hoặc tác vụ hệ thống, chỉ số chính để so sánh là `median_ms`; `mean_ms`, `min_ms` và `max_ms` vẫn được ghi để đối chiếu.

### Vòng 1 — quét thô

Các CUTOFF được thử là: `0, 2, 4, 6, 7, 8, 10, 12, 16, 20, 24, 32`. CUTOFF 0 là mốc Mergesort gần như thuần; 7 và 10 là hai giá trị được giáo trình nhắc đến. Kết quả được lưu trong `results/results.csv` và biểu đồ trong `results/random-cutoff-chart.svg`.

### Vòng 2 — quét tinh

Từ vòng thô, các giá trị tốt tập trung gần 6–12. Vì vậy thí nghiệm quét từng giá trị từ 4 đến 14 và lặp độc lập ba lượt. Các file dữ liệu là:

- `results/fine-results.csv`
- `results/fine-results-run2.csv`
- `results/fine-results-run3.csv`

Biểu đồ của lượt tinh đầu tiên là `results/fine-random-cutoff-chart.svg`.

## 5. Kết quả

Phần phân tích chính dùng dữ liệu `RANDOM`, kích thước 100.000 phần tử vì đây là trường hợp đại diện cho input lớn, không có thứ tự ban đầu.

Ở vòng quét thô, sáu CUTOFF có `median_ms` thấp nhất là:

| CUTOFF | median_ms |
|---:|---:|
| 7 | 19.2671 |
| 8 | 19.2832 |
| 12 | 19.3015 |
| 6 | 19.4901 |
| 10 | 19.4954 |
| 4 | 19.7797 |

Ba lượt quét tinh cho kết quả sau. Cột cuối là trung vị của ba giá trị median từ ba lượt chạy.

| CUTOFF | Lượt 1 | Lượt 2 | Lượt 3 | Trung vị giữa các lượt (ms) |
|---:|---:|---:|---:|---:|
| 4 | 21.0388 | 17.9699 | 17.3110 | 17.9699 |
| 5 | 23.0042 | 17.9358 | 17.1751 | 17.9358 |
| 6 | 24.7639 | 17.5606 | 16.8589 | 17.5606 |
| 7 | 22.8205 | 18.1998 | 16.7356 | 18.1998 |
| 8 | 20.3108 | 17.5748 | 17.6378 | 17.6378 |
| 9 | 19.2988 | 17.5532 | 17.4919 | 17.5532 |
| 10 | 19.6757 | 17.5549 | 17.9000 | 17.9000 |
| 11 | 20.2271 | 17.5517 | 17.3029 | 17.5517 |
| 12 | 19.0445 | 17.2548 | 17.2372 | 17.2548 |
| 13 | 20.5766 | 17.1850 | 16.9910 | **17.1850** |
| 14 | 19.3364 | 17.2572 | 16.9746 | 17.2572 |

## 6. Phân tích

Mergesort có chi phí đệ quy và chi phí merge. Với đoạn con rất nhỏ, Insertion Sort có ít overhead hơn và tận dụng locality tốt hơn. Tuy nhiên, nếu CUTOFF quá lớn thì Insertion Sort phải xử lý các đoạn dài hơn, làm tăng chi phí bậc hai của các đoạn con.

Vòng thô cho thấy CUTOFF lớn như 20, 24 và 32 chậm hơn vùng 6–12; do đó việc quét tinh 4–14 là có cơ sở. Trong ba lượt tinh, CUTOFF 13 có trung vị giữa các lượt thấp nhất: 17.1850 ms. Tuy nhiên, CUTOFF 12 và 14 chỉ chênh lần lượt 0.0698 ms và 0.0722 ms, nhỏ hơn 0.5%. Ngoài ra, thời gian của từng lượt có dao động, nhiều khả năng do JVM, CPU scaling hoặc tác vụ nền.

Vì vậy, kết quả cho thấy vùng CUTOFF hiệu quả là khoảng 12–14; không nên coi 13 là một hằng số tối ưu cho mọi máy hay mọi dạng dữ liệu.

## 7. Kết luận

Trên cấu hình đã nêu, CUTOFF = 13 được đề xuất cho implementation Mergesort này, vì nó có trung vị giữa ba lượt quét tinh thấp nhất trên dữ liệu ngẫu nhiên kích thước 100.000. Tuy nhiên, CUTOFF 12 và 14 có hiệu năng gần tương đương. Kết luận chỉ áp dụng cho cấu hình máy, JVM, implementation và phương pháp đo trong báo cáo này.

## 8. Sản phẩm nộp kèm

- Source code: `src/`
- Script chạy: `scripts/run-experiment.sh`
- Mô tả bộ dữ liệu: `data/README.md`
- Kết quả thô/tinh và biểu đồ: `results/`
- Báo cáo: `report/report.md`
