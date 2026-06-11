import Vue from 'vue'
import App from './App.vue'
import router from './router'
//引入ElementUI组件
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios';
import  AsideView  from './components/AsideView.vue';
import HeaderView from './components/HeaderView.vue'
import './assets/style/global.css';
Vue.config.productionTip = false
Vue.use(ElementUI);
Vue.component('AsideView', AsideView);
Vue.component('HeaderView', HeaderView);
axios.defaults.baseURL='http://localhost:8080'
// Add a request interceptor
axios.interceptors.request.use(
  config => {
    // Set your global headers here
    const jwt=localStorage.getItem('jwt');
    if (jwt)
      config.headers.Authorization =  `Bearer ${jwt}` // Example Authorization header
    config.headers['Content-Type'] = 'application/json' // Example Content-Type
    // You can add any other headers you need
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

new Vue({
  router,
  render: h => h(App)
}).$mount("#app")
