// src/utils/axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/', // 替换为你的 API 基础 URL
  timeout: 10000, // 请求超时时间
});

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 在请求发送之前添加认证头等操作
    config.headers.Authorization = `Bearer ${localStorage.getItem('jwt')}`;
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
instance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      console.error('Unauthorized, redirecting to login...');
      // 可以添加重定向到登录页的逻辑
    }
    return Promise.reject(error);
  }
);

export default instance;