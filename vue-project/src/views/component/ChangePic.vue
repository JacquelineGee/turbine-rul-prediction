<script lang="ts" setup>
import { ref, onMounted,defineProps } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  contents: {
    type: Array as () => Array<{
      image: string;
      title: string;
      text: string;
    }>,
    required: true
  }
});

const nowID = ref(0);

onMounted(() => {
  // 组件加载完成后让背景图片缓缓变化
  gsap.to('.image-container',
    { backgroundImage: `url(${props.contents[0].image})`}
  );
});

const onMouseEnter = (index: number) => {
  nowID.value = index;
  gsap.set('.image-container', {
    backgroundImage: `url(${props.contents[nowID.value].image})`,
    opacity: 1, // 隐藏背景图片
    delay:0.2
  });
  gsap.fromTo('.text', {
    opacity: 0,
    ease: 'ease-out'
  },
    {
      opacity: 1,
      duration: 0.6,
      ease: 'ease-out'
    });
//   gsap.to('.image-container',
//     {
//       opacity: 1,  // 显示新的背景图片
//       duration: 2,
//       ease: 'ease-out'
//     });

  // 让图标进行缩放 (例: 当鼠标悬浮时，图标稍微放大)
  gsap.to(`.image-section:nth-child(${index + 1}) .icon-class`, {
    scale: 1.5,
    duration: 0.5,
    delay: 0.2
  });
};

const onMouseLeave = () => {
  gsap.to(`.image-section .icon-class`, {
    scale: 1,
    duration: 0.5
  });
}
</script>

<template>
  <div class="container">
    <!-- 文字部分 -->
    <div class="text">
      <span>{{ contents[nowID].title }}</span>
      <div style="margin-top:20px;width:400px;">
        <p>{{ contents[nowID].text }}</p>
      </div>
    </div>

    <!-- 图片部分 -->
    <div class="image">
      <div class="image-container">
        <div class="image-section" v-for="(content, index) in contents" :class="{ 'hovered': nowID === index }"
          @mouseenter="onMouseEnter(index)" @mouseleave="onMouseLeave">
          <el-icon class="icon-class">
            <CirclePlusFilled />
          </el-icon>
          <span>
            {{ content.title }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped>
.container {
  display: flex;
  width: 100%;
  /* 使容器宽度适应父元素 */
  max-width: 1200px;
  /* 最大宽度 */
  height: 500px;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  margin: 0 auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  /* border-radius: 10px; */
  margin-top: 50px;
}

.image {
  flex: 6;
}

.card.hovered .card-content {
  z-index: 10;
  /* 悬浮时将内容层级提升 */
}

.image-container {
  display: flex;
  width: 100%;
  /* width:100px; */
  height: 500px;
  background-size: cover;
  /* 保持图片比例，并覆盖整个区域 */
  background-repeat: no-repeat;
  background-position: center;
  /* 保证图片居中 */
  /* border-right: 1px solid rgb(0, 0, 0); 每个部分之间的边界 */
  /* 根据需要调整高度  */
}

.image-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-right: 0.5px solid rgb(255, 255, 255);
  color: white;
}

.image-section:last-child {
  border-right: none;
}

.image-section .icon-class {
  margin-bottom: 8px;
  color: white;
}

.text {
  padding: 0px 0px 0px 50px;
  display: flex;
  flex: 4;
  flex-direction: column;
  justify-content: center;
}

.text span {
  font-size: 2em;
  font-weight: bold;
  margin-bottom: 10px;
  margin-right:100px;
}

.text p {
  font-family: 方正汉真广标;
  font-size: 1.3em;
  color: rgb(72, 72, 72);
  line-height: 1.6;
}
</style>
