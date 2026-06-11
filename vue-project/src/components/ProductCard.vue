<template>
  <div class="product-card-wrapper">
    <div class="product-card">
      <div class="product-image-container">
        <img :src="image" :alt="title" class="product-image" />
      </div>
      <div class="product-card-text">
        <h2 class="product-title">{{ title }}</h2>
        <p class="product-description">{{ description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  description: String,
  image: String,
});
</script>

<style scoped>
.product-card-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
  /* 保证平滑过渡 */
}

.product-card {
  border-radius: 15px;
  padding: 20px;
  text-align: center;
  width: 320px;
  /* 固定宽度 */
  min-height: 410px;
  /* 设置固定的最小高度，确保所有卡片高度一致 */
  background: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-card-text{
  
  text-decoration: none;
}

.product-card-wrapper:hover .product-card {
  transform: scale(1.2);
  /* 整体放大 */
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.product-image-container {
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  border-radius: 12px;
}

.product-image {
  max-width: 100%;
  height: auto;
}

.product-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #333;
  margin: 18px 0 12px;
}

.product-description {
  font-size: 1rem;
  color: #777;
  line-height: 1.5;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .product-card {
    width: 100%;
    /* 确保在小屏幕时卡片宽度自适应 */
    padding: 15px;
    min-height: 450px;
    /* 调整小屏幕时的最小高度 */
  }

  .product-title {
    font-size: 1.5rem;
  }

  .product-description {
    font-size: 0.95rem;
  }
}
</style>
