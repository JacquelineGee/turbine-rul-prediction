<script lang="ts" setup>
import { ref } from 'vue';
import { gsap } from 'gsap';

const props = defineProps({
  cards: {
    type: Array as () => Array<{
      title: string;
      description: string[];
      icon: string;
      backgroundImage: string;
    }>,
    required: true
  }
});

const hoveredIndex = ref<number | null>(null);

const onMouseEnter = (index: number) => {
  hoveredIndex.value = index;

  // 水平方向平移背景图像
  gsap.to(`.card:nth-child(${index + 1})`, {
    backgroundPosition: index === 0 ? '100% 50%' : '0% 50%', // 平移到右边或左边
    width:500,
    boxShadow: '0 10px 20px rgba(0, 0, 0, 0.3)',
    duration: 0.3,
    zIndex: 10,  // 确保卡片位于最前面
  });

  // 隐藏标题和图标
  gsap.to(`.card:nth-child(${index + 1}) .icon-title`, {
    opacity: 0,
    y: -10,     // 轻微向上移动
    duration: 0.2
  });

  // 显示详情内容
  gsap.to(`.card:nth-child(${index + 1}) .detail`, {
    scaleX: 1,
    scaleY: 1,
    opacity: 1,
    y: 0,
    duration: 0.2,
    delay: 0.1
  });
};

const onMouseLeave = () => {
  if (hoveredIndex.value !== null) {
    gsap.to(`.card:nth-child(${hoveredIndex.value! + 1})`, {
      backgroundPosition: '50% 50%',  // 恢复背景图像位置
      boxShadow: 'none',
      width:400,
      zIndex: 1,  // 恢复原始层级
      duration: 0.3,
    });

    // 恢复标题和图标位置
    gsap.to(`.card:nth-child(${hoveredIndex.value! + 1}) .icon-title`, {
      opacity: 1,
      y: 0,
      duration: 0.3
    });

    // 隐藏详情
    gsap.to(`.card:nth-child(${hoveredIndex.value! + 1}) .detail`, {
      opacity: 0,
      y: 20,
      duration: 0.3
    });

    hoveredIndex.value = null;
  }
};
</script>

<template>
    <div class="card-container">
        <div
            class="card"
            v-for="(card, index) in cards"
            :key="index"
            :style="{ backgroundImage: `url(${card.backgroundImage})` }"
            :class="{ 'hovered': hoveredIndex === index }"
            @mouseenter="onMouseEnter(index)"
            @mouseleave="onMouseLeave"
        >
            <div class="card-content">
                <div class="icon-title" v-show="hoveredIndex !== index">
                    <img :src="card.icon" class="icon" alt="icon" />
                    <span>{{ card.title }}</span>
                </div>
                <div class="detail" v-show="hoveredIndex === index">
                    <span>{{ card.title }}</span>
                    <br>
                    <p v-for="(text, idx) in card.description" :key="idx">{{ text }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.card-container {
  display: flex;
}

.card {
  position: relative;
  width: 400px;
  height: 400px;
  background-size: 200% 100%; /* 背景图片的宽度是容器的两倍 */
  background-position: 50% 50%; /* 初始背景位置居中 */
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: rgb(255, 255, 255);
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}

.card-content {
  width: 100%;
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  backdrop-filter: blur(5px);
  z-index: 1; /* 默认内容层级较低 */
}

.card.hovered .card-content {
  z-index: 10; /* 悬浮时将内容层级提升 */
}

.icon-title {
  display: flex;
  flex-direction: column;
  align-items: center;
  opacity: 1;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.icon-title span{
  font-size: 1.5em;
  font-weight: bold;
}

.icon {
  width: 50px;
  height: 50px;
  margin-bottom: 10px;
}

.detail {
  opacity: 0;
  transition: opacity 0.3s ease, transform 0.3s ease;
  padding: 15px;
  width: 70%;
  margin: 0 auto;
}

.detail span{
  font-size: 2em;
  font-weight: bold;
  line-height:2;
}

.detail p {
  color: rgb(247, 247, 247);
  font-family: '方正汉真广标', sans-serif;
  font-size: 16px; /* 适当增加字体大小 */
  line-height: 1.7; /* 行间距 */
  margin: 5px 0; /* 添加间距 */
}

.card.hovered {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
}
</style>
