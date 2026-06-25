<template>
  <div class="review-container">
    <div class="page-header">
      <el-button text @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首頁
      </el-button>
      <h1>審核管理</h1>
    </div>

    <el-card>
      <!-- Filter Bar -->
      <div class="filter-bar">
        <el-radio-group v-model="statusFilter" size="default" @change="loadData">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="PENDING">待審核</el-radio-button>
          <el-radio-button value="APPROVED">已核准</el-radio-button>
          <el-radio-button value="REJECTED">已拒絕</el-radio-button>
        </el-radio-group>
        <el-button type="primary" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon> 重新整理
        </el-button>
      </div>

      <el-table :data="filteredList" v-loading="loading" stripe style="width:100%">
        <el-table-column label="申請編號" width="130">
          <template #default="{ row }">APP-{{ String(row.id).padStart(6, '0') }}</template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申請人" width="100" />
        <el-table-column prop="department" label="部門" width="100" />
        <el-table-column prop="targetSystem" label="申請系統" />
        <el-table-column prop="accessScope" label="存取範圍" width="150" />
        <el-table-column label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="tagType(row.status)">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申請時間" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push('/review/' + row.id)">
              查看 / 審核
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && filteredList.length === 0" class="empty-state">
        <el-empty description="目前沒有申請單" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { applicationApi } from '../api/index.js'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')

async function loadData() {
  loading.value = true
  try {
    const { data } = await applicationApi.getAll()
    list.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const filteredList = computed(() =>
  statusFilter.value ? list.value.filter(r => r.status === statusFilter.value) : list.value
)

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
.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.empty-state { padding: 40px 0; }
</style>
