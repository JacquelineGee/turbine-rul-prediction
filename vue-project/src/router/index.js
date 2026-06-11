import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/page/HomePage.vue'),
      
    },
    {
      path: '/TurbineDetail',
      name: 'TurbineDetail',
      component: () => import('../views/component/GLBViewer.vue'),
      
    },
    {
      path: '/model1',
      name: 'Model1',
      component: () => import('../views/page/model/model1.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/model2',
      name: 'Model2',
      component: () => import('../views/page/model/model2.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/model3',
      name: 'Model3',
      component: () => import('../views/page/model/model3.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/page/LoginPage.vue'),
    },
    {
      path: '/methods',
      name: 'methods',
      component: () => import('../views/page/carousel/Methods.vue'),
      
    },
    {
      path: '/BaseOnDL',
      name: '/BaseOnDL',
      component: () => import('../views/page/carousel/BaseOnDL.vue'),
      
    },
    {
      path: '/reason',
      name: '/reason',
      component: () => import('../views/page/carousel/Reason.vue'),
      
    },
    {
      path: '/data-collecting',
      name: 'data-collecting',
      component: () => import('../views/page/DataGetPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/turbine',
      name: 'turbine',
      component: () => import('../views/page/carousel/Turbine.vue'),
      
    },
    {
      path: '/data-show',
      name: 'data-show',
      component: () => import('../views/page/predict.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/AI-Chat',
      name: 'AI-Chat',
      component: () => import('../views/page/AIChatPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/model',
      name: 'model',
      component: () => import('../views/page/ModelPage.vue'),
    }, {
      path: '/structure',
      name: 'structure',
      component: () => import('../views/page/TurbineStructure.vue'),
    },
    {
      path: '/service',
      name: 'service',
      component: () => import('../views/page/ServicePage.vue'),
      
    },
    {
      path: '/user-info',
      name: 'user-info',
      component: () => import('../views/page/UserInfoPage.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/predictionDetail/:id',
        name: 'PredictionDetail',
      component: () => import('../views/page/predictionDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

router.beforeEach((to, from, next) => {
  const isLogged = localStorage.getItem('isLoggedIn') === 'true'; // 假设登录状态存储在 localStorage
  const requiresAuth = to.meta?.requiresAuth; // 检查目标路由是否需要认证

  if (requiresAuth && !isLogged) {
    alert("请登录")
    next({ path: '/login' });
    return;
  }

  // 如果用户已登录，检查权限
  if (isLogged) {
    if (to.path === '/data-collecting') {
      const userPower = Number(window.sessionStorage.getItem('dataset')); // 转换为数字
      if (userPower === 1) {
        return next();
      } else {
        ElMessage.error('您没有此权限');
        return next(false); // 阻止导航
      }
    } else if (to.path === '/data-show') {
      const rolePower = Number(window.sessionStorage.getItem('HPd')); // 转换为数字
      if (rolePower === 1) {
        return next();
      } else {
        ElMessage.error('您没有此权限');
        return next(false); // 阻止导航
      }
    } else if (to.path === '/model1') {
      const opPower = Number(window.sessionStorage.getItem('model1')); // 转换为数字
      if (opPower === 1) {
        return next();
      } 
       else {
        ElMessage.error('您没有此权限');
        return next(false); // 阻止导航
      }
    }
    else if (to.path === '/model2') {
      const opPower = Number(window.sessionStorage.getItem('model2')); // 转换为数字
      if (opPower === 1) {
        return next();
      } 
       else {
        ElMessage.error('您没有此权限');
        return next(false); // 阻止导航
      }
    }
    else if (to.path === '/model3') {
      const opPower = Number(window.sessionStorage.getItem('model3')); // 转换为数字
      if (opPower === 1) {
        return next();
      } 
       else {
        ElMessage.error('您没有此权限');
        return next(false); // 阻止导航
      }
    }
  }

  // 其他情况正常放行
  next();
});

export default router
