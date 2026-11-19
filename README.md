# Mergesort CUTOFF Experiment

Project thi nghiem tim CUTOFF phu hop de chuyen tu Mergesort sang Insertion Sort.

## Chay

Can JDK 17+ (JDK 11 cung duoc):

```bash
./scripts/run-experiment.sh
```

Ket qua nam o `results/results.csv`, bieu do SVG nam o `results/random-cutoff-chart.svg`.

## Thiet ke

- `MergeSortWithCutoff.java`: Mergesort top-down tu cai dat, co mang phu va toi uu bo qua merge khi hai nua da co thu tu.
- `DataGenerator.java`: tao input RANDOM, SORTED, REVERSED, FEW_UNIQUE.
- `Benchmark.java`: warm-up 5 lan, do 15 lan cho moi cap pattern/size/cutoff; kiem tra ket qua sort truoc khi ghi CSV.
- `ChartGenerator.java`: tao bieu do SVG tu ket qua RANDOM input, khong can thu vien ngoai.

Thay doi `CUTOFFS`, `SIZES`, `WARMUP_RUNS`, `MEASURED_RUNS` trong `Benchmark.java` de phu hop may cua ban.

## Luu y bao cao

Dung `median_ms` lam so lieu chinh neu co outlier; `mean_ms` dung de doi chieu. Chi chon cutoff sau khi xem nhieu kich thuoc va nhieu loai input.
