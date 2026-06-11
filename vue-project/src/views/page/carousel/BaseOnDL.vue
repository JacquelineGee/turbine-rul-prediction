<script lang="ts" setup>
import NavMenu from '../../component/NavMenu.vue'
import BottomLine from '@/views/component/BottomLine.vue';
import gsap from 'gsap';
import { ref, onMounted } from 'vue'

const hasAnimated1 = ref(false);
const hasAnimated2 = ref(false);
const hasAnimated3 = ref(false);
const hasAnimated4 = ref(false);
onMounted(() => {
  gsap.fromTo('.DL-title span',
    { opacity: 0, visibility: 'hidden', y: 50 },
    { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
  );

  gsap.fromTo('.DL-title p',
    { opacity: 0, visibility: 'hidden', y: 50 },
    { opacity: 1, visibility: 'visible', y: 0, duration: 1, ease: 'ease-out' }
  );
  const observer1 = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
          hasAnimated1.value = true; // 标记动画已触发
          // // 触发动画：产品列表（从透明到不透明）
          gsap.fromTo('.section-1',
            { opacity: 0, visibility: 'hidden', y: 20 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
          );
          // observer1.disconnect();
        }
      });
    },
    { threshold: 0.1 } // 50% 可见时触发
  );

  const appElement1 = document.querySelector('.section-1');
  if (appElement1) {
    observer1.observe(appElement1);
  }

  const observer2 = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
          hasAnimated2.value = true; // 标记动画已触发
          // // 触发动画：产品列表（从透明到不透明）
          gsap.fromTo('.section-2',
            { opacity: 0, visibility: 'hidden', y: 200 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
          );
          // observer2.disconnect();
        }
      });
    },
    { threshold: 0 } // 50% 可见时触发
  );

  // 开始观察 .app 元素
  const appElement2 = document.querySelector('.section-2');
  if (appElement2) {
    observer2.observe(appElement2);
  }

  const observer3 = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
          hasAnimated3.value = true; // 标记动画已触发
          // // 触发动画：产品列表（从透明到不透明）
          gsap.fromTo('.section-3',
            { opacity: 0, visibility: 'hidden', y: 100 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
          );
          gsap.fromTo('.model-1',
            { opacity: 0,rotation:-5, visibility: 'hidden' },
            { opacity: 1,rotation:0, visibility: 'visible',  duration: 3, ease: 'ease-out' }
          );
          // observer3.disconnect();
        }
      });
    },
    { threshold: 0 } // 50% 可见时触发
  );

  // 开始观察 .app 元素
  const appElement3 = document.querySelector('.section-3');
  if (appElement3) {
    observer3.observe(appElement3);
  }


  const observer4 = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.boundingClientRect.top > 0) {
          hasAnimated4.value = true; // 标记动画已触发
          // // 触发动画：产品列表（从透明到不透明）
          gsap.fromTo('.section-4',
            { opacity: 0, visibility: 'hidden', y: -50 },
            { opacity: 1, visibility: 'visible', y: 0, duration: 2, ease: 'ease-out' }
          );
          // observer4.disconnect();
        }
      });
    },
    { threshold: 0.2 } // 50% 可见时触发
  );

  // 开始观察 .app 元素
  const appElement4 = document.querySelector('.section-4');
  if (appElement4) {
    observer4.observe(appElement4);
  }
});
</script>

<template>
  <div>
    <NavMenu />
    <div class="DL-title">
      <span>
        深度学习技术
      </span>
      <p>依托深度学习技术的革新，优化设备寿命预测过程<br>减少维护成本，提高能源系统的稳定性与效率</p>
    </div>
    <div class="section-1">
      <div class="section-introduction">
        <span>背景介绍</span>
        <p>
          叶片（如风力涡轮机叶片、航空发动机叶片等）在长时间运行中容易受到各种因素的影响，如疲劳、腐蚀、磨损、热应力等，这些因素逐渐导致叶片的性能退化，最终影响其使用寿命，甚至可能引发设备故障。传统的叶片寿命预测方法大多依赖于物理模型和经验公式，然而这些方法通常无法充分考虑多变的工况条件。随着技术的不断发展，深度学习为叶片寿命预测提供了新的解决思路，通过数据驱动的方法提高了预测的精度和可靠性。
        </p>
      </div>
    </div>
    <div class="section-2">
      <div class="section-apply">
        <span>深度学习在叶片寿命预测中的应用</span>
        <p>
          深度学习，作为一种强大的数据驱动技术，能够从大量历史数据中自动学习叶片的退化模式。通过采集叶片运行过程中的传感器数据（如振动、温度、应力等），深度学习模型（如LSTM、CNN等）可以对叶片的剩余寿命进行精准预测。
        </p>
        <div class="apply-img">
          <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E9%A2%84%E6%B5%8B1.png" style="width:450px"
            alt="">
          <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E9%A2%84%E6%B5%8B2.png" style="width:450px"
            alt="">
        </div>
      </div>
    </div>
    <div class="section-3">
      <div class="section-model">
        <span>常见的深度学习模型</span>
        <div class="all-model">
          <div class="model-1">
            <span>LSTM（长短时记忆网络）</span>
            <p> LSTM是一种特别适合处理时间序列数据的深度学习模型。它通过捕捉时间序列中的长期依赖性，能够有效预测叶片在不同工况下的寿命变化趋势，为维护决策提供数据支持，提前识别潜在风险。</p>
          </div>
          <div style="flex:0.2;"></div>
          <div class="model-1">
            <span>CNN（卷积神经网络）</span>
            <p>CNN是一种强大的图像处理神经网络模型，特别适用于对叶片表面的损伤检测。通过分析叶片表面图像数据，CNN能够识别细微的裂纹或腐蚀，为叶片的健康监测和预防性维护提供依据。</p>
          </div>
          <div style="flex:0.2;"></div>
          <div class="model-1">
            <span>强化学习（RL）</span>
            <p>强化学习是一种模拟决策过程的机器学习技术，能够通过不断试错优化决策策略。在叶片的运维中，RL可以帮助设计最佳维护策略，最大化设备的运行效率，减少故障风险，并降低长期维护成本。</p>
          </div>
        </div>
      </div>
    </div>
    <div class="section-4">
      <div class="section-apply">
        <span>研究方法与技术</span>
        <p>
          通过使用 LSTM 模型，基于传感器获取的时序数据（如温度、振动等），可以预测叶片的健康状态，并根据其变化趋势提前预测寿命。数据预处理是非常关键的一步，通常需要对数据进行清洗、去噪、归一化等操作。
        </p>
        <div class="apply-img">
          <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/%E6%B5%81%E7%A8%8B.png" style="width:400px;"
            alt="">
        </div>
      </div>
    </div>
    <BottomLine />
  </div>
</template>

<style scoped>
.DL-title {
  background-image: url('https://vue-project-image.oss-cn-shanghai.aliyuncs.com/title7.webp');
  height: 400px;
  /* background-color: black; */
  background-size: cover;
  /* 背景图片覆盖整个容器 */
  background-position: center;
  /* 确保图片居中 */
  background-repeat: no-repeat;
  /* 防止图片重复 */
  position: relative;
}

.DL-title span {
  color: rgb(255, 255, 255);
  font-size: 50px;
  font-weight: bold;
  font-family: Noto Sans;
  display: flex;
  justify-content: center;
  /* position: absolute; */
  /* 使用绝对定位 */
  /* top: 60px; */
  /* 距离上方100px */
  /* right: 210px; */
  position: absolute;
  top: 17%;
  left: 62%;
}

.DL-title p {
  color: white;
  font-size: 25px;
  font-family: Noto Sans;
  display: flex;
  justify-content: center;
  position: absolute;
  /* 使用绝对定位 */
  top: 40%;
  /* 距离上方100px */
  left: 50%;
  right: 0%;
  line-height: 50px;
}

.section-1 {
  opacity: 0;
  visibility: hidden;
  overflow: hidden;
}

.section-2 {
  opacity: 0;
  visibility: hidden;
  overflow: hidden;
}

.section-3 {
  opacity: 0;
  visibility: hidden;
  overflow: hidden;
}

.section-4 {
  opacity: 0;
  visibility: hidden;
  overflow: hidden;
}

.section-introduction {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background-color: #f4f6f7;
  border-radius: 8px;
  padding: 100px 100px;
  /* box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); */
  /* margin-bottom: 30px; */
}

.section-introduction span {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 30px;
}

.section-introduction p {
  color: #3b3b3b;
  font-size: 20px;
  margin-bottom: 30px;
  padding: 0px 150px;
  line-height: 1.5;
}

.section-apply {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  padding: 100px 100px;
  /* box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); */
  /* margin-bottom: 30px; */
}

.section-apply span {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 30px;
}

.section-apply p {
  color: #3b3b3b;
  font-size: 20px;
  margin-bottom: 30px;
  padding: 0px 100px 0px 100px;
  line-height: 1.8;
}

.apply-image {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-items: center;
}

.section-model {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  background-color: #f4f6f7;
  border-radius: 8px;
  padding: 100px 100px;
  height: 400px;
  /* box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); */
  /* margin-bottom: 30px; */
}

.section-model span {
  height:40px;
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 40px;
}

.all-model {
  display: flex;
  flex-direction: row;
}

.model-1 {
  display: flex;
  flex: 1;
  /* justify-content: center; */
  flex-direction: column;
  /* align-items: center; */
}

.model-1 span {
  font-size: 1.5em;
  font-weight: bold;
  color: #0d00c6;
}

.model-1 p {
  padding-top: 30px;
  padding-right: 20px;
  /* max-width: 100px??; */
  border-top: 1.1px solid rgb(177, 177, 177);
  color: #515151;
  font-size: 17px;
  margin-bottom: 30px;
  line-height: 1.7;
}

.container {
  padding: 50px 100px;
  /* background: #f4f7fc; */
  min-height: 100vh;
}

.content {
  font-size: 16px;
  color: #555;
  line-height: 1.8;
}

.content span {
  margin-top: 40px;
  font-size: 1.8em;
  color: #0053a1;
  font-weight: bold;
  text-transform: uppercase;
}

.content p {
  margin-bottom: 20px;
  font-size: 1.1em;
  color: #333;
}

ul {
  list-style-type: disc;
  margin-left: 20px;
  font-size: 1.1em;
}

ul li {
  margin-bottom: 10px;
}

.cta {
  margin-top: 50px;
  text-align: center;
}

button {
  padding: 15px 30px;
  font-size: 1.2em;
  background-color: #0084FF;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

button:hover {
  background-color: #005BB5;
  transform: scale(1.05);
}

button:active {
  transform: scale(0.98);
}

/* Add subtle shadow effects */
header,
.content,
.cta {
  background-color: white;
  border-radius: 10px;
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

header {
  margin-bottom: 50px;
}

footer {
  text-align: center;
  margin-top: 50px;
  color: #777;
}

footer p {
  font-size: 1.1em;
}
</style>
