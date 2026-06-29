<template>
  <div class="chatbot-wrapper">
    <!-- 浮動按鈕 -->
    <el-badge :value="unread > 0 ? unread : ''" :hidden="unread === 0" type="danger">
      <el-button
        circle
        class="chat-toggle-btn"
        :type="open ? 'primary' : 'default'"
        @click="toggleChat"
      >
        <el-icon size="22"><ChatDotRound /></el-icon>
      </el-button>
    </el-badge>

    <!-- 聊天視窗 -->
    <transition name="chat-slide">
      <div v-if="open" class="chat-window" @keydown.esc="open = false">
        <div class="chat-header">
          <el-icon><Robot /></el-icon>
          <span>DB 權限助理</span>
          <el-button text circle class="close-btn" @click="open = false">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>

        <div class="chat-body" ref="bodyRef">
          <div v-if="messages.length === 0" class="chat-welcome">
            <el-icon size="32" color="#409eff"><Robot /></el-icon>
            <p>您好！我是 DB 權限助理。<br>請問需要什麼協助？</p>
            <div class="quick-btns">
              <el-button size="small" round @click="quickAsk('目前有幾筆待審核申請？')">待審核數量</el-button>
              <el-button size="small" round @click="quickAsk('如何申請 DB 權限？')">如何申請</el-button>
              <el-button size="small" round @click="quickAsk('申請流程是什麼？')">申請流程</el-button>
            </div>
          </div>

          <div
            v-for="(msg, i) in messages"
            :key="i"
            class="chat-msg"
            :class="msg.role === 'user' ? 'msg-user' : 'msg-bot'"
          >
            <div class="bubble">{{ msg.content }}</div>
          </div>

          <div v-if="loading" class="chat-msg msg-bot">
            <div class="bubble loading-bubble">
              <span class="dot" /><span class="dot" /><span class="dot" />
            </div>
          </div>
        </div>

        <div class="chat-footer">
          <el-input
            v-model="inputText"
            placeholder="輸入訊息…"
            :disabled="loading"
            @keydown.enter.prevent="sendMessage"
            size="default"
          />
          <el-button type="primary" :loading="loading" @click="sendMessage" :disabled="!inputText.trim()">
            <el-icon v-if="!loading"><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ChatDotRound, Close, Promotion } from '@element-plus/icons-vue'
import axios from 'axios'

// Element Plus 沒有內建 Robot icon，用 Service 替代
import { Service as Robot } from '@element-plus/icons-vue'

const open = ref(false)
const unread = ref(0)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const bodyRef = ref(null)

function toggleChat() {
  open.value = !open.value
  if (open.value) unread.value = 0
}

async function quickAsk(text) {
  inputText.value = text
  await sendMessage()
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const history = messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content }))
    const { data } = await axios.post('/api/chat', { message: text, history })
    messages.value.push({ role: 'assistant', content: data.reply })
    if (!open.value) unread.value++
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，服務暫時無法使用，請稍後再試。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight
  })
}
</script>

<style scoped>
.chatbot-wrapper {
  position: fixed;
  bottom: 28px;
  right: 28px;
  z-index: 9999;
}

.chat-toggle-btn {
  width: 52px;
  height: 52px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.chat-window {
  position: absolute;
  bottom: 64px;
  right: 0;
  width: 360px;
  height: 480px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #409eff;
  color: #fff;
  font-weight: 600;
  font-size: 15px;
}

.close-btn {
  margin-left: auto;
  color: #fff !important;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 8px;
  color: #666;
  text-align: center;
  font-size: 14px;
}

.quick-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
  margin-top: 8px;
}

.chat-msg {
  display: flex;
}

.msg-user {
  justify-content: flex-end;
}

.msg-bot {
  justify-content: flex-start;
}

.bubble {
  max-width: 80%;
  padding: 9px 13px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.msg-user .bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.msg-bot .bubble {
  background: #f2f3f5;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 12px 16px;
}

.dot {
  width: 7px;
  height: 7px;
  background: #999;
  border-radius: 50%;
  animation: bounce 1.2s infinite;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

.chat-footer {
  display: flex;
  gap: 8px;
  padding: 10px 12px;
  border-top: 1px solid #eee;
}

.chat-footer .el-input {
  flex: 1;
}

.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.25s ease;
}

.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(16px) scale(0.97);
}
</style>
