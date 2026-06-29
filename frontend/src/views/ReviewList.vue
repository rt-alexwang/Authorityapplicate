<template>
  <div class="review-container">
    <div class="page-header">
      <el-button text @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首頁
      </el-button>
      <h1>審核管理</h1>
    </div>

    <el-card>
      <!-- H1: filter 按鈕顯示各狀態數量 -->
      <div class="filter-bar">
        <el-radio-group v-model="statusFilter" size="default">
          <el-radio-button value="">
            全部 <el-badge :value="list.length" type="info" class="badge" />
          </el-radio-button>
          <el-radio-button value="PENDING">
            待審核 <el-badge :value="countOf('PENDING')" type="warning" class="badge" />
          </el-radio-button>
          <el-radio-button value="APPROVED">
            已核准 <el-badge :value="countOf('APPROVED')" type="success" class="badge" />
          </el-radio-button>
          <el-radio-button value="REJECTED">
            已拒絕 <el-badge :value="countOf('REJECTED')" type="danger" class="badge" />
          </el-radio-button>
        </el-radio-group>
        <el-button type="primary" :icon="Refresh" @click="loadData" :loading="loading">重新整理</el-button>
      </div>

      <!-- H6: 提示整列可點擊 -->
      <p v-if="filteredList.length" class="click-hint">
        <el-icon><InfoFilled /></el-icon> 點擊任一列可查看詳情並進行審核
      </p>

      <el-table
        :data="filteredList"
        v-loading="loading"
        stripe
        style="width:100%"
        row-class-name="clickable-row"
        @row-click="row => $router.push('/review/' + row.id)"
      >
        <el-table-column label="申請編號" width="130">
          <template #default="{ row }">
            <span class="app-no">APP-{{ String(row.id).padStart(6, '0') }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申請人" width="100" />
        <el-table-column prop="requestItem" label="申請項目" show-overflow-tooltip />
        <!-- H1: 顯示狀態標籤 -->
        <el-table-column label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="tagType(row.status)" effect="light">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申請時間" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="" width="80" fixed="right">
          <template #default>
            <el-icon color="#2952a3"><ArrowRight /></el-icon>
          </template>
        </el-table-column>
      </el-table>

      <!-- H8: 各狀態的空白訊息不同，引導使用者下一步 -->
      <div v-if="!loading && filteredList.length === 0" class="empty-state">
        <el-empty :description="emptyText" />
        <el-button v-if="statusFilter === 'PENDING'" type="primary" @click="$router.push('/apply')">
          前往申請
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, ArrowRight, Refresh, InfoFilled } from '@element-plus/icons-vue'
import { applicationApi } from '../api/index.js'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')

async function loadData() {
  loading.value = true
  try {
    const { data } = await applicationApi.getAll()
    list.value = data
  } catch {
    console.error('載入失敗')
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() =>
  statusFilter.value ? list.value.filter(r => r.status === statusFilter.value) : list.value
)

function countOf(status) {
  const n = list.value.filter(r => r.status === status).length
  return n > 0 ? n : null
}

const emptyText = computed(() => ({
  PENDING: '目前沒有待審核的申請單',
  APPROVED: '目前沒有已核准的申請單',
  REJECTED: '目前沒有已拒絕的申請單',
  '': '目前沒有任何申請單'
}[statusFilter.value]))

function tagType(status) {
  return { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }[status] || 'info'
}

function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleString('zh-TW', { hour12: false }).slice(0, 16)
}

onMounted(loadData)
</script>

<style scoped>
.review-container { max-width: 1100px; margin: 0 auto; padding: 24px 20px; }
.page-header { margin-bottom: 20px; }
.page-header h1 { margin: 8px 0 0; color: #1a2f6e; }
.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; flex-wrap: wrap; gap: 8px; }
.badge { margin-left: 4px; }
.click-hint { font-size: 0.82rem; color: #909399; margin: 4px 0 8px; display: flex; align-items: center; gap: 4px; }
.app-no { font-family: monospace; font-weight: bold; color: #2952a3; }
.empty-state { padding: 40px 0; text-align: center; }
:deep(.clickable-row) { cursor: pointer; }
:deep(.clickable-row:hover td) { background: #f0f4ff !important; }
</style>
