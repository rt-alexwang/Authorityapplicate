<template>
  <div class="detail-container">
    <div class="page-header">
      <el-button text @click="$router.push('/review')">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
      <h1>申請單詳細資訊</h1>
    </div>

    <div v-if="loading" class="loading-box">
      <el-skeleton :rows="8" animated />
    </div>

    <template v-if="app && !loading">
      <!-- Status Banner -->
      <el-alert
        :type="alertType"
        :closable="false"
        style="margin-bottom:20px"
      >
        <template #title>
          <span style="font-size:1rem">
            申請編號：APP-{{ String(app.id).padStart(6, '0') }}　
            狀態：<strong>{{ app.statusLabel }}</strong>
          </span>
        </template>
      </el-alert>

      <el-card style="margin-bottom:20px">
        <template #header><span class="card-title">申請人資訊</span></template>
        <el-descriptions border :column="2">
          <el-descriptions-item label="姓名">{{ app.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="信箱">{{ app.applicantEmail }}</el-descriptions-item>
          <el-descriptions-item label="部門">{{ app.department }}</el-descriptions-item>
          <el-descriptions-item label="申請時間">{{ formatDate(app.createdAt) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card style="margin-bottom:20px">
        <template #header><span class="card-title">申請內容</span></template>
        <el-descriptions border :column="2">
          <el-descriptions-item label="申請系統">{{ app.targetSystem }}</el-descriptions-item>
          <el-descriptions-item label="存取範圍">{{ app.accessScope }}</el-descriptions-item>
          <el-descriptions-item label="預計開始">{{ app.expectedStartDate }}</el-descriptions-item>
          <el-descriptions-item label="預計結束">{{ app.expectedEndDate || '無限期' }}</el-descriptions-item>
          <el-descriptions-item label="申請原因" :span="2">{{ app.accessReason }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- Review Result (if already reviewed) -->
      <el-card v-if="app.status !== 'PENDING'" style="margin-bottom:20px">
        <template #header><span class="card-title">審核結果</span></template>
        <el-descriptions border :column="2">
          <el-descriptions-item label="審核時間">{{ formatDate(app.reviewedAt) }}</el-descriptions-item>
          <el-descriptions-item label="審核意見">{{ app.reviewerComment || '—' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- PDF Download -->
      <el-card style="margin-bottom:20px">
        <el-button type="info" @click="downloadPdf">
          <el-icon><Download /></el-icon> 下載申請單 PDF
        </el-button>
      </el-card>

      <!-- Review Actions (only if pending) -->
      <el-card v-if="app.status === 'PENDING'">
        <template #header><span class="card-title">審核操作</span></template>
        <el-form label-position="top" size="default">
          <el-form-item label="審核意見（選填）">
            <el-input
              v-model="reviewComment"
              type="textarea"
              :rows="3"
              placeholder="請輸入審核意見或備註"
            />
          </el-form-item>
          <div class="review-btns">
            <el-button
              type="success"
              size="large"
              :loading="approving"
              @click="handleApprove"
            >核准通過</el-button>
            <el-button
              type="danger"
              size="large"
              :loading="rejecting"
              @click="handleReject"
            >拒絕申請</el-button>
          </div>
        </el-form>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { applicationApi } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const id = route.params.id

const loading = ref(true)
const app = ref(null)
const reviewComment = ref('')
const approving = ref(false)
const rejecting = ref(false)

const alertType = computed(() => ({
  PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger'
}[app.value?.status] || 'info'))

async function loadApp() {
  loading.value = true
  try {
    const { data } = await applicationApi.getById(id)
    app.value = data
  } catch (e) {
    ElMessage.error('載入失敗')
  } finally {
    loading.value = false
  }
}

async function handleApprove() {
  await ElMessageBox.confirm('確認核准此申請？', '核准確認', { type: 'success' })
  approving.value = true
  try {
    await applicationApi.approve(id, { comment: reviewComment.value })
    ElMessage.success('已核准')
    await loadApp()
  } catch (e) {
    ElMessage.error('操作失敗')
  } finally {
    approving.value = false
  }
}

async function handleReject() {
  await ElMessageBox.confirm('確認拒絕此申請？', '拒絕確認', { type: 'warning' })
  rejecting.value = true
  try {
    await applicationApi.reject(id, { comment: reviewComment.value })
    ElMessage.warning('已拒絕')
    await loadApp()
  } catch (e) {
    ElMessage.error('操作失敗')
  } finally {
    rejecting.value = false
  }
}

function downloadPdf() {
  window.open(applicationApi.getPdfUrl(id), '_blank')
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleString('zh-TW', { hour12: false }).slice(0, 16)
}

onMounted(loadApp)
</script>

<style scoped>
.detail-container { max-width: 860px; margin: 0 auto; padding: 24px 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 8px 0 0; color: #1a2f6e; }
.card-title { font-weight: bold; color: #2952a3; }
.review-btns { display: flex; gap: 12px; margin-top: 8px; }
.loading-box { padding: 40px 0; }
</style>
