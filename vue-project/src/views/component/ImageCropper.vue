<template>
    <div>
      <div v-if="imageUrl" class="cropper-container">
        <img id="cropper-image" :src="imageUrl" alt="Image to crop" />
      </div>
      <div class="buttons">
        <el-button type="primary" @click="confirmCrop">确认裁剪</el-button>
        <el-button @click="cancelCrop">取消</el-button>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted, watch, defineProps, defineEmits } from 'vue';
  import Cropper from 'cropperjs';
  import 'cropperjs/dist/cropper.css';
  
  const props = defineProps({
    imageUrl: {
      type: String,
      required: true,
    },
  });
  
  const emit = defineEmits(['cropperClose', 'cropperImageUrl']);
  
  const cropper = ref<Cropper | null>(null);
  
  onMounted(() => {
    const imageElement = document.getElementById('cropper-image') as HTMLImageElement;
    cropper.value = new Cropper(imageElement, {
      aspectRatio: 1, // 强制裁剪为正方形
      viewMode: 1, // 限制裁剪框
      autoCropArea: 1, // 默认裁剪框占满
    });
  });
  
  // 处理裁剪确认
  const confirmCrop = () => {
    const canvas = cropper.value?.getCroppedCanvas({ width: 500, height: 500 });
    canvas?.toBlob((blob) => {
      if (blob) {
        const imageUrl = URL.createObjectURL(blob);
        emit('cropperImageUrl', imageUrl); // 输出裁剪后的图片URL
      }
    });
  };
  
  // 取消裁剪
  const cancelCrop = () => {
    emit('cropperClose'); // 关闭裁剪界面
  };
  </script>
  
  <style scoped>
  .cropper-container {
    text-align: center;
  }
  
  .buttons {
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
  }
  </style>
  