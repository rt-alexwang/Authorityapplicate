<template>
  <div class="page-wrapper">
    <el-page-header @back="$router.push('/')" content="系統存取權限申請" class="page-header" />

    <el-card class="wizard-card">
      <el-steps :active="step" finish-status="success" align-center class="steps">
        <el-step title="申請資訊" style="cursor:pointer" @click="step > 0 && (step = 0)" />
        <el-step title="權限明細" style="cursor:pointer" @click="step > 1 && (step = 1)" />
        <el-step title="確認送出" />
      </el-steps>

      <!-- ── Step 1 ── -->
      <div v-if="step === 0">
        <el-alert type="info" :closable="false" show-icon class="step-hint">
          請選擇或填寫申請基本資訊。標示 <span class="req">*</span> 為必填。
        </el-alert>

        <el-form :model="form" :rules="rules1" ref="form1Ref" label-width="110px" class="step-form">
          <el-form-item prop="applicantName">
            <template #label><span class="req">*</span> 申請人姓名</template>
            <el-input v-model="form.applicantName" placeholder="請輸入姓名" maxlength="50" />
          </el-form-item>

          <el-form-item prop="applicantEmail">
            <template #label><span class="req">*</span> 申請人信箱</template>
            <el-input v-model="form.applicantEmail" placeholder="例：alex_wang@pxmart.com.tw" type="email" />
          </el-form-item>

          <el-form-item prop="requestItem">
            <template #label><span class="req">*</span> 申請項目</template>
            <el-select v-model="form.requestItem" filterable allow-create
              placeholder="請選擇或輸入申請項目" style="width:100%"
              @change="val => saveNewOption('REQUEST_ITEM', val)">
              <el-option v-for="o in opts.REQUEST_ITEM" :key="o" :label="o" :value="o" />
            </el-select>
          </el-form-item>

          <el-form-item label="需求說明">
            <el-select v-model="form.description" filterable allow-create clearable
              placeholder="請選擇常用說明，或直接輸入自訂內容" style="width:100%"
              @change="val => saveNewOption('DESCRIPTION', val)">
              <el-option v-for="o in opts.DESCRIPTION" :key="o" :label="o" :value="o" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <template #label>
              申請帳號
              <el-tooltip content="可多選，或輸入新帳號後按 Enter 新增" placement="top">
                <el-icon class="help-icon"><QuestionFilled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="form.accountList" multiple filterable allow-create
              placeholder="請選擇或輸入帳號" style="width:100%"
              @change="syncAccounts">
              <el-option v-for="o in opts.ACCOUNT" :key="o" :label="o" :value="o" />
            </el-select>
          </el-form-item>
        </el-form>

        <div class="step-footer">
          <el-button type="primary" @click="nextStep1">
            下一步：填寫權限明細 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- ── Step 2 ── -->
      <div v-if="step === 1">
        <el-alert type="info" :closable="false" show-icon class="step-hint">
          新增每筆 DB 物件的存取需求。每列請至少勾選一項權限。
        </el-alert>
        <el-alert v-if="rowError" type="error" :closable="false" show-icon class="step-hint">
          {{ rowError }}
        </el-alert>

        <div class="table-scroll">
          <table class="perm-table">
            <thead>
              <tr>
                <th>#</th>
                <th colspan="2">本機伺服器</th>
                <th colspan="2">遠端伺服器</th>
                <th colspan="2">Object</th>
                <th colspan="6">
                  權限
                  <el-tooltip :content="permHelp" placement="top">
                    <el-icon class="help-icon-sm"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </th>
                <th>操作</th>
              </tr>
              <tr>
                <th></th>
                <th>IP / 主機</th><th>DB</th>
                <th>IP / 主機</th><th>DB</th>
                <th>
                  Type
                  <el-tooltip content="SP=預存程序  Table=資料表  View=檢視表  Function=函數" placement="top">
                    <el-icon class="help-icon-sm"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </th>
                <th>物件名稱</th>
                <th>Select</th><th>Insert</th><th>Update</th><th>Delete</th><th>Execute</th><th>Alter</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in form.rows" :key="idx"
                  :class="{ 'row-error': rowErrors[idx] }">
                <td class="row-num">{{ idx + 1 }}</td>
                <td>
                  <el-select v-model="row.localIp" filterable allow-create size="small"
                    style="min-width:120px" placeholder="主機名稱"
                    @change="val => saveNewOption('LOCAL_SERVER', val)">
                    <el-option v-for="o in opts.LOCAL_SERVER" :key="o" :label="o" :value="o" />
                  </el-select>
                </td>
                <td>
                  <el-select v-model="row.localDb" filterable allow-create size="small"
                    style="min-width:100px" placeholder="DB 名稱"
                    @change="val => saveNewOption('DATABASE', val)">
                    <el-option v-for="o in opts.DATABASE" :key="o" :label="o" :value="o" />
                  </el-select>
                </td>
                <td>
                  <el-select v-model="row.remoteIp" filterable allow-create size="small"
                    style="min-width:130px" placeholder="IP / 主機"
                    @change="val => saveNewOption('REMOTE_SERVER', val)">
                    <el-option v-for="o in opts.REMOTE_SERVER" :key="o" :label="o" :value="o" />
                  </el-select>
                </td>
                <td>
                  <el-select v-model="row.remoteDb" filterable allow-create size="small"
                    style="min-width:100px" placeholder="DB 名稱"
                    @change="val => saveNewOption('DATABASE', val)">
                    <el-option v-for="o in opts.DATABASE" :key="o" :label="o" :value="o" />
                  </el-select>
                </td>
                <td>
                  <el-select v-model="row.objectType" size="small" style="width:95px">
                    <el-option label="Table" value="Table" />
                    <el-option label="View" value="View" />
                    <el-option label="SP" value="SP" />
                    <el-option label="Function" value="Function" />
                  </el-select>
                </td>
                <td>
                  <el-input v-model="row.objectName" size="small" style="min-width:160px"
                    placeholder="[dbo].[TableName]" />
                </td>
                <td class="check-td"><el-checkbox v-model="row.canSelect" /></td>
                <td class="check-td"><el-checkbox v-model="row.canInsert" /></td>
                <td class="check-td"><el-checkbox v-model="row.canUpdate" /></td>
                <td class="check-td"><el-checkbox v-model="row.canDelete" /></td>
                <td class="check-td"><el-checkbox v-model="row.canExecute" /></td>
                <td class="check-td"><el-checkbox v-model="row.canAlter" /></td>
                <td>
                  <el-tooltip content="複製此列" placement="top">
                    <el-button link @click="copyRow(idx)"><el-icon><CopyDocument /></el-icon></el-button>
                  </el-tooltip>
                  <el-tooltip content="刪除此列" placement="top">
                    <el-button type="danger" link @click="removeRow(idx)" :disabled="form.rows.length === 1">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tooltip>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="row-actions">
          <el-button :icon="Plus" @click="addRow">新增一列</el-button>
          <span class="row-count">共 {{ form.rows.length }} 列</span>
        </div>

        <div class="step-footer">
          <el-button @click="step--"><el-icon><ArrowLeft /></el-icon> 上一步</el-button>
          <el-button type="primary" @click="nextStep2">
            下一步：確認申請內容 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- ── Step 3 確認 ── -->
      <div v-if="step === 2">
        <el-alert type="warning" :closable="false" show-icon class="step-hint">
          請仔細確認以下資訊無誤後再送出。送出後無法修改。
        </el-alert>

        <el-descriptions :column="2" border class="confirm-desc">
          <el-descriptions-item label="申請人">{{ form.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="申請人信箱">{{ form.applicantEmail }}</el-descriptions-item>
          <el-descriptions-item label="申請項目" :span="2">{{ form.requestItem }}</el-descriptions-item>
          <el-descriptions-item label="需求說明" :span="2">{{ form.description || '（未填）' }}</el-descriptions-item>
          <el-descriptions-item label="申請帳號" :span="2">
            <el-tag v-for="a in form.accountList" :key="a" style="margin:2px">{{ a }}</el-tag>
            <span v-if="!form.accountList?.length" style="color:#999">（未填）</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="table-scroll" style="margin-top:16px">
          <table class="perm-table confirm">
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
                <th>IP</th><th>DB</th><th>IP</th><th>DB</th>
                <th>Type</th><th>Name</th>
                <th>Select</th><th>Insert</th><th>Update</th><th>Delete</th><th>Execute</th><th>Alter</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, idx) in form.rows" :key="idx">
                <td class="row-num">{{ idx + 1 }}</td>
                <td>{{ row.localIp }}</td><td>{{ row.localDb }}</td>
                <td>{{ row.remoteIp }}</td><td>{{ row.remoteDb }}</td>
                <td>{{ row.objectType }}</td><td>{{ row.objectName }}</td>
                <td class="check-td">{{ row.canSelect ? 'V' : '' }}</td>
                <td class="check-td">{{ row.canInsert ? 'V' : '' }}</td>
                <td class="check-td">{{ row.canUpdate ? 'V' : '' }}</td>
                <td class="check-td">{{ row.canDelete ? 'V' : '' }}</td>
                <td class="check-td">{{ row.canExecute ? 'V' : '' }}</td>
                <td class="check-td">{{ row.canAlter ? 'V' : '' }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="step-footer">
          <el-button @click="step--"><el-icon><ArrowLeft /></el-icon> 返回修改</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="submit">
            確認送出申請
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Delete, ArrowRight, ArrowLeft, CopyDocument, QuestionFilled } from '@element-plus/icons-vue'
import { createApplication, optionsApi } from '../api/index.js'

const router = useRouter()
const step = ref(0)
const submitting = ref(false)
const form1Ref = ref(null)
const rowError = ref('')

const permHelp = 'Select=查詢  Insert=新增  Update=更新  Delete=刪除  Execute=執行(SP/Function)  Alter=修改結構'

const opts = reactive({
  REQUEST_ITEM: [],
  DESCRIPTION: [],
  LOCAL_SERVER: [],
  REMOTE_SERVER: [],
  DATABASE: [],
  ACCOUNT: []
})

const form = reactive({
  applicantName: '',
  applicantEmail: '',
  requestItem: '',
  description: '',
  requestAccounts: '',
  accountList: [],
  rows: [newRow()]
})

const rules1 = {
  applicantName:  [{ required: true, message: '請輸入申請人姓名', trigger: 'blur' }],
  applicantEmail: [
    { required: true, message: '請輸入申請人信箱', trigger: 'blur' },
    { type: 'email', message: '信箱格式不正確', trigger: 'blur' }
  ],
  requestItem: [{ required: true, message: '請選擇申請項目', trigger: 'change' }]
}

const rowErrors = computed(() =>
  form.rows.map(r => !r.canSelect && !r.canInsert && !r.canUpdate && !r.canDelete && !r.canExecute && !r.canAlter)
)

onMounted(async () => {
  try {
    const { data } = await optionsApi.getAll()
    Object.keys(opts).forEach(k => { if (data[k]) opts[k] = data[k] })
  } catch { /* 選單載入失敗不影響填表 */ }
})

function syncAccounts() {
  form.requestAccounts = form.accountList.join(', ')
}

async function saveNewOption(type, value) {
  if (!value || opts[type]?.includes(value)) return
  try {
    await optionsApi.add(type, value)
    if (opts[type]) opts[type].push(value)
  } catch { /* 靜默失敗 */ }
}

function newRow() {
  return { localIp:'', localDb:'', remoteIp:'', remoteDb:'', objectType:'Table', objectName:'',
           canSelect:false, canInsert:false, canUpdate:false, canDelete:false, canExecute:false, canAlter:false }
}

function addRow() { form.rows.push(newRow()) }

function removeRow(idx) {
  if (form.rows.length > 1) form.rows.splice(idx, 1)
  else ElMessage.warning('至少需要保留一列')
}

function copyRow(idx) {
  form.rows.splice(idx + 1, 0, { ...form.rows[idx] })
}

async function nextStep1() {
  try { await form1Ref.value.validate() } catch { return }
  step.value++
}

function nextStep2() {
  rowError.value = ''
  const bad = rowErrors.value.map((e, i) => e ? i + 1 : null).filter(Boolean)
  if (bad.length) {
    rowError.value = `第 ${bad.join('、')} 列尚未勾選任何權限，請至少勾選一項。`
    return
  }
  step.value++
}

async function submit() {
  submitting.value = true
  try {
    const payload = { ...form }
    delete payload.accountList
    const res = await createApplication(payload)
    router.push({ path: '/apply/success', query: { id: res.data.id } })
  } catch (e) {
    const msg = e?.response?.data?.message || e?.response?.statusText || '網路錯誤，請稍後再試'
    ElMessage.error('送出失敗：' + msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-wrapper { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
.page-header { margin-bottom: 20px; }
.wizard-card { padding: 10px; }
.steps { margin-bottom: 24px; }
.step-hint { margin-bottom: 18px; font-size: 0.9rem; }
.step-form { max-width: 620px; }
.req { color: #f56c6c; margin-right: 3px; }
.help-icon { margin-left: 4px; color: #909399; cursor: help; vertical-align: middle; }
.help-icon-sm { font-size: 12px; color: #909399; cursor: help; vertical-align: middle; margin-left: 2px; }
.step-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
.confirm-desc { margin-bottom: 10px; }
.row-actions { margin-top: 12px; display: flex; align-items: center; gap: 16px; }
.row-count { color: #909399; font-size: 0.85rem; }

.table-scroll { overflow-x: auto; }
.perm-table { border-collapse: collapse; white-space: nowrap; font-size: 13px; width: 100%; }
.perm-table th, .perm-table td {
  border: 1px solid #c8d4e8; padding: 5px 8px;
  text-align: center; vertical-align: middle;
}
.perm-table thead tr:first-child th { background: #dce6f5; font-weight: bold; }
.perm-table thead tr:last-child th { background: #eef2fa; }
.perm-table.confirm tbody td { padding: 6px 10px; }
.check-td { width: 56px; }
.row-num { width: 32px; color: #909399; font-size: 12px; }
.row-error td { background: #fff0f0 !important; }
</style>
