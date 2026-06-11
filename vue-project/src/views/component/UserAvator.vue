<template>
  <div>
    <!-- 裁剪对话框 -->
    <el-dialog v-model="dialogVisible" title="裁剪头像" width="500px">
      <div class="cropper-container">
        <img ref="image" :src="imageSrc" alt="Avatar" style="max-width: 100%;" />
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="cropImage">确认</el-button>
      </template>
    </el-dialog>

    <!-- 头像展示区域，点击触发裁剪 -->
    <div class="avatar-container" @click="openCropper">
      <img :src="initialImage" alt="Avatar" class="avatar" />
      <div class="avatar-mask">点击修改</div>
    </div>

    <!-- 选择本地图片按钮 -->
    <el-upload action="#" :show-file-list="false" :before-upload="beforeUpload" class="upload-button">
      <el-button type="primary">选择本地图片</el-button>
    </el-upload>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.css';
import { ElMessage } from 'element-plus';
import axios from '@/utils/axios';

const props = defineProps({
  initialImage: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['upload-success']);

const dialogVisible = ref(false);
const imageSrc = ref(props.initialImage); // 默认图片
const cropper = ref<Cropper | null>(null);
const image = ref<HTMLImageElement | null>(null);

// 监听 imageSrc 的变化，重新初始化 Cropper
watch(imageSrc, (newSrc) => {
  if (dialogVisible.value && image.value) {
    if (cropper.value) {
      cropper.value.destroy(); // 销毁旧的 Cropper 实例
    }
    cropper.value = new Cropper(image.value, {
      aspectRatio: 1, // 1:1 比例
      viewMode: 1,
    });
  }
});

// 打开裁剪对话框
const openCropper = () => {
  dialogVisible.value = true;
  setTimeout(() => {
    if (image.value) {
      if (cropper.value) {
        cropper.value.destroy(); // 销毁旧的 Cropper 实例
      }
      cropper.value = new Cropper(image.value, {
        aspectRatio: 1, // 1:1 比例
        viewMode: 1,
      });
    }
  }, 0);
};

// 选择本地图片
const beforeUpload = (file: File) => {
  const reader = new FileReader();
  reader.onload = (e) => {
    imageSrc.value = e.target?.result as string; // 设置图片源
    openCropper(); // 打开裁剪对话框
  };
  reader.readAsDataURL(file);
  return false; // 阻止默认上传行为
};

// 裁剪图片并上传
const cropImage = async () => {
  if (cropper.value) {
    const croppedCanvas = cropper.value.getCroppedCanvas();
    if (croppedCanvas) {
      croppedCanvas.toBlob(async (blob) => {
        if (blob) {
          const formData = new FormData();
          formData.append('image', blob, 'avatar.png');

          try {
            // 调用上传接口
            const response = await axios.post('http://localhost:8080/upload', formData, {
              headers: {
                'Content-Type': 'multipart/form-data',
              },
            });

            if (response.data.code === 1) {
              const imageUrl = response.data.data; // 获取返回的 URL
              emit('upload-success', imageUrl); // 触发上传成功事件
              ElMessage.success('头像上传成功');
              dialogVisible.value = false;
            } else {
              ElMessage.error('头像上传失败');
            }
          } catch (error) {
            console.error('上传失败:', error);
            ElMessage.error('头像上传失败');
          }
        }
      });
    }
  }
};

onUnmounted(() => {
  if (cropper.value) {
    cropper.value.destroy(); // 销毁 Cropper 实例
  }
});
</script>

<style scoped>
.cropper-container {
  width: 100%;
  height: 300px;
  overflow: hidden;
}

.avatar-container {
  position: relative;
  width: 140px;
  height: 140px;
  cursor: pointer;
  margin: 0 auto; /* 使头像容器居中 */
  margin-bottom: 20px; /* 增加间距 */
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  overflow: hidden; /* 确保头像图片不会超出容器 */
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片按比例填充 */
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:hover .avatar-mask {
  opacity: 1;
}

.upload-button {
  margin-top: 10px; /* 调整按钮间距 */
  display: flex;
  justify-content: center; /* 使按钮居中 */
}
</style>