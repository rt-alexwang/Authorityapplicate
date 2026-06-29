<template>
  <div class="detail-container">
    <div class="page-header">
      <el-button text @click="$router.push('/review')">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
      <h1>申請單詳細資訊</h1>
    </div>

    <div v-if="loading" class="loading-box"><el-skeleton :rows="8" animated /></div>

    <template v-if="app && !loading">
      <!-- H1: 狀態橫幅清楚顯示目前狀態 -->
      <el-alert :type="alertType" :closable="false" style="margin-bottom:20px" show-icon>
        <template #title>
          <span style="font-size:1rem">
            申請編號：<strong>APP-{{ String(app.id).padStart(6, '0') }}</strong>
            &nbsp;｜&nbsp;狀態：<strong>{{ app.statusLabel }}</strong>
            <span v-if="app.reviewedAt" style="font-weight:normal;margin-left:12px;font-size:0.9rem">
              審核於 {{ formatDate(app.reviewedAt) }}
            </span>
          </span>
        </template>
      </el-alert>

      <!-- H5: 待審核時顯示提醒 -->
      <el-alert v-if="app.status === 'PENDING'" type="warning" :closable="false" show-icon
        title="此申請單尚未審核，請審閱下方內容後進行核准或拒絕。" style="margin-bottom:20px" />

      <el-card style="margin-bottom:16px">
        <template #header><span class="card-title">基本資訊</span></template>
        <el-descriptions border :column="2">
          <el-descriptions-item label="申請人">{{ app.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="申請時間">{{ formatDate(app.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="申請項目" :span="2">{{ app.requestItem }}</el-descriptions-item>
          <el-descriptions-item label="需求說明" :span="2">
            {{ app.description || '（未填）' }}
          </el-descriptions-item>
          <el-descriptions-item label="申請帳號" :span="2">
            <span style="white-space:pre-wrap">{{ app.requestAccounts || '（未填）' }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card style="margin-bottom:16px">
        <template #header>
          <span class="card-title">權限明細</span>
          <!-- H1: 顯示幾筆明細 -->
          <el-tag size="small" style="margin-left:8px">共 {{ app.rows?.length || 0 }} 筆</el-tag>
        </template>
        <div class="table-scroll">
          <table class="perm-table">
            <thead>
              <tr>
                <th>#</th>
                <th colspan="2">本機伺服器</th>
                <th colspan="2">遠端伺服器</th>
                <th colspan="2">Object</th>
                <th colspan="6">權限</th>
              </tr>
              <tr>
                <th></th>
                <th>IP</th><th>DB</th>
                <th>IP</th><th>DB</th>
                <th>Type</th><th>Name</th>
                <th>Select</th><th>Insert</th><th>Update</th><th>Delete</th><th>Execute</th><th>Alter</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in app.rows" :key="idx">
                <td class="row-num">{{ idx + 1 }}</td>
                <td>{{ row.localIp }}</td><td>{{ row.localDb }}</td>
                <td>{{ row.remoteIp }}</td><td>{{ row.remoteDb }}</td>
                <td>{{ row.objectType }}</td><td>{{ row.objectName }}</td>
                <td class="check-td check-yes">{{ row.canSelect ? 'V' : '' }}</td>
                <td class="check-td check-yes">{{ row.canInsert ? 'V' : '' }}</td>
                <td class="check-td check-yes">{{ row.canUpdate ? 'V' : '' }}</td>
                <td class="check-td check-yes">{{ row.canDelete ? 'V' : '' }}</td>
                <td class="check-td check-yes">{{ row.canExecute ? 'V' : '' }}</td>
                <td class="check-td check-yes">{{ row.canAlter ? 'V' : '' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </el-card>

      <el-card v-if="app.status !== 'PENDING'" style="margin-bottom:16px">
        <template #header><span class="card-title">審核結果</span></template>
        <el-descriptions border :column="2">
          <el-descriptions-item label="審核時間">{{ formatDate(app.reviewedAt) }}</el-descriptions-item>
          <el-descriptions-item label="審核意見">
            {{ app.reviewerComment || '（無意見）' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card style="margin-bottom:16px">
        <el-button type="info" plain @click="downloadPdf">
          <el-icon><Download /></el-icon> 下載申請單 PDF
        </el-button>
      </el-card>

      <!-- 審核操作區 -->
      <el-card v-if="app.status === 'PENDING'">
        <template #header><span class="card-title">審核操作</span></template>

        <!-- H10: 說明核准/拒絕的後果 -->
        <el-alert type="info" :closable="false" show-icon style="margin-bottom:16px"
          title="核准後申請人將獲得所申請的權限；拒絕時請務必填寫意見，讓申請人知道原因。" />

        <el-form label-position="top">
          <el-form-item>
            <template #label>
              審核意見
              <!-- H5: 拒絕時強制填寫 -->
              <span class="req" v-if="requireComment">*（拒絕時為必填）</span>
            </template>
            <el-input v-model="reviewComment" type="textarea" :rows="3"
              placeholder="請填寫審核意見，說明核准條件或拒絕原因…" />
          </el-form-item>

          <div class="review-btns">
            <el-button type="success" size="large" :loading="approving" @click="handleApprove">
              <el-icon><CircleCheck /></el-icon> 核准通過
            </el-button>
            <el-button type="danger" size="large" :loading="rejecting" @click="handleReject">
              <el-icon><CircleClose /></el-icon> 拒絕申請
            </el-button>
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
import { ArrowLeft, Download, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { applicationApi } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const id = route.params.id

const loading = ref(true)
const app = ref(null)
const reviewComment = ref('')
const approving = ref(false)
const rejecting = ref(false)
const requireComment = ref(false)

const alertType = computed(() => ({
  PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger'
}[app.value?.status] || 'info'))

async function loadApp() {
  loading.value = true
  try {
    const { data } = await applicationApi.getById(id)
    app.value = data
  } catch {
    ElMessage.error('載入申請單失敗，請重新整理頁面')
  } finally {
    loading.value = false
  }
}

async function handleApprove() {
  // H5: 確認對話框說明後果
  try {
    await ElMessageBox.confirm(
      `確認核准申請單 APP-${String(app.value.id).padStart(6,'0')}？\n核准後申請人將取得所申請的資料庫物件權限。`,
      '核准確認', { type: 'success', confirmButtonText: '確認核准', cancelButtonText: '取消' }
    )
  } catch { return }

  approving.value = true
  try {
    await applicationApi.approve(id, { comment: reviewComment.value })
    ElMessage({ type: 'success', message: '已核准，申請人將收到通知', duration: 3000 })
    await loadApp()
  } catch {
    ElMessage.error('操作失敗，請稍後再試')
  } finally {
    approving.value = false
  }
}

async function handleReject() {
  // H5: 拒絕時強制填意見
  if (!reviewComment.value.trim()) {
    requireComment.value = true
    ElMessage.warning('拒絕申請時，請填寫拒絕原因讓申請人了解')
    return
  }
  requireComment.value = false

  try {
    await ElMessageBox.confirm(
      `確認拒絕申請單 APP-${String(app.value.id).padStart(6,'0')}？\n申請人將被通知申請被拒絕。`,
      '拒絕確認', { type: 'warning', confirmButtonText: '確認拒絕', cancelButtonText: '取消' }
    )
  } catch { return }

  rejecting.value = true
  try {
    await applicationApi.reject(id, { comment: reviewComment.value })
    ElMessage({ type: 'warning', message: '已拒絕申請', duration: 3000 })
    await loadApp()
  } catch {
    ElMessage.error('操作失敗，請稍後再試')
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
.detail-container { max-width: 1100px; margin: 0 auto; padding: 24px 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 8px 0 0; color: #1a2f6e; }
.card-title { font-weight: bold; color: #2952a3; }
.req { color: #f56c6c; margin-left: 4px; font-size: 0.85rem; }
.review-btns { display: flex; gap: 12px; margin-top: 8px; }
.loading-box { padding: 40px 0; }
.row-num { width: 32px; color: #909399; font-size: 12px; }

.table-scroll { overflow-x: auto; }
.perm-table { border-collapse: collapse; font-size: 13px; width: 100%; }
.perm-table th, .perm-table td {
  border: 1px solid #c8d4e8; padding: 6px 10px;
  text-align: center; vertical-align: middle;
}
.perm-table thead tr:first-child th { background: #dce6f5; font-weight: bold; }
.perm-table thead tr:last-child th { background: #eef2fa; }
.check-td { width: 56px; }
.check-yes { color: #2e7d32; font-weight: bold; }
</style>
