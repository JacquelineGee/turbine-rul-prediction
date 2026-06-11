<template>
  <NavMenu />
  <div class="chat-container">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
      <div class="sidebar-header">
        <h3 v-if="!isSidebarCollapsed">历史对话</h3>
        <!-- <button class="toggle-sidebar" @click="toggleSidebar">
          {{ isSidebarCollapsed ? '>' : '<' }}
        </button> -->
      </div>
      
      <div class="conversation-list" v-if="!isSidebarCollapsed">
        <div 
          v-for="conv in conversations" 
          :key="conv.id"
          class="conversation-item"
          :class="{ 'active': activeConversationId === conv.id }"
          @click="loadConversation(conv.id)"
        >
          <div class="conv-title">{{ conv.title }}</div>
          <div class="conv-meta">
            <span>{{ formatDate(conv.createdAt) }}</span>
            <span>{{ conv.messageCount }}条消息</span>
          </div>
          <button class="delete-conv" @click.stop="deleteConversation(conv.id)">
            <i class="el-icon-delete"></i>
          </button>
        </div>
      </div>
      
      <button class="new-chat-btn" @click="startNewConversation">
        <i class="el-icon-plus"></i>
        <span v-if="!isSidebarCollapsed">新对话</span>
      </button>
    </div>

    <!-- 主聊天区域 -->
    <div class="chat-box" :class="{ 'expanded': isSidebarCollapsed }">
      <div class="chat-title">
        <span>{{ currentConversationTitle || 'DeepSeek对话' }}</span>
        <el-button 
          v-if="activeConversationId"
          size="small"
          type="danger"
          @click="deleteConversation(activeConversationId)"
          class="delete-current-btn"
        >
          删除当前对话
        </el-button>
      </div>

      <!-- 消息列表 -->
      <div class="chat-messages" ref="chatMessages">
        <div v-if="showWelcomeMessage" class="welcome-message">
          <span>我是DeepSeek，很高兴见到你！</span>
          <p>请开始您的对话，或者从左侧选择历史对话继续</p>
        </div>

        <div v-for="(msg, index) in messages" :key="index" :class="msg.sender">
          <div class="message-wrapper"
              :class="{ 'user-message': msg.sender === 'user', 'ai-message': msg.sender === 'ai' }">
              <!-- 用户消息 -->
              <div v-if="msg.sender === 'user'" class="user-message-container">
                  <div class="user-text">
                      <span>{{ msg.text }}</span>
                  </div>
              </div>
              <!-- AI 消息 -->
              <div v-else class="ai-message-container">
                  <div class="ai-avatar">
                      <img src="../../assets/images/deepseekAvator.png" alt="DeepSeek"
                          style="width: 40px; height: 40px; object-fit: cover; margin-right: 8px;" />
                  </div>
                  <div class="ai-text">
                      <span v-html="msg.renderedHtml || renderMarkdown(msg.text)"></span>
                  </div>
              </div>
          </div>
        </div>
        
        <div v-if="loading" class="loading-indicator">
          <i class="el-icon-loading"></i>
          <span>DeepSeek正在思考...</span>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-container-bottom">
        <el-input 
          v-model="userInput" 
          placeholder="请输入消息..." 
          :autosize="{ minRows: 2, maxRows: 6 }" 
          type="textarea"
          @keyup.enter="handleSendMessage"
          :disabled="loading"
        ></el-input>
        <el-button 
          type="primary" 
          @click="handleSendMessage" 
          :loading="loading" 
          class="send-button"
          :disabled="!userInput.trim()"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, onMounted } from 'vue';
import { marked } from 'marked';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css';
import { ElMessage, ElLoading } from 'element-plus';
import NavMenu from '../component/NavMenu.vue';
import { useUserStore } from '@/store/userStore'
import { useUser } from '@/store/user'

interface Conversation {
  id: number;
  title: string;
  createdAt: string;
  messageCount: number;
}

interface ChatMessage {
  sender: 'user' | 'ai';
  text: string;
  renderedHtml?: string; // 新增字段，用于存储渲染后的HTML
}

// 响应式数据
const messages = ref<ChatMessage[]>([]);
const userInput = ref('');
const loading = ref(false);
const chatMessages = ref<HTMLElement | null>(null);
const showWelcomeMessage = ref(true);
const conversations = ref<Conversation[]>([]);
const activeConversationId = ref<number | null>(null);
const currentConversationTitle = ref('');
const isSidebarCollapsed = ref(false);
const userStore = useUserStore();
const userS = useUser();

console.log(userStore);
// 生成或获取用户ID
const userId = userS.user_info.userId || Math.random().toString(36).substring(2);
const jwt=localStorage.getItem("jwt")
localStorage.setItem('userId', userId);

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    scrollToBottom();
    highlightCodeBlocks();
    wrapCodeBlocks();
  });
}, { deep: true });

// 初始化 - 加载对话列表
onMounted(async () => {
  await fetchConversations();
  
  // 全局注册复制函数
  (window as any).copyCode = copyCode;
});

// 获取对话列表
const fetchConversations = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/chat/conversations`, {
      headers: {
        'X-User-ID': userId,
        'Authorization': `Bearer ${jwt}`,
      },
    });
    
    if (!response.ok) throw new Error('获取对话列表失败');
    
    conversations.value = await response.json();
  } catch (error) {
    console.error("获取对话列表失败:", error);
    ElMessage.error('获取对话列表失败');
  }
};

// 加载特定对话
const loadConversation = async (conversationId: number) => {
  const loadingInstance = ElLoading.service({ 
    lock: true,
    text: '加载对话中...',
  });
  
  try {
    const response = await fetch(`http://localhost:8080/api/chat/conversation/${conversationId}`,
    {
      headers: {
        'Authorization': `Bearer ${jwt}`,
      },
    }
    );
    
    if (!response.ok) throw new Error('加载对话失败');
    
    const conversation = await response.json();
    
    activeConversationId.value = conversationId;
    currentConversationTitle.value = conversation.title;
    
    // 转换消息格式并对AI消息进行Markdown渲染
    messages.value = conversation.messages.map((msg: any) => {
      // 处理消息内容中的转义字符
      let processedContent = msg.content
        // 首先处理所有转义双引号
        .replace(/\\"/g, '"')
        // 处理其他常见转义字符
        .replace(/\\n/g, '\n')
        .replace(/\\t/g, '\t')
        // 将单独的\替换为"（但排除转义序列的情况）
        .replace(/(?<!\\)\\(?!["nt\\])/g, '"');
      
      // 特别处理代码块中的转义字符
      processedContent = processedContent.replace(
        /```([\s\S]*?)```/g, 
        (match: string) => {
          // 在代码块内，我们更积极地转换反斜杠
          return match
            .replace(/\\"/g, '"')
            .replace(/\\n/g, '\n')
            // 将代码块内所有单独的\转换为"
            .replace(/\\(?!["n\\])/g, '"');
        }
      );
      
      const message: ChatMessage = {
        sender: msg.role === 'user' ? 'user' : 'ai',
        text: processedContent,
      };
      
      // 对AI消息进行Markdown渲染
      if (message.sender === 'ai') {
        message.renderedHtml = renderMarkdown(processedContent);
      }
      
      return message;
    });
    
    showWelcomeMessage.value = false;
    
    // 滚动到底部
    scrollToBottom();
    
    // 确保代码块高亮和包装
    nextTick(() => {
      highlightCodeBlocks();
      wrapCodeBlocks();
    });
  } catch (error) {
    console.error("加载对话失败:", error);
    ElMessage.error('加载对话失败');
  } finally {
    loadingInstance.close();
  }
};

// 删除对话
const deleteConversation = async (conversationId: number) => {
  try {
    await fetch(`http://localhost:8080/api/chat/conversation/${conversationId}`, {
      method: 'DELETE',
      headers: {
        'X-User-ID': userId,
        'Authorization': `Bearer ${jwt}`
      },
    });
    
    // 如果删除的是当前对话，则开启新对话
    if (activeConversationId.value === conversationId) {
      startNewConversation();
    }
    
    // 刷新对话列表
    await fetchConversations();
    ElMessage.success('对话已删除');
  } catch (error) {
    console.error("删除对话失败:", error);
    ElMessage.error('删除对话失败');
  }
};

// 开始新对话
const startNewConversation = () => {
  activeConversationId.value = null;
  currentConversationTitle.value = '';
  messages.value = [];
  showWelcomeMessage.value = true;
  userInput.value = '';
};

// 发送消息
const handleSendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return;
  
  const currentMessage = userInput.value.trim();
  showWelcomeMessage.value = false;
  loading.value = true;
  
  // 先添加用户消息到列表
  messages.value.push({ sender: 'user', text: currentMessage });
  scrollToBottom(); // 立即滚动到底部
  
  try {
    // 确保conversationId参数为空时也能正确处理
    const url = `http://localhost:8080/api/chat/message${
      activeConversationId.value ? `?conversationId=${activeConversationId.value}` : '-1'
    }`;
    const response = await fetch(
      `http://localhost:8080/api/chat/message?conversationId=${activeConversationId.value || '-1'}`, 
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-User-ID': userId,
          'Authorization': `Bearer ${jwt}`,
        },
        body: JSON.stringify({ message: currentMessage }),
      }
    );
    
    if (!response.ok) throw new Error('发送消息失败');
    
    const reader = response.body?.getReader();
    if (!reader) throw new Error('无法读取响应流');
    
    const decoder = new TextDecoder('utf-8');
    let buffer = '';
    let aiMessageIndex = messages.value.length;
    
    // 添加初始AI消息
    messages.value.push({ sender: 'ai', text: '' });
    
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      
      const chunk = decoder.decode(value, { stream: true });
      buffer += chunk;
      
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';
      
      for (const line of lines) {
        const trimmedLine = line.trim();
        if (trimmedLine.startsWith('data:')) {
          const jsonString = trimmedLine.replace('data:', '').trim();
          if (jsonString === '[DONE]') break;
          
          if (jsonString) {
            try {
              const jsonChunk = JSON.parse(jsonString);
              if (jsonChunk.choices && jsonChunk.choices.length > 0) {
                const delta = jsonChunk.choices[0].delta;
                if (delta?.content) {
                  messages.value[aiMessageIndex].text += delta.content;
                }
              }
            } catch (error) {
              console.error('解析 JSON 失败:', error);
            }
          }
        }
      }
    }
    
    // 对完整的AI消息进行Markdown渲染
    if (messages.value[aiMessageIndex]) {
      messages.value[aiMessageIndex].renderedHtml = renderMarkdown(messages.value[aiMessageIndex].text);
    }
    
    // 如果是新对话，刷新对话列表
    if (!activeConversationId.value) {
      await fetchConversations();
      if (conversations.value.length > 0) {
        activeConversationId.value = conversations.value[0].id;
        currentConversationTitle.value = conversations.value[0].title;
      }
    }
    
    userInput.value = ''; // 清空输入框
  } catch (error) {
    console.error("发送消息失败:", error);
    ElMessage.error('发送消息失败');
    // 移除最后一条用户消息和AI消息（如果有）
    messages.value = messages.value.filter(msg => 
      msg.text !== currentMessage || msg.sender !== 'user'
    );
  } finally {
    loading.value = false;
    scrollToBottom();
  }
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessages.value) {
      chatMessages.value.scrollTop = chatMessages.value.scrollHeight;
    }
  });
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
};

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

// Markdown 渲染
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

      return `<div class="code-block">
        <div class="code-header">
          <span class="code-language">${validLanguage}</span>
          <button class="copy-button" onclick="copyCode(this)">复制代码</button>
        </div>
        <pre style="width: 100%;"><code style="width: 100%;">${highlightedCode}</code></pre>
      </div>`;
    },
  }) as string;
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

    header.querySelector('.copy-button')?.addEventListener('click', () => {
      const codeText = block.textContent || '';
      navigator.clipboard.writeText(codeText).then(() => {
        ElMessage({ message: '复制成功', type: 'success' });
      }).catch(err => console.error('复制失败:', err));
    });

    wrapper.appendChild(header);
    wrapper.appendChild(preElement.cloneNode(true));
    preElement.replaceWith(wrapper);
  });
};

// 复制代码
const copyCode = (button: HTMLElement) => {
  const codeBlock = button.parentElement?.nextElementSibling?.textContent || '';
  if (codeBlock) {
    navigator.clipboard.writeText(codeBlock)
      .then(() => {
        ElMessage({ message: '复制成功', type: 'success' });
      })
      .catch((err) => {
        console.error('复制失败:', err);
      });
  }
};
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 90px);
  background-color: white;
}

/* 侧边栏样式 */
.sidebar {
  width: 280px;
  height: 100%;
  background-color: #f5f5f5;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar-collapsed {
  width: 50px;
}

.sidebar-header {
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e0e0e0;
  min-height: 60px;
}

.toggle-sidebar {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 5px;
  color: #666;
}

.toggle-sidebar:hover {
  color: #409eff;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 5px 0;
}

.conversation-item {
  padding: 12px 15px;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  position: relative;
  transition: background-color 0.2s;
}

.conversation-item:hover {
  background-color: #ebebeb;
}

.conversation-item.active {
  background-color: #e0e0e0;
}

.conv-title {
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 20px;
}

.conv-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.delete-conv {
  position: absolute;
  right: 5px;
  top: 5px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  display: none;
  padding: 2px;
}

.conversation-item:hover .delete-conv {
  display: block;
}

.delete-conv:hover {
  color: #f56c6c;
}

.new-chat-btn {
  padding: 12px;
  margin: 10px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.new-chat-btn:hover {
  background-color: #66b1ff;
}

.new-chat-btn i {
  margin-right: 5px;
}

/* 主聊天区域 */
.chat-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
}

.chat-box.expanded {
  margin-left: -230px;
}

.chat-title {
  padding: 15px 20px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-current-btn {
  margin-left: 10px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #fafafa;
}

.welcome-message {
  text-align: center;
  font-size: 1.2em;
  color: #666;
  margin-bottom: 30px;
}

.welcome-message p {
  font-size: 0.9em;
  color: #999;
  margin-top: 10px;
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  padding: 10px;
}

.loading-indicator i {
  margin-right: 8px;
  font-size: 18px;
}

/* 消息样式 */
.user,
.ai {
  white-space: pre-wrap;
  margin-bottom: 15px;
}

.user-message {
  display: flex;
  justify-content: flex-end;
}

.user-message-container {
  display: inline-block;
  background-color: #eff6ff;
  color: #262626;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 80%;
  word-wrap: break-word;
  white-space: pre-wrap;
  text-align: left;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.ai-message {
  display: flex;
  align-items: flex-start;
}

.ai-message-container {
  display: flex;
  align-items: flex-start;
  background-color: #ffffff;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 80%;
  word-wrap: break-word;
  white-space: pre-wrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.ai-avatar {
  flex-shrink: 0;
}

.ai-text {
  color: #404040;
  margin-left: 8px;
}

/* 输入框区域 */
.input-container-bottom {
  padding: 15px 20px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
  display: flex;
  align-items: center;
}

.el-input {
  flex: 1;
  margin-right: 10px;
}

.send-button {
  flex-shrink: 0;
  height: 100%;
}

/* 代码块样式 */
:deep(.code-block) {
  background-color: #0D1117;
  border-radius: 8px;
  margin: 10px 0;
  overflow: hidden;
  width: 100%;
  max-width: 800px;
}

:deep(.code-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 16px;
  background-color: #2d2d2d;
  border-bottom: 1px solid #444;
}

:deep(.code-language) {
  color: #cfcf84;
  font-size: 14px;
  font-family: monospace;
}

:deep(.copy-button) {
  background-color: #2d2d2d;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 12px;
}

:deep(pre) {
  margin: 0;
  padding: 16px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
  width: 100%;
  box-sizing: border-box;
}

:deep(code) {
  display: block;
  width: 100%;
  box-sizing: border-box;
}
</style>