<script lang="ts" setup>
import { defineProps } from 'vue'

// 定义 props，用来传递图片与文字的比例
const props = defineProps({
  imageUrl: {
    type: String,
    default: 'https://vue-project-image.oss-cn-shanghai.aliyuncs.com/blade2.jpg', // 默认图片
  },
  title: {
    type: String,
    default: '这里是标题',
  },
  textContent: {
    type: Array as () => string[], // 传递多个段落
    default: () => ['这是一段可以编辑的文本内容。可以在这里放置任何你需要展示的文字。', '你还可以添加更多的描述内容或者其他元素。']
  },
  imageRatio: {
    type: Number,
    default: 4, // 默认值为4，图片占4/10，文字占6/10
  },
  backgroundColor: {
    type: String,
    default: '#ffffff', // 默认背景颜色为白色
  }
})
</script>

<template>
  <div class="Left-bgc" :style="{ backgroundColor: props.backgroundColor }">
    <div class="Left-container">
      <!-- 图片部分 -->
      <div class="Left-image" :style="{ flex: props.imageRatio }">
        <img :src=imageUrl alt="Image" />
      </div>
      <div style="flex:0.5;"></div>
      <!-- 文字部分 -->
      <div class="Left-text" :style="{ flex: 10 - props.imageRatio - 1 }">
        <span>{{ title }}</span>
        <p v-for="(text, index) in textContent" :key="index">{{ text }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.Left-bgc {
  width: 100%;
  height: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  /* 确保内容不换行 */
  margin: 0 auto;
}

.Left-container {
  display: flex;
  width: 1200px;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  /* 确保内容不换行 */
  margin: 0 auto;
  /* 让容器在父级元素（通常是 body）中水平居中 */
  /* box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); 添加阴影 */
  /* border-radius: 10px; 可选：增加圆角美观度 */
  /* margin-top:50px; */
  /* min-height: 100vh; 让容器至少占满整个视口高度 */
}


.Left-image {
  padding: 10px;
  padding-left: 50px;
}

.Left-image img {
  width: 100%;
  height: auto;
}

.Left-text {
  padding: 20px;
  /* margin-left:100psx; */
  display: flex;
  flex-direction: column;
  justify-content: center;
  /* align-items: center; */
}

.Left-text span {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 10px;
}

.Left-text p {
  font-family: 方正汉真广标;
  font-size: 1.2em;
  color: rgb(118, 118, 118);
  line-height: 1.6;
  padding-right: 70px;
}
</style>
