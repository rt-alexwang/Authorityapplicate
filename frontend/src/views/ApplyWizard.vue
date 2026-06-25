<template>
  <div class="wizard-container">
    <div class="wizard-header">
      <el-button text @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon> 返回首頁
      </el-button>
      <h1>系統存取權限申請</h1>
    </div>

    <el-card class="wizard-card">
      <!-- Step Progress -->
      <el-steps :active="currentStep" finish-status="success" align-center class="steps">
        <el-step title="申請人資訊" />
        <el-step title="申請系統" />
        <el-step title="原因與期間" />
        <el-step title="確認送出" />
      </el-steps>

      <!-- Step 1 -->
      <div v-if="currentStep === 0" class="step-content">
        <h2 class="step-title">步驟 1：填寫申請人資訊</h2>
        <el-form ref="form1Ref" :model="form" :rules="rules1" label-position="top" size="large">
          <el-form-item label="姓名" prop="applicantName">
            <el-input v-model="form.applicantName" placeholder="請輸入您的姓名" />
          </el-form-item>
          <el-form-item label="電子信箱" prop="applicantEmail">
            <el-input v-model="form.applicantEmail" placeholder="請輸入電子信箱" type="email" />
          </el-form-item>
          <el-form-item label="部門" prop="department">
            <el-select v-model="form.department" placeholder="請選擇部門" style="width:100%">
              <el-option v-for="d in departments" :key="d" :label="d" :value="d" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>

      <!-- Step 2 -->
      <div v-if="currentStep === 1" class="step-content">
        <h2 class="step-title">步驟 2：選擇申請系統與存取範圍</h2>
        <el-form ref="form2Ref" :model="form" :rules="rules2" label-position="top" size="large">
          <el-form-item label="目標系統" prop="targetSystem">
            <el-select v-model="form.targetSystem" placeholder="請選擇系統" allow-create filterable style="width:100%">
              <el-option v-for="s in systems" :key="s" :label="s" :value="s" />
            </el-select>
          </el-form-item>
          <el-form-item label="存取範圍" prop="accessScope">
            <el-radio-group v-model="form.accessScope" size="large">
              <el-radio-button value="唯讀（Read Only）">唯讀（Read Only）</el-radio-button>
              <el-radio-button value="讀寫（Read & Write）">讀寫（Read & Write）</el-radio-button>
              <el-radio-button value="管理員（Admin）">管理員（Admin）</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>

      <!-- Step 3 -->
      <div v-if="currentStep === 2" class="step-content">
        <h2 class="step-title">步驟 3：填寫申請原因與使用期間</h2>
        <el-form ref="form3Ref" :model="form" :rules="rules3" label-position="top" size="large">
          <el-form-item label="申請原因" prop="accessReason">
            <el-input
              v-model="form.accessReason"
              type="textarea"
              :rows="4"
              placeholder="請詳細說明申請此系統存取權限的原因及用途"
            />
          </el-form-item>
          <el-form-item label="預計開始日期" prop="expectedStartDate">
            <el-date-picker
              v-model="form.expectedStartDate"
              type="date"
              placeholder="選擇開始日期"
              format="YYYY/MM/DD"
              value-format="YYYY-MM-DD"
              style="width:100%"
            />
          </el-form-item>
          <el-form-item label="預計結束日期（選填）">
            <el-date-picker
              v-model="form.expectedEndDate"
              type="date"
              placeholder="選擇結束日期（無限期可留空）"
              format="YYYY/MM/DD"
              value-format="YYYY-MM-DD"
              style="width:100%"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- Step 4 - Confirm -->
      <div v-if="currentStep === 3" class="step-content">
        <h2 class="step-title">步驟 4：確認申請內容</h2>
        <el-alert type="info" :closable="false" style="margin-bottom:20px">
          請確認以下資訊無誤後點擊「送出申請」
        </el-alert>
        <el-descriptions border :column="2" size="default">
          <el-descriptions-item label="姓名">{{ form.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="信箱">{{ form.applicantEmail }}</el-descriptions-item>
          <el-descriptions-item label="部門">{{ form.department }}</el-descriptions-item>
          <el-descriptions-item label="申請系統">{{ form.targetSystem }}</el-descriptions-item>
          <el-descriptions-item label="存取範圍">{{ form.accessScope }}</el-descriptions-item>
          <el-descriptions-item label="開始日期">{{ form.expectedStartDate }}</el-descriptions-item>
          <el-descriptions-item label="結束日期">{{ form.expectedEndDate || '無限期' }}</el-descriptions-item>
          <el-descriptions-item label="申請原因" :span="2">{{ form.accessReason }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- Navigation Buttons -->
      <div class="btn-row">
        <el-button v-if="currentStep > 0" size="large" @click="currentStep--">上一步</el-button>
        <el-button
          v-if="currentStep < 3"
          type="primary"
          size="large"
          @click="nextStep"
        >下一步</el-button>
        <el-button
          v-if="currentStep === 3"
          type="success"
          size="large"
          :loading="submitting"
          @click="submit"
        >送出申請</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { applicationApi } from '../api/index.js'

const router = useRouter()
const currentStep = ref(0)
const submitting = ref(false)

const form = reactive({
  applicantName: '',
  applicantEmail: '',
  department: '',
  targetSystem: '',
  accessScope: '',
  accessReason: '',
  expectedStartDate: '',
  expectedEndDate: ''
})

const departments = ['資訊部', '財務部', '人資部', '業務部', '行銷部', '倉儲部', '管理部', '其他']
const systems = ['ERP系統', 'CRM系統', '人資系統', '財務系統', '倉儲管理系統', 'BI報表平台', '電子郵件系統']

const rules1 = {
  applicantName: [{ required: true, message: '請輸入姓名', trigger: 'blur' }],
  applicantEmail: [
    { required: true, message: '請輸入信箱', trigger: 'blur' },
    { type: 'email', message: '信箱格式不正確', trigger: 'blur' }
  ],
  department: [{ required: true, message: '請選擇部門', trigger: 'change' }]
}
const rules2 = {
  targetSystem: [{ required: true, message: '請選擇或輸入系統名稱', trigger: 'change' }],
  accessScope: [{ required: true, message: '請選擇存取範圍', trigger: 'change' }]
}
const rules3 = {
  accessReason: [{ required: true, message: '請填寫申請原因', trigger: 'blur' }],
  expectedStartDate: [{ required: true, message: '請選擇開始日期', trigger: 'change' }]
}

const form1Ref = ref()
const form2Ref = ref()
const form3Ref = ref()
const formRefs = [form1Ref, form2Ref, form3Ref]

async function nextStep() {
  const formRef = formRefs[currentStep.value]
  if (!formRef.value) { currentStep.value++; return }
  await formRef.value.validate((valid) => {
    if (valid) currentStep.value++
  })
}

async function submit() {
  submitting.value = true
  try {
    const payload = { ...form }
    if (!payload.expectedEndDate) delete payload.expectedEndDate
    const { data } = await applicationApi.create(payload)
    router.push({ path: '/apply/success', query: { id: data.id } })
  } catch (e) {
    ElMessage.error('送出失敗，請稍後再試')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.wizard-container {
  min-height: 100vh;
  background: #f5f7ff;
  padding: 24px 20px;
}
.wizard-header {
  max-width: 760px;
  margin: 0 auto 24px;
}
.wizard-header h1 { margin: 8px 0 0; color: #1a2f6e; }
.wizard-card {
  max-width: 760px;
  margin: 0 auto;
  padding: 8px;
}
.steps { margin-bottom: 36px; }
.step-title { color: #2952a3; margin-bottom: 24px; }
.step-content { min-height: 260px; }
.btn-row {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
