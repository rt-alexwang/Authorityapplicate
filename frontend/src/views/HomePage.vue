<template>
  <div class="home-container">
    <div class="hero">
      <el-icon class="hero-icon"><Key /></el-icon>
      <h1>系統存取權限申請系統</h1>
      <p class="subtitle">PXMart Permission Application System</p>
    </div>

    <!-- H8: 申請流程說明 — 讓使用者進入前就知道全貌 -->
    <div class="flow-bar">
      <div class="flow-step">
        <el-icon size="22" color="#2952a3"><EditPen /></el-icon>
        <span>填寫申請單</span>
      </div>
      <el-icon class="arrow"><ArrowRight /></el-icon>
      <div class="flow-step">
        <el-icon size="22" color="#2952a3"><Bell /></el-icon>
        <span>進入審核</span>
      </div>
      <el-icon class="arrow"><ArrowRight /></el-icon>
      <div class="flow-step">
        <el-icon size="22" color="#2952a3"><CircleCheck /></el-icon>
        <span>通知結果</span>
      </div>
    </div>

    <div class="card-row">
      <!-- H6: 卡片說明使用者「需要什麼」才能開始，而非只說「能做什麼」 -->
      <el-card class="entry-card" shadow="hover" @click="$router.push('/apply')">
        <el-icon size="48" color="#2952a3"><EditPen /></el-icon>
        <h2>申請權限</h2>
        <p>填寫申請人資訊、申請帳號及所需 DB 物件權限，送出後等待審核人員審核。</p>
        <el-tag type="info" size="small" style="margin-bottom:16px">約需 3–5 分鐘</el-tag>
        <br />
        <el-button type="primary" size="large">開始申請</el-button>
      </el-card>

      <el-card class="entry-card" shadow="hover" @click="$router.push('/my-records')">
        <el-icon size="48" color="#67c23a"><Search /></el-icon>
        <h2>查詢我的權限</h2>
        <p>輸入姓名或帳號，查看所有申請紀錄及目前已核准的資料庫物件存取權限。</p>
        <el-tag type="success" size="small" style="margin-bottom:16px">申請人使用</el-tag>
        <br />
        <el-button type="success" size="large">查詢權限</el-button>
      </el-card>

      <el-card class="entry-card" shadow="hover" @click="$router.push('/review')">
        <el-icon size="48" color="#e6a23c"><List /></el-icon>
        <h2>審核管理</h2>
        <p>查看所有待審核、已核准、已拒絕的申請單，進行審核並填寫審核意見。</p>
        <el-tag type="warning" size="small" style="margin-bottom:16px">僅審核人員使用</el-tag>
        <br />
        <el-button size="large">進入審核</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { Key, EditPen, List, ArrowRight, Bell, CircleCheck, Search } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0f4ff 0%, #e8eeff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.hero { text-align: center; margin-bottom: 28px; }
.hero-icon { font-size: 64px; color: #2952a3; margin-bottom: 16px; }
.hero h1 { font-size: 2rem; color: #1a2f6e; margin: 0 0 8px; }
.subtitle { color: #666; font-size: 1rem; }

.flow-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 32px;
  padding: 12px 28px;
  margin-bottom: 36px;
  box-shadow: 0 2px 8px rgba(41,82,163,.1);
}
.flow-step { display: flex; align-items: center; gap: 6px; font-size: 0.9rem; color: #2952a3; }
.arrow { color: #aaa; font-size: 14px; }

.card-row { display: flex; gap: 32px; flex-wrap: wrap; justify-content: center; }
.entry-card {
  width: 300px;
  text-align: center;
  padding: 32px 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.entry-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(41,82,163,.15); }
.entry-card h2 { margin: 16px 0 8px; color: #1a2f6e; }
.entry-card p { color: #666; font-size: 0.88rem; margin-bottom: 12px; line-height: 1.6; }
</style>
