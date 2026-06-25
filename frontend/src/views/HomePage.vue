<template>
  <div class="home-container">
    <div class="hero">
      <el-icon class="hero-icon"><Key /></el-icon>
      <h1>系統存取權限申請系統</h1>
      <p class="subtitle">PXMart Permission Application System</p>
    </div>
    <div class="card-row">
      <el-card class="entry-card" shadow="hover" @click="$router.push('/apply')">
        <el-icon size="48" color="#2952a3"><EditPen /></el-icon>
        <h2>申請權限</h2>
        <p>逐步填寫系統存取申請單，提交後進入審核流程</p>
        <el-button type="primary" size="large">開始申請</el-button>
      </el-card>
      <el-card class="entry-card" shadow="hover" @click="$router.push('/review')">
        <el-icon size="48" color="#2952a3"><List /></el-icon>
        <h2>審核管理</h2>
        <p>查看所有申請單，進行審核通過或拒絕操作</p>
        <el-button type="default" size="large">進入審核</el-button>
      </el-card>
    </div>
  </div>
</template>

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
.hero {
  text-align: center;
  margin-bottom: 48px;
}
.hero-icon {
  font-size: 64px;
  color: #2952a3;
  margin-bottom: 16px;
}
.hero h1 {
  font-size: 2rem;
  color: #1a2f6e;
  margin: 0 0 8px;
}
.subtitle {
  color: #666;
  font-size: 1rem;
}
.card-row {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  justify-content: center;
}
.entry-card {
  width: 300px;
  text-align: center;
  padding: 32px 20px;
  cursor: pointer;
  transition: transform 0.2s;
}
.entry-card:hover { transform: translateY(-4px); }
.entry-card h2 { margin: 16px 0 8px; color: #1a2f6e; }
.entry-card p { color: #666; font-size: 0.9rem; margin-bottom: 24px; }
</style>
