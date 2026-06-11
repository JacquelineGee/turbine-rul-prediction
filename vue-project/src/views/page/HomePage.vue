<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import gsap from 'gsap';
import NavMenu from '../component/NavMenu.vue';
import ProductCard from '../../components/ProductCard.vue';
import BottomLine from '../component/BottomLine.vue';

const imageList = ref([
  { imageUrl: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine2.jpg', link: '/turbine' },
  { imageUrl: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine7.jpeg', link: '/turbine' },
  { imageUrl: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/turbine4.webp', link: '/turbine' },
]);

const router = useRouter();
const handleCarouselClick = (index: number) => {
  const link = imageList.value[index].link;
  if (link) {
    router.push(link);
  }
};

const hasAnimated = ref(false);

// 页面加载时应用动画
onMounted(() => {
  // 使用 IntersectionObserver 监听视口的进入
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
          hasAnimated.value = true; // 标记动画已触发

          // 触发动画：标题（从透明到不透明）
          gsap.fromTo('.app span',
            { opacity: 0, visibility: 'hidden', y: 50 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
          );

          // 触发动画：产品列表（从透明到不透明）
          gsap.fromTo('.product-list',
            { opacity: 0, visibility: 'hidden', y: 50 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out', delay: 0.3 }
          );

          // 触发动画：每个产品卡片（从透明到不透明）
          gsap.fromTo('.product-card',
            { opacity: 0, visibility: 'hidden', y: 50 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 1, stagger: 0.2, delay: 0.6 }
          );
        }
      });
    },
    { threshold: 0.2 } // 50% 可见时触发
  );

  // 开始观察 .app 元素
  const appElement = document.querySelector('.app');
  if (appElement) {
    observer.observe(appElement);
  }
});
</script>

<template>
  <div>
    <NavMenu />
    <div style="height:50px"></div>
    <el-carousel :interval="4000" type="card" height="350px">
      <el-carousel-item v-for="(item, index) in imageList" :key="index" @click="handleCarouselClick(index)">
        <img :src="item.imageUrl" alt="carousel image" style="width: 100%; height: 100%; object-fit: cover;" />
      </el-carousel-item>
    </el-carousel>
    <div style="height:100px">

    </div>
    <div class="app">
      <router-link class="router-dec" to="/service">
        <span>产品与服务</span>
      </router-link>
      <div class="product-list">
        <router-link class="router-dec" :to="{ path: '/service', hash: '#rul-prediction' }">
          <ProductCard title="剩余寿命预测与健康监测"
            description="通过实时监测燃气轮机的运行状态，结合机器学习和数据分析算法，精准预测燃气轮机的剩余使用寿命（RUL）。该功能可以基于历史数据、传感器数据等进行健康评估，帮助用户提前发现潜在的故障风险。"
            image="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/RUL.png" />
        </router-link>

        <router-link class="router-dec" :to="{ path: '/service', hash: '#analysis-support' }">
          <ProductCard title="智能数据分析与可视化"
            description="平台提供可视化的仪表盘和图表，展示燃气轮机的性能趋势、健康状态和预测误差等关键信息。用户可以方便地查看实时数据、历史数据和分析结果，帮助其快速理解设备状态。提供历史预测数据查询功能，帮助用户评估预测模型准确性和设备历史状态。"
            image="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E5%8F%AF%E8%A7%86%E5%8C%962.png" />
        </router-link>

        <router-link class="router-dec" :to="{ path: '/service', hash: '#maintenance-support' }">
          <ProductCard title="预警与维护决策支持"
            description="基于实时数据与预测结果，平台能够提供故障预警、异常报警和维护建议。当燃气轮机的健康状态接近临界时，平台将自动发送警报，提醒用户进行检查或保养。"
            image="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E9%A2%84%E8%AD%A62.png" />
        </router-link>
      </div>
    </div>
    <BottomLine />
  </div>
</template>

<style>
.router-dec {
  text-decoration: none;
  color: black;
}

.HomeImg {
  display: flex;
  justify-content: center;
  align-items: center;
}

.HomeImg img {
  width: 500px;
  height: 300px;
}

.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 500px;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.app {
  padding: 20px;
  text-align: center;
}

.app span {
  font-size: 2.5em;
  font-weight: bold;
  line-height: 2;
  opacity: 0;
  visibility: hidden;
  overflow: hidden;
}

.product-list {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.product-card {
  margin: 15px;
  opacity: 0;
  /* 初始不可见 */
  visibility: hidden;
  /* 初始不可见 */
  overflow: hidden;
}

.product-list {
  opacity: 0;
  /* 初始不可见 */
  visibility: hidden;
  /* 初始不可见 */
  overflow: hidden;
}
</style>
