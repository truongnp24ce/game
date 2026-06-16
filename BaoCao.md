# Báo cáo Dự án Lập trình Thiết bị Di động (Android – Kotlin)

**Project Title:** Animal Villa
**Repository:** [truongnp24ce/game](https://github.com/truongnp24ce/game)
**Module chính:** `Mobile-Device-Programming-Animal-Villa`
**Ngôn ngữ:** Kotlin (100%)
**Ngày:** 2026-06-16

---

## 1. Giới thiệu

### 1.1 Bối cảnh (Background)

**Animal Villa** là một ứng dụng game di động dạng *visual-novel / life-simulation* trên nền tảng Android, được phát triển bằng Kotlin và Jetpack Compose. Người chơi vào vai một cư dân mới chuyển đến thị trấn Animal Villa, tương tác với các nhân vật động vật xung quanh qua một tuần (Thứ Hai → Chủ Nhật) và đưa ra các lựa chọn để dẫn đến một trong bốn kết thúc khác nhau. Ứng dụng tích hợp với **Firebase** (Authentication + Firestore + Storage) để quản lý tài khoản người dùng và lưu trữ tiến trình chơi trên đám mây.

### 1.2 Mục tiêu (Objectives)

- Xây dựng một ứng dụng Android đơn module sử dụng **Kotlin + Jetpack Compose**.
- Cung cấp cơ chế **đăng ký / đăng nhập** tài khoản người chơi qua Firebase Authentication.
- Hỗ trợ **lưu/tải tiến trình** chơi tự động lên Firestore và lưu trữ cục bộ bằng Room.
- Triển khai **gameplay theo lựa chọn (swipe / tap left-right)** với hệ thống chỉ số (Energy ❤️, Status 🔥, Money 💲) ảnh hưởng đến cốt truyện.
- Cung cấp **nhiều kết thúc (4 endings)** dựa trên chỉ số cuối cùng của người chơi.

### 1.3 Phạm vi (Scope)

- Ứng dụng chạy trên thiết bị Android (**minSdk 32, targetSdk 34, compileSdk 34**).
- Hướng đến chế độ portrait, một người chơi (single-player), chơi offline-first và sync khi có mạng.
- Phạm vi gồm các thành phần: `app` (Activity, ViewModel), `DAO`, `DTO`, `JSON` (kịch bản truyện), `Service`, `UI` (Compose screens).
- Phụ thuộc backend: **Firebase Firestore + Firebase Storage + Firebase Authentication** (cấu hình qua `google-services.json`).

---

## 2. Yêu cầu Hệ thống

### 2.1 Yêu cầu Chức năng (Functional Requirements)

| ID  | Function                  | Description                                                                                          |
| --- | ------------------------- | ---------------------------------------------------------------------------------------------------- |
| F1  | User Registration         | Người dùng tạo tài khoản qua màn `RegistrationActivity` (Firebase Auth + lưu profile lên Firestore). |
| F2  | Login                     | Người dùng đăng nhập tại `LoginActivity` để mở khóa lưu tiến trình đám mây.                          |
| F3  | Title Screen / Start Game | Màn `TitleScreenActivity` cho phép bắt đầu game mới hoặc tiếp tục từ save gần nhất.                  |
| F4  | Gameplay & Choices        | `GamePlayModel` hiển thị prompt + 2 lựa chọn (left/right), cập nhật chỉ số Energy/Status/Money.      |
| F5  | Save & Load Progress      | `GameSave` + `MainViewModel` lưu / khôi phục tiến trình qua Room (local) và Firestore (cloud).       |
| F6  | Multiple Endings          | Cuối tuần (Sunday) trigger 1 trong 4 ending dựa trên chỉ số cuối: Bad / Exhausted / Penniless / Good. |

> **Chi tiết các lựa chọn dẫn tới từng ending** được mô tả đầy đủ trong [README.md của module](https://github.com/truongnp24ce/game/blob/main/Mobile-Device-Programming-Animal-Villa/README.md#endings).

### 2.2 Yêu cầu Phi chức năng (Non-Functional Requirements)

| ID   | Requirement       | Description                                                                                                                              |
| ---- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| NF1  | Performance       | UI Compose phản hồi mượt khi chuyển prompt; chỉ số stat cập nhật realtime trên màn hình.                                                |
| NF2  | Security          | Xác thực qua Firebase Authentication; mật khẩu được Firebase hash & lưu an toàn.                                                         |
| NF3  | Usability         | Giao diện đơn giản, 2 nút lựa chọn (trái/phải), portrait, dễ thao tác bằng một tay.                                                      |
| NF4  | Compatibility     | Hỗ trợ Android 12L+ (minSdk 32, targetSdk 34). JVM target 11.                                                                            |
| NF5  | Maintainability   | Mã nguồn chia tầng (DAO / DTO / Service / UI / ViewModel) theo hướng MVVM + Koin DI.                                                     |
| NF6  | Storyboard Driven | Kịch bản truyện được tách riêng ra các file JSON trong `app/src/main/java/app/JSON`, dễ chỉnh sửa nội dung mà không cần build lại logic. |

---

## 3. Thiết kế Hệ thống (System Design)

### 3.1 Kiến trúc Hệ thống (System Architecture)

Animal Villa được tổ chức theo mô hình **MVVM (Model–View–ViewModel)** trên một module duy nhất (`:app`), với phụ thuộc được quản lý qua **Koin (Dependency Injection)**.

**Các tầng chính:**

- **UI Layer** (`app/src/main/java/app/UI` + các `*Activity.kt`): màn hình viết bằng **Jetpack Compose**, sử dụng `androidx.navigation:navigation-compose` cho điều hướng và `constraintlayout-compose` cho layout phức tạp.
- **ViewModel Layer** (`MainViewModel.kt`, `GamePlayModel.kt`): giữ state của game (chỉ số người chơi, prompt hiện tại) và xử lý logic phản hồi lựa chọn.
- **Domain / Logic** (`AppMethods.kt`, `GetInformation.kt`, `GameSave.kt`): các hàm nghiệp vụ — đọc dữ liệu prompt, áp dụng thay đổi stat, quyết định ending.
- **Data Layer**:
  - `DAO/` — Room DAO cho persistence cục bộ.
  - `DTO/` — Data Transfer Objects (Prompt, Player, Character...).
  - `JSON/` — Tài nguyên nội dung (kịch bản các ngày trong tuần).
  - `Service/` — Network / Firebase wrappers (Retrofit, Firestore, Storage).
- **DI** (`AppModule.kt`, `AnimalVillaApplication.kt`): khởi tạo Koin module cho toàn ứng dụng.

**Runtime flow:**
`Compose UI → ViewModel (GamePlayModel) → Logic (AppMethods/GameSave) → DAO/Service → Room (local) + Firebase Firestore/Storage (cloud)`

### 3.2 Thiết kế Giao diện (UI Design)

| Screen              | Description                                                                                   |
| ------------------- | --------------------------------------------------------------------------------------------- |
| Login Screen        | Đăng nhập bằng email/mật khẩu qua Firebase Auth (`LoginActivity`).                            |
| Registration Screen | Đăng ký tài khoản mới (`RegistrationActivity`).                                               |
| Title Screen        | Màn hình tiêu đề, vào game mới hoặc tải save (`TitleScreenActivity`).                         |
| Main / Home Screen  | Entry point sau khi launcher mở (`MainActivity` — khai báo `LAUNCHER` trong AndroidManifest). |
| Gameplay Screen     | Hiển thị prompt + 2 lựa chọn + 3 chỉ số (Energy ❤️, Status 🔥, Money 💲) (`GamePlayModel`).      |
| Ending Screen       | Hiển thị 1 trong 4 ending; nút *Back to Start* quay lại Title.                                |

**Ghi chú UI/UX:**

- Mọi Activity đều khóa hướng `screenOrientation="portrait"`.
- Chỉ `MainActivity` được export làm LAUNCHER, các Activity khác `exported="false"` để bảo mật.

### 3.3 Thiết kế Dữ liệu (Database Design)

Dữ liệu được lưu ở **2 lớp**:

- **Cục bộ (Local):** Room (`androidx.room:room-ktx:2.4.2`) — lưu save game gần nhất, cho phép chơi offline.
- **Đám mây (Cloud):** Firebase **Firestore** lưu profile + save game; Firebase **Storage** dùng cho asset (ảnh nhân vật, lưu file lớn nếu cần).

**Các thực thể chính (DTO):**

| Entity        | Key Fields                                                  | Description                                               |
| ------------- | ----------------------------------------------------------- | --------------------------------------------------------- |
| Player        | `userId`, `displayName`, `energy`, `status`, `money`        | Trạng thái người chơi hiện tại.                           |
| GameSave      | `userId`, `day`, `promptIndex`, `stats`                     | Snapshot tiến trình để khôi phục.                         |
| Prompt (JSON) | `id`, `day`, `text`, `leftChoice`, `rightChoice`, `effects` | Một bước truyện + ảnh hưởng đến chỉ số.                   |
| Character     | `id`, `name`, `metAt`                                       | Thông tin nhân vật người chơi đã gặp.                     |
| Ending        | `type`, `priority`, `condition`                             | Định nghĩa 4 endings: Bad / Exhausted / Penniless / Good. |

**Logic chọn Ending (theo README):**

| Priority | Ending       | Trigger (final stat)                       |
| -------- | ------------ | ------------------------------------------ |
| 1        | Bad ❤️‍🔥       | `Status < 30`                              |
| 2        | Exhausted ❤️  | `Status ≥ 30` và `Energy < 30`             |
| 3        | Penniless 💲  | `Status ≥ 30`, `Energy ≥ 30`, `Money < 40` |
| 4        | Good ✨       | Cả 3 chỉ số vượt ngưỡng                    |

---

## 4. Phát triển Hệ thống (System Development)

### 4.1 Công nghệ sử dụng (Technologies Used)

| Component            | Technology                                                                                                                      |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| Programming Language | Kotlin 1.9.20                                                                                                                   |
| Build System         | Gradle (Groovy DSL), Android Gradle Plugin 8.1.4                                                                                |
| Java Target          | JVM 11                                                                                                                          |
| IDE                  | Android Studio                                                                                                                  |
| Architecture         | MVVM (single-module)                                                                                                            |
| UI Framework         | Jetpack Compose 1.5.4 + Material                                                                                                |
| Navigation           | androidx.navigation:navigation-compose 2.5.1                                                                                    |
| Dependency Injection | Koin (`io.insert-koin:koin-android:3.2.0`)                                                                                      |
| Local Database       | Room 2.4.2                                                                                                                      |
| Networking           | Retrofit 2.9.0 + OkHttp 5.0.0-alpha + Gson converter                                                                            |
| Backend / Auth       | Firebase BoM 30.1.0 (Authentication, Firestore, Storage, Analytics) + FirebaseUI-Auth 8.0.1 + Google Play Services Auth 20.2.0 |
| Background Work      | WorkManager 2.7.1                                                                                                               |
| Responsive Sizing    | `com.intuit.sdp:sdp-android:1.0.6`                                                                                              |
| Testing              | JUnit 4.13.2, MockK 1.12.4, kotlinx-coroutines-test 1.6.3, Espresso 3.4.0, Compose UI Test                                      |
| Version Control      | Git / GitHub                                                                                                                    |

### 4.2 Các API & Service chính

| API / Service               | Mục đích                                                |
| --------------------------- | ------------------------------------------------------- |
| Firebase Authentication     | Đăng ký / đăng nhập người chơi.                         |
| Firebase Firestore          | Lưu profile và save game trên cloud.                    |
| Firebase Storage            | Lưu trữ asset (ảnh nhân vật, file lớn).                 |
| Room DAO (local)            | Lưu cache save game để chơi offline.                    |
| Retrofit + OkHttp (network) | Sẵn sàng cho gọi REST API mở rộng (nếu có).             |

### 4.3 Thư viện chính (Libraries)

| Library                                            | Purpose                              |
| -------------------------------------------------- | ------------------------------------ |
| Jetpack Compose + Material                         | UI declarative                       |
| Navigation Compose                                 | Điều hướng giữa các màn              |
| Koin                                               | Dependency Injection                 |
| Retrofit + OkHttp + Gson Converter                 | HTTP client + serialization          |
| Room                                               | Lưu trữ cục bộ                       |
| Firebase BoM (Auth, Firestore, Storage, Analytics) | Backend-as-a-Service                 |
| WorkManager                                        | Đồng bộ nền (sync save khi có mạng)  |
| ConstraintLayout (Compose)                         | Layout phức tạp                      |
| SDP-Android                                        | Đơn vị kích thước responsive         |
| JUnit / MockK / Espresso                           | Unit test & UI test                  |

---

## 5. Triển khai (Deployment)

### 5.1 Mã nguồn (Source Code)

- Repository: **[truongnp24ce/game](https://github.com/truongnp24ce/game)** (branch `main`)
- Module Android: [`Mobile-Device-Programming-Animal-Villa/`](https://github.com/truongnp24ce/game/tree/main/Mobile-Device-Programming-Animal-Villa)
- File entry: [`AndroidManifest.xml`](https://github.com/truongnp24ce/game/blob/main/Mobile-Device-Programming-Animal-Villa/app/src/main/AndroidManifest.xml) → LAUNCHER là `app.MainActivity`.

### 5.2 Build & chạy thử

1. Clone repository:

   ```bash
   git clone https://github.com/truongnp24ce/game.git
   ```

2. Mở thư mục `Mobile-Device-Programming-Animal-Villa` bằng Android Studio (Giraffe trở lên, AGP 8.1.4).
3. Đảm bảo có file `app/google-services.json` (đã có sẵn trong repo cho cấu hình Firebase mặc định) — **khuyến nghị thay bằng cấu hình Firebase của riêng bạn cho môi trường production**.
4. Đồng bộ Gradle, sau đó **Run** trên emulator hoặc thiết bị Android ≥ 12L (API 32).

### 5.3 Hướng dẫn cài đặt (Installation Guide)

1. Build APK debug:

   ```bash
   cd Mobile-Device-Programming-Animal-Villa
   ./gradlew assembleDebug
   ```

2. APK xuất tại: `app/build/outputs/apk/debug/app-debug.apk`.
3. Trên điện thoại: bật *Install from unknown sources* và mở APK, hoặc dùng:

   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 6. Kết luận (Conclusion)

### 6.1 Thành tựu (Achievements)

- Hoàn thành một **game visual-novel hoàn chỉnh** trên Android với 4 endings và hệ thống chỉ số 3 chiều (Energy / Status / Money).
- Áp dụng đầy đủ **stack hiện đại**: Kotlin + Jetpack Compose + MVVM + Koin DI + Room + Firebase.
- Tách kịch bản truyện ra file **JSON** ngoài code, hỗ trợ chỉnh sửa nội dung linh hoạt.
- Tích hợp **Firebase Authentication + Firestore** cho đăng nhập và lưu tiến trình đám mây.

### 6.2 Hạn chế (Limitations)

- File `google-services.json` được commit thẳng vào repo — nên thay bằng biến môi trường hoặc loại khỏi VCS ở production.
- Một số thư viện đang dùng phiên bản cũ (Retrofit 2.9.0, OkHttp 5.0.0-alpha, Room 2.4.2) — nên cân nhắc nâng cấp.
- Chưa có CI/CD pipeline (chỉ có thư mục `.github/` rỗng) và chưa có release build ký số (release dùng `minifyEnabled false`).
- Mô tả repository hiện tại (`jskdhfsjhdbdksbsdjk`) cần được cập nhật thành mô tả có ý nghĩa.

### 6.3 Hướng phát triển (Future Work)

- Thêm **CI/CD** với GitHub Actions (build + test + lint + assembleRelease ký số).
- Bật **R8/ProGuard** ở release build (`minifyEnabled true`) để giảm dung lượng APK.
- Bổ sung **unit test cho `GamePlayModel`** (logic chọn ending) và **UI test cho Compose screens**.
- Bổ sung **chế độ chơi offline hoàn toàn** với đồng bộ Firestore qua WorkManager.
- Thêm **đa ngôn ngữ (i18n)** và **chế độ tối (dark theme)** cho Compose.
- Quản lý bí mật Firebase qua `local.properties` hoặc GitHub Secrets thay vì commit `google-services.json`.

---

> 📌 **Ghi chú:** Báo cáo này được sinh tự động dựa trên cấu trúc mẫu báo cáo SmartHomeApp và nội dung thực tế của repository [truongnp24ce/game](https://github.com/truongnp24ce/game).
