// import './assets/main.css'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './assets/background.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from '@/utils/axios';
import { createPinia } from 'pinia'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)

app.use(ElementPlus)
const pinia = createPinia()
app.use(pinia)

app.config.globalProperties.$axios = axios;

const token = localStorage.getItem('jwt');

// if (token) {
//   axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
// }

// // 可选：设置默认 Content-Type
// axios.defaults.headers.post['Content-Type'] = 'application/json';
// axios.defaults.headers.put['Content-Type'] = 'application/json';

app.mount('#app')


