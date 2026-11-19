# Bao cao thi nghiem: chon CUTOFF cho Mergesort

## 1. Muc tieu

Muc tieu cua thi nghiem la tim gia tri CUTOFF phu hop cho Mergesort top-down tren moi truong chay thu nghiem. Khi mang con co do dai khong lon hon CUTOFF, chuong trinh dung Insertion Sort thay vi tiep tuc chia de tri.

## 2. Moi truong thu nghiem

- He dieu hanh: Fedora Linux 44 (kernel 7.2.8)
- Java: OpenJDK 25.0.4
- Che do JVM: mixed mode
- CPU: 11th Gen Intel(R) Core(TM) i7-11850H @ 2.50GHz (8 nhan, 16 luong)
- RAM: 16GB

## 3. Cai dat thuat toan

`MergeSortWithCutoff` la Mergesort top-down tu cai dat. Moi lan goi de quy chia doan `[low..high]` lam hai nua, sort hai nua roi merge bang mot mang phu. Neu do dai doan khong vuot qua CUTOFF, chuong trinh goi Insertion Sort tren chinh doan do.

Implementation co toi uu bo qua merge neu `a[mid] <= a[mid + 1]`. Khong dung `Arrays.sort()` hay bat ky implementation sort thu vien nao de sap xep du lieu.

## 4. Thiet ke thi nghiem

- CUTOFF: 0, 2, 4, 6, 7, 8, 10, 12, 16, 20, 24, 32.
- Kich thuoc input: 1,000; 10,000; 100,000.
- Loai input: RANDOM, SORTED, REVERSED, FEW_UNIQUE.
- Moi cau hinh warm-up 5 lan va do 15 lan.
- Chi do thoi gian goi sort bang `System.nanoTime()`; khong tinh thoi gian tao/copy mang, kiem tra hay ghi file.
- Truoc khi ghi so lieu, chuong trinh kiem tra mang da duoc sap xep tang dan.
- Bao cao dung median de giam anh huong cua outlier, mean de doi chieu.

## 5. Ket qua

Chua co ket qua. Sau khi chay thi nghiem, chen file CSV, bieu do SVG va bang tong hop vao day.

## 6. Phan tich

Mergesort co chi phi de quy va merge. Voi mang con rat nho, Insertion Sort thuong nhanh hon do code ngan, locality tot va it overhead. Tuy nhien CUTOFF qua lon lam Insertion Sort xu ly nhieu phan tu hon, khi do chi phi bac hai tren cac mang con bat dau tang va thoi gian tong the co the xau di.

CUTOFF = 7 va CUTOFF = 10 la hai gia tri tham khao trong giao trinh. Thi nghiem cho phep kiem tra xem chung co phu hop voi may va phien ban Java dang dung hay khong, thay vi mac dinh mot gia tri chung.

## 7. Ket luan

Chua co ket luan. Chon CUTOFF de xuat sau khi da chay va phan tich ket qua.

## 8. Tai lieu nop kem

- Source: `src/`
- Script chay: `scripts/run-experiment.sh`
- Mo ta du lieu: `data/README.md`
- Log AI: `../ai-chat-log.md`
