<template>
  <div class="success-container">
    <el-card class="success-card">
      <el-result
        icon="success"
        title="申請已成功送出！"
        sub-title="您的申請單已進入審核流程，請等待審核結果"
      >
        <template #extra>
          <div class="app-id-box">
            <span class="label">申請編號</span>
            <span class="id">APP-{{ String(appId).padStart(6, '0') }}</span>
          </div>
          <div class="btn-group">
            <el-button type="primary" size="large" @click="downloadPdf">
              <el-icon><Download /></el-icon> 下載申請單 PDF
            </el-button>
            <el-button size="large" @click="$router.push('/')">返回首頁</el-button>
            <el-button size="large" @click="$router.push('/apply')">再次申請</el-button>
          </div>
          <el-alert type="info" :closable="false" style="margin-top:20px;max-width:420px">
            請保存您的申請編號以便日後查詢。審核結果將通知申請人。
          </el-alert>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { applicationApi } from '../api/index.js'

const route = useRoute()
const appId = computed(() => route.query.id)

function downloadPdf() {
  window.open(applicationApi.getPdfUrl(appId.value), '_blank')
}
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  background: #f5f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.success-card { max-width: 560px; width: 100%; }
.app-id-box {
  background: #f0f4ff;
  border: 2px dashed #2952a3;
  border-radius: 8px;
  padding: 16px 32px;
  margin-bottom: 20px;
  display: inline-block;
}
.label { display: block; font-size: 0.85rem; color: #666; margin-bottom: 4px; }
.id { font-size: 1.6rem; font-weight: bold; color: #2952a3; letter-spacing: 2px; }
.btn-group { display: flex; gap: 12px; flex-wrap: wrap; justify-content: center; }
</style>
