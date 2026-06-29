<template>
  <div class="my-container">
    <div class="page-header">
      <el-button text @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首頁
      </el-button>
      <h1>個人權限查詢</h1>
    </div>

    <!-- 搜尋區 -->
    <el-card class="search-card">
      <p class="search-hint">
        <el-icon><InfoFilled /></el-icon>
        輸入您的姓名或帳號，查詢申請紀錄及目前已核准的權限。
      </p>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          size="large"
          placeholder="請輸入姓名或帳號（例：王小明 或 smlin）"
          clearable
          style="max-width:480px"
          @keyup.enter="doSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" :loading="loading" @click="doSearch">查詢</el-button>
      </div>
    </el-card>

    <!-- 查詢結果 -->
    <template v-if="searched">
      <!-- 無資料 -->
      <el-empty v-if="records.length === 0"
        description="查無資料，請確認姓名或帳號是否正確"
        style="margin-top:40px" />

      <template v-else>
        <!-- 統計摘要 -->
        <div class="stat-row">
          <el-statistic title="申請總數" :value="records.length" />
          <el-statistic title="待審核" :value="countOf('PENDING')" value-style="color:#e6a23c" />
          <el-statistic title="已核准" :value="countOf('APPROVED')" value-style="color:#67c23a" />
          <el-statistic title="已拒絕" :value="countOf('REJECTED')" value-style="color:#f56c6c" />
        </div>

        <!-- 現有權限彙整（僅顯示已核准的） -->
        <el-card v-if="approvedRows.length > 0" class="section-card">
          <template #header>
            <span class="card-title">
              <el-icon color="#67c23a"><CircleCheck /></el-icon>
              目前已核准的資料庫物件權限
            </span>
            <el-tag type="success" style="margin-left:8px">共 {{ approvedRows.length }} 筆</el-tag>
          </template>
          <div class="table-scroll">
            <table class="perm-table">
              <thead>
                <tr>
                  <th>申請編號</th>
                  <th colspan="2">本機伺服器</th>
                  <th colspan="2">遠端伺服器</th>
                  <th colspan="2">Object</th>
                  <th colspan="6">權限</th>
                </tr>
                <tr>
                  <th></th>
                  <th>IP / 主機</th><th>DB</th>
                  <th>IP / 主機</th><th>DB</th>
                  <th>Type</th><th>物件名稱</th>
                  <th>Select</th><th>Insert</th><th>Update</th><th>Delete</th><th>Execute</th><th>Alter</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(r, i) in approvedRows" :key="i">
                  <td>
                    <el-button link type="primary" @click="$router.push('/review/' + r.appId)">
                      APP-{{ String(r.appId).padStart(6,'0') }}
                    </el-button>
                  </td>
                  <td>{{ r.localIp }}</td><td>{{ r.localDb }}</td>
                  <td>{{ r.remoteIp }}</td><td>{{ r.remoteDb }}</td>
                  <td>{{ r.objectType }}</td><td class="obj-name">{{ r.objectName }}</td>
                  <td class="check-td">{{ r.canSelect  ? 'V' : '' }}</td>
                  <td class="check-td">{{ r.canInsert  ? 'V' : '' }}</td>
                  <td class="check-td">{{ r.canUpdate  ? 'V' : '' }}</td>
                  <td class="check-td">{{ r.canDelete  ? 'V' : '' }}</td>
                  <td class="check-td">{{ r.canExecute ? 'V' : '' }}</td>
                  <td class="check-td">{{ r.canAlter   ? 'V' : '' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>

        <el-alert v-else type="info" :closable="false" show-icon style="margin-top:16px"
          title="目前尚無已核准的資料庫物件權限" />

        <!-- 申請紀錄列表 -->
        <el-card class="section-card">
          <template #header>
            <span class="card-title">
              <el-icon color="#2952a3"><Document /></el-icon>
              所有申請紀錄
            </span>
          </template>

          <el-collapse>
            <el-collapse-item v-for="app in records" :key="app.id"
              :name="app.id" class="record-item">
              <template #title>
                <div class="collapse-title">
                  <span class="app-no">APP-{{ String(app.id).padStart(6,'0') }}</span>
                  <el-tag :type="tagType(app.status)" effect="light" size="small">{{ app.statusLabel }}</el-tag>
                  <span class="app-item">{{ app.requestItem }}</span>
                  <span class="app-date">{{ formatDate(app.createdAt) }}</span>
                </div>
              </template>

              <el-descriptions border :column="2" size="small" style="margin-bottom:12px">
                <el-descriptions-item label="申請項目" :span="2">{{ app.requestItem }}</el-descriptions-item>
                <el-descriptions-item label="需求說明" :span="2">{{ app.description || '（未填）' }}</el-descriptions-item>
                <el-descriptions-item label="申請帳號" :span="2">{{ app.requestAccounts || '（未填）' }}</el-descriptions-item>
                <el-descriptions-item label="申請時間">{{ formatDate(app.createdAt) }}</el-descriptions-item>
                <el-descriptions-item label="審核時間">{{ formatDate(app.reviewedAt) }}</el-descriptions-item>
                <el-descriptions-item v-if="app.reviewerComment" label="審核意見" :span="2">
                  {{ app.reviewerComment }}
                </el-descriptions-item>
              </el-descriptions>

              <div v-if="app.rows?.length" class="table-scroll">
                <table class="perm-table mini">
                  <thead>
                    <tr>
                      <th>遠端 IP</th><th>DB</th><th>Type</th><th>物件名稱</th>
                      <th>Sel</th><th>Ins</th><th>Upd</th><th>Del</th><th>Exe</th><th>Alt</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(row, ri) in app.rows" :key="ri">
                      <td>{{ row.remoteIp }}</td>
                      <td>{{ row.remoteDb }}</td>
                      <td>{{ row.objectType }}</td>
                      <td class="obj-name">{{ row.objectName }}</td>
                      <td class="check-td">{{ row.canSelect  ? 'V' : '' }}</td>
                      <td class="check-td">{{ row.canInsert  ? 'V' : '' }}</td>
                      <td class="check-td">{{ row.canUpdate  ? 'V' : '' }}</td>
                      <td class="check-td">{{ row.canDelete  ? 'V' : '' }}</td>
                      <td class="check-td">{{ row.canExecute ? 'V' : '' }}</td>
                      <td class="check-td">{{ row.canAlter   ? 'V' : '' }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div style="margin-top:10px;text-align:right">
                <el-button size="small" @click="$router.push('/review/' + app.id)">查看完整申請單</el-button>
                <el-button size="small" type="info" plain @click="downloadPdf(app.id)">
                  <el-icon><Download /></el-icon> PDF
                </el-button>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </template>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ArrowLeft, Search, InfoFilled, CircleCheck, Document, Download } from '@element-plus/icons-vue'
import { applicationApi } from '../api/index.js'

const keyword = ref('')
const records = ref([])
const loading = ref(false)
const searched = ref(false)

async function doSearch() {
  const q = keyword.value.trim()
  if (!q) return
  loading.value = true
  searched.value = false
  try {
    const { data } = await applicationApi.search(q)
    records.value = data
  } catch {
    records.value = []
  } finally {
    loading.value = false
    searched.value = true
  }
}

// 把所有已核准申請的 rows 攤平，附上 appId
const approvedRows = computed(() =>
  records.value
    .filter(a => a.status === 'APPROVED')
    .flatMap(a => (a.rows || []).map(r => ({ ...r, appId: a.id })))
)

function countOf(status) {
  return records.value.filter(r => r.status === status).length
}

function tagType(status) {
  return { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }[status] || 'info'
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleString('zh-TW', { hour12: false }).slice(0, 16)
}

function downloadPdf(id) {
  window.open(applicationApi.getPdfUrl(id), '_blank')
}
</script>

<style scoped>
.my-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 8px 0 0; color: #1a2f6e; }

.search-card { margin-bottom: 20px; }
.search-hint { color: #606266; margin: 0 0 14px; display: flex; align-items: center; gap: 6px; font-size: 0.9rem; }
.search-bar { display: flex; gap: 12px; align-items: center; }

.stat-row {
  display: flex; gap: 0; margin-bottom: 20px;
  background: #fff; border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,.08);
  overflow: hidden;
}
.stat-row :deep(.el-statistic) {
  flex: 1; padding: 20px; text-align: center;
  border-right: 1px solid #eee;
}
.stat-row :deep(.el-statistic:last-child) { border-right: none; }

.section-card { margin-bottom: 20px; }
.card-title { font-weight: bold; color: #2952a3; display: inline-flex; align-items: center; gap: 6px; }

.collapse-title {
  display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
  width: 100%; font-size: 14px;
}
.app-no { font-family: monospace; font-weight: bold; color: #2952a3; min-width: 110px; }
.app-item { flex: 1; color: #303133; }
.app-date { color: #909399; font-size: 12px; margin-left: auto; white-space: nowrap; }
.record-item { border-bottom: 1px solid #f0f0f0; }

.table-scroll { overflow-x: auto; }
.perm-table { border-collapse: collapse; font-size: 13px; width: 100%; white-space: nowrap; }
.perm-table th, .perm-table td {
  border: 1px solid #c8d4e8; padding: 5px 10px;
  text-align: center; vertical-align: middle;
}
.perm-table thead tr:first-child th { background: #dce6f5; font-weight: bold; }
.perm-table thead tr:last-child th { background: #eef2fa; }
.perm-table.mini th, .perm-table.mini td { padding: 4px 8px; font-size: 12px; }
.check-td { width: 44px; color: #2e7d32; font-weight: bold; }
.obj-name { text-align: left; min-width: 140px; }
</style>
