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
  <div class="Right-bgc" :style="{ backgroundColor: props.backgroundColor }">
    <div class="Right-container">
      <!-- 图片部分 -->
      <div class="Right-text" :style="{ flex: 10 - props.imageRatio - 2 }">
        <span>{{ title }}</span>
        <p v-for="(text, index) in textContent" :key="index">{{ text }}</p>
      </div>
      
      <div style="flex:1;"></div>
      <!-- 文字部分 -->
      <div class="Right-image" :style="{ flex: props.imageRatio }">
        <img :src=imageUrl alt="Image" />
      </div>
    </div>
  </div>

</template>

<style scoped>
.Right-bgc {
  width: 100%;
  height: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  /* 确保内容不换行 */
  margin: 0 auto;
}

.Right-container {
  display: flex;
  width: 1200px;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  /* 确保内容不换行 */
  margin: 0 auto;
}


.Right-image {
  padding: 10px;
  padding-right:50px;
}

.Right-image img {
  width: 100%;
  height: auto;
}

.Right-text {
  padding: 20px;
  padding-left:50px;
  /* margin-left:100psx; */
  display: flex;
  flex-direction: column;
  justify-content: center;
  /* align-items: center; */
}

.Right-text span {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 10px;
}

.Right-text p {
  font-family: 方正汉真广标;
  font-size: 1.2em;
  color: rgb(118, 118, 118);
  line-height: 1.6;
}
</style>
