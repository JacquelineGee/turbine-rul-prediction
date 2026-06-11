<template>
  <div class="chat-container">
    <el-card class="chat-box">
      <div class="chat-title">
        <span>DeepSeek预测结果评估</span>
      </div>

      <!-- 消息列表 -->
      <div class="chat-messages" ref="chatMessages">
        <div v-for="(msg, index) in messages" :key="index" :class="msg.sender">
          <div class="message-wrapper"
            :class="{ 'user-message': msg.sender === 'user', 'ai-message': msg.sender === 'ai' }">
            <div v-if="msg.sender === 'user'" class="user-text">
              <span>User: </span>
              <span>{{ msg.text }}</span>
            </div>
            <div v-else class="ai-message-container">
              <div class="ai-avatar">
                <img src="../../assets/images/deepseekAvator.png" alt="DeepSeek"
                  style="width: 40px; height: 40px; object-fit: cover; margin-right: 8px;" />
              </div>
              <div class="ai-text">
                <span v-html="renderMarkdown(msg.text)"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框和按钮容器 -->
      <div class="input-container">
        <el-input v-model="userInput" placeholder="请输入消息..." @keyup.enter="handleSendMessage"></el-input>
        <el-button type="primary" @click="handleSendMessage" :loading="loading" class="send-button">
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { marked } from 'marked'; // 引入 marked 库
import hljs from 'highlight.js'; // 引入 highlight.js
import 'highlight.js/styles/github-dark.css'; // 引入代码高亮样式
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/store/userStore'
import { useUser } from '@/store/user'

// 聊天数据
const messages = ref<{ sender: 'user' | 'ai'; text: string }[]>([]);
const userInput = ref('');
const loading = ref(false);
const chatMessages = ref<HTMLElement | null>(null); // 明确为 HTMLElement 类型
const showUserMessage = ref(true);
// 生成一个唯一的用户ID
const userStore = useUserStore();
const userS = useUser();

console.log(userStore);
// 生成或获取用户ID
const userId = userS.user_info.userId || Math.random().toString(36).substring(2);

// 监听 messages 的变化
watch(messages, () => {
  // 在 DOM 更新后滚动到底部
  nextTick(() => {
    if (chatMessages.value) {
      // 将 chatMessages.value 明确转换为 HTMLElement
      const chatMessagesElement = chatMessages.value as HTMLElement;
      chatMessagesElement.scrollTop = chatMessagesElement.scrollHeight;
    }
    // 高亮代码块
    highlightCodeBlocks();
    // 为代码块添加语言类别和复制按钮
    wrapCodeBlocks();
  });
}, { deep: true });

const renderMarkdown = (text: string) => {
  const renderer = new marked.Renderer();

  // 自定义 inline code 的渲染
  renderer.codespan = ({ text }: { text: string }) => {
    return `<span style="background-color: #f0f0f0; font-weight: bold; padding: 2px 4px; border-radius: 4px;">${text}</span>`;
  };

  return marked(text, {
    renderer,
    highlight: (code: string, language: string) => {
      const validLanguage = hljs.getLanguage(language) ? language : 'plaintext';
      const highlightedCode = hljs.highlight(code, { language: validLanguage }).value;

      // 返回包含代码类型和复制按钮的完整代码块
      return `<div class="code-block">
          <div class="code-header">
            <span class="code-language">${validLanguage}</span>
            <button class="copy-button" onclick="copyCode(this)">复制代码</button>
          </div>
          <pre style="width: 100%;"><code style="width: 100%;">${highlightedCode}</code></pre>
        </div>`;
    },
  }) as string; // 使用类型断言确保返回的是字符串
};

// 高亮代码块
const highlightCodeBlocks = () => {
  document.querySelectorAll('pre code').forEach((block) => {
    hljs.highlightBlock(block as HTMLElement);
  });
};

// 为代码块添加语言类别和复制按钮
const wrapCodeBlocks = () => {
  document.querySelectorAll('pre code').forEach((block) => {
    const preElement = block.parentElement;

    // 避免重复包裹
    if (!preElement || preElement.parentElement?.classList.contains('code-block')) {
      return;
    }

    const language = block.classList[0]?.replace('language-', '') || 'plaintext';

    const wrapper = document.createElement('div');
    wrapper.className = 'code-block';

    const header = document.createElement('div');
    header.className = 'code-header';
    header.innerHTML = `
      <span class="code-language">${language}</span>
      <button class="copy-button">复制代码</button>
    `;

    // 复制按钮的事件监听
    header.querySelector('.copy-button')?.addEventListener('click', () => {
      const codeText = block.textContent || '';
      navigator.clipboard.writeText(codeText).then(() => {
        ElMessage({ message: '复制成功', type: 'success' });
      }).catch(err => console.error('复制失败:', err));
    });

    // 包裹代码块
    wrapper.appendChild(header);
    wrapper.appendChild(preElement.cloneNode(true));

    // 替换原有 pre 元素
    preElement.replaceWith(wrapper);
  });
};

// 复制代码
const copyCode = (button: HTMLElement) => {
  const codeBlock = button.parentElement?.nextElementSibling?.textContent || '';
  if (codeBlock) {
    navigator.clipboard.writeText(codeBlock)
      .then(() => {
        ElMessage({
          message: '复制成功',
          type: 'success',
        });
      })
      .catch((err) => {
        console.error('复制失败:', err);
      });
  }
};
const handleSendMessage = () => {
  if (userInput.value.trim()) {
    sendMessage(userInput.value, true); // 显示用户发送的消息
  }
};
// 全局注册 copyCode 函数
onMounted(async () => {
  
  (window as any).copyCode = copyCode;
});

const channel = new BroadcastChannel('deepseekResponseChannel');

const sendMessage = async (message: string, showUserMessage: boolean = true, isPredictionRequest: boolean = false) => {
  if (typeof message !== 'string') {
    console.error('sendMessage 期望接收一个字符串参数，但接收到:', message);
    return;
  }

  if (!message.trim()) return;

  // 如果 showUserMessage 为 true，则显示用户发送的消息
  if (showUserMessage) {
    messages.value.push({ sender: 'user', text: message });
  }

  // 清空输入框
  userInput.value = '';
  loading.value = true;

  // 初始化 AI 消息
  let aiMessageIndex = messages.value.length;
  messages.value.push({ sender: 'ai', text: '' });
  const jwt=localStorage.getItem("jwt")

  try {
    // 向 Spring Boot 后端发送请求
    const response = await fetch('http://localhost:8080/api/chat/messageTemp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-ID': userId,
        'Authorization': `Bearer ${jwt}`,
      },
      body: JSON.stringify({ message: message }),
    });

    // 检查响应是否为成功
    if (response.ok) {
      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');

      let buffer = '';
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });
        buffer += chunk;

        // 处理缓冲区中的数据
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        lines.forEach(line => {
          if (line.startsWith('data:')) {
            const jsonString = line.replace('data:', '').trim();
            if (jsonString === '[DONE]') {
              console.log('Stream completed');
              // 流式输出完成后，将 isPredictionRequest 设置为 false
              if (isPredictionRequest) {
                isPredictionRequest = false;
              }
              return;
            }

            if (jsonString) {
              try {
                const jsonChunk = JSON.parse(jsonString);
                if (jsonChunk.choices && jsonChunk.choices.length > 0) {
                  const delta = jsonChunk.choices[0].delta;
                  if (delta.content) {
                    messages.value[aiMessageIndex].text += delta.content;
                    console.log(isPredictionRequest);
                    if (isPredictionRequest) {
                      // 通过 BroadcastChannel 发送渲染后的 Markdown 内容
                      const renderedContent = renderMarkdown(messages.value[aiMessageIndex].text);
                      channel.postMessage({ type: 'deepseekResponse', content: renderedContent });
                    }

                  }
                }
              } catch (error) {
                console.error('解析 JSON 失败:', error);
                console.error('无效的 JSON 字符串:', jsonString);
              }
            }
          }
        });
      }
    } else {
      messages.value.push({ sender: 'ai', text: '❌ DeepSeek API 调用失败' });
    }
  } catch (error) {
    console.error("与后端通信时发生错误:", error);
    messages.value.push({ sender: 'ai', text: '❌ 请求失败' });
  } finally {
    loading.value = false;
  }
};

// 暴露 sendMessage 方法
defineExpose({
  sendMessage,
});

// 在组件卸载时清空对话历史

  

</script>

<style scoped>
.chat-container {
  display: flex;
  justify-content: center;
  align-items: top;
  height: flex;
  background-color: white;
}

.chat-box {
  width: 1000px;
  padding: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.chat-title {
  font-size: 1.5em;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 15px;
  max-height: 70vh;
  /* 设置最大高度，防止对话框过高 */
}

.user,
.ai {
  white-space: pre-wrap;
  margin-bottom: 5px;
}

.user-message {
  text-align: right;
  color: #409eff;
  margin-bottom: 5px;
}

.ai-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 5px;
}

.ai-message-container {
  display: flex;
  align-items: flex-start;
}

.ai-avatar {
  flex-shrink: 0;
}

.ai-text {
  color: #404040;
  margin-top: 10px;
}

/* 代码块容器 */
:deep(.code-block) {
  background-color: #0D1117;
  /* 代码块背景颜色 */
  border-radius: 8px;
  /* 圆角 */
  margin-bottom: 16px;
  /* 代码块之间的间距 */
  overflow: hidden;
  /* 防止内容溢出 */
  width: 100%;
  /* 代码块宽度始终填满父容器 */
  max-width: 800px;
  /* 限制最大宽度，避免过宽 */
  margin-left: auto;
  margin-right: auto;
  /* 居中对齐 */
}

/* 代码块头部 */
:deep(.code-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 16px;
  background-color: #2d2d2d;
  border-bottom: 1px solid #444;
}

/* 代码类型 */
:deep(.code-language) {
  color: #cfcf84;
  /* 代码类型文字颜色 */
  font-size: 14px;
  font-family: monospace;
}

/* 复制按钮 */
:deep(.copy-button) {
  background-color: #2d2d2d;
  /* 按钮背景颜色 */
  color: white;
  /* 按钮文字颜色 */
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 12px;
}

/* 代码内容 */
:deep(pre) {
  margin: 0;
  padding: 16px;
  overflow-x: auto;
  white-space: pre-wrap;
  /* 允许换行 */
  word-wrap: break-word;
  /* 避免超长单词导致溢出 */
  width: 100%;
  /* 固定宽度 */
  box-sizing: border-box;
  /* 确保 padding 不会影响宽度 */
}

:deep(code) {
  display: block;
  width: 100%;
  /* 固定宽度 */
  box-sizing: border-box;
  /* 确保 padding 不会影响宽度 */
}

.input-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-input {
  flex: 1;
  margin-right: 10px;
}

.send-button {
  flex-shrink: 0;
}
</style>