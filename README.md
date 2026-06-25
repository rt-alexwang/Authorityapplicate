# 權限申請系統

系統存取權限申請與審核平台 (Vue 3 + Spring Boot + H2/MSSQL)

## 啟動方式

### 後端 (http://192.168.82.225:8090)
```
雙擊 start-backend.bat
```
或手動：
```
"C:\Program Files\Java\jdk-21.0.11\bin\java.exe" ^
  --patch-module java.base=D:\Users\alex_wang\Documents\patch ^
  -Djava.net.preferIPv4Stack=true ^
  -jar backend\target\permission-system-1.0.0.jar
```

若 JAR 不存在，先 build：
```
cd backend
set PATH=C:\apache-maven-3.9.16\bin;%PATH%
mvn package -DskipTests
```

### 前端 (http://192.168.82.225:5173)
```
雙擊 start-frontend.bat
```
或手動：
```
cd frontend
npm run dev
```

## 功能頁面

| URL | 說明 |
|-----|------|
| http://192.168.82.225:5173/ | 首頁入口 |
| http://192.168.82.225:5173/apply | 逐步申請精靈（4 步驟）|
| http://192.168.82.225:5173/apply/success | 申請完成，下載 PDF |
| http://192.168.82.225:5173/review | 審核管理列表 |
| http://192.168.82.225:5173/review/:id | 審核詳細頁（核准/拒絕）|

## H2 資料庫 Console（開發用）
http://192.168.82.225:8090/h2-console  
JDBC URL: `jdbc:h2:mem:permdb`

## 切換 MSSQL
編輯 `backend/src/main/resources/application.yml`，
移除 H2 設定區塊並取消 MSSQL 區塊的註解，填入連線資訊後重新 build。

## 注意事項
`--patch-module java.base=D:\Users\alex_wang\Documents\patch` 是必要參數，
用於修正此機器 Windows Unix Domain Socket 的 JDK 21 相容性問題。
