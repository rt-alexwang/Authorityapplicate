<template>
  <div class="success-container">
    <el-card class="success-card">
      <el-result icon="success" title="申請已成功送出！">
        <template #sub-title>
          您的申請單已進入審核流程。申請編號如下，請妥善保存。
        </template>
        <template #extra>
          <!-- H1: 清楚顯示申請編號 -->
          <div class="app-id-box">
            <span class="label">申請編號</span>
            <span class="id">APP-{{ String(appId).padStart(6, '0') }}</span>
          </div>

          <!-- H10: 清楚說明下一步流程 -->
          <el-steps direction="vertical" :active="1" class="next-steps">
            <el-step title="申請已送出" description="您剛完成的步驟" status="finish" />
            <el-step title="等待審核" description="審核人員將審閱您的申請內容" status="process" />
            <el-step title="通知結果" description="審核完成後將通知申請人" status="wait" />
          </el-steps>

          <div class="btn-group">
            <el-button type="primary" size="large" @click="downloadPdf">
              <el-icon><Download /></el-icon> 下載申請單 PDF
            </el-button>
            <el-button size="large" @click="$router.push('/')">返回首頁</el-button>
            <el-button size="large" plain @click="$router.push('/apply')">再次申請</el-button>
          </div>
        </template>
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Download } from '@element-plus/icons-vue'
import { applicationApi } from '../api/index.js'

const route = useRoute()
const router = useRouter()
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
.success-card { max-width: 520px; width: 100%; }
.app-id-box {
  background: #f0f4ff;
  border: 2px dashed #2952a3;
  border-radius: 8px;
  padding: 16px 40px;
  margin-bottom: 24px;
  display: inline-block;
}
.label { display: block; font-size: 0.85rem; color: #666; margin-bottom: 4px; }
.id { font-size: 1.6rem; font-weight: bold; color: #2952a3; letter-spacing: 2px; }
.next-steps { text-align: left; margin: 0 auto 24px; max-width: 280px; }
.btn-group { display: flex; gap: 10px; flex-wrap: wrap; justify-content: center; }
</style>
