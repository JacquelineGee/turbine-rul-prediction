import Vue from 'vue'
import VueRouter from 'vue-router'
import { Message } from 'element-ui';
//import HomeView from '../views/HomeView.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/User',
    name: 'user',
    component: () => import(/* webpackChunkName: "about" */ '../views/power/UserView.vue')
  },
  {
    path: '/Role',
    name: 'role',
    component: () => import(/* webpackChunkName: "about" */ '../views/power/RoleView.vue')
  },
  {
    path: '/Dataset',
    name: 'Dataset',
    component: () => import(/* webpackChunkName: "about" */ '../views/DatasetView.vue')
  },
  {
    path: '/PredictData',
    name: 'PredictData',
    component: () => import(/* webpackChunkName: "about" */ '../views/PredictDataView.vue')
  },
  {
    path: '/',
    name: 'login',
    component: () => import(/* webpackChunkName: "about" */ '../views/LoginView.vue')
  },
  {
    path: '/sys-log',
    name: 'log',
    component: () => import(/* webpackChunkName: "about" */ '../views/LogView.vue')
  },
  {
    path: '/home',
    name:'home',
    component: () => import(/* webpackChunkName: "about" */ '../views/HomeView.vue')
  },
  {
    path: '/function',
    name:'function',
    component: () => import(/* webpackChunkName: "about" */ '../views/power/FunctionView.vue')
  },
  {
    path: '/op',
    name:'op',
    component: () => import(/* webpackChunkName: "about" */ '../views/power/OpView.vue')
  }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  // 如果是访问登录页，直接放行
  if (to.path === "/") return next();

  // 获取 token
  const tokenStr = localStorage.getItem("jwt");

  // 如果没有 token，强制跳转到登录页
  if (!tokenStr) {
    alert("请登录");
    return next("/");
  } else if (to.path === "/User") {
    const userPower = Number(window.sessionStorage.getItem("user")); // 转换为数字
    if (userPower === 1) {
      return next();
    } else {
      /* alert("您没有此权限"); // 或者使用  */Message.error("您没有此权限");
    }
  } else if (to.path === "/Role") {
    const rolePower = Number(window.sessionStorage.getItem("role")); // 转换为数字
    if (rolePower === 1) {
      return next();
    } else {
      /* alert("您没有此权限"); // 或者使用  */Message.error("您没有此权限");
    }
  } else if (to.path === "/op") {
    const opPower = Number(window.sessionStorage.getItem("op")); // 转换为数字
    if (opPower === 1) {
      return next();
    } else {
      /* alert("您没有此权限"); // 或者使用  */Message.error("您没有此权限");
    }
  }
  else
  // 有 token，正常放行
  next();
});


export default router
