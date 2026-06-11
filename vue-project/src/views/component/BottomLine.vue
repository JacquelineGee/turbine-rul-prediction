<template>
    <div class="page-container">
        <!-- 其他内容 -->
        <div class="bottom-line">
            <span>
                燃气轮机剩余寿命预测小组
            </span>
        </div>
    </div>
</template>

<style scoped>
.page-container {
    display: flex;
    flex-direction: column;
    min-height: 60vh; /* 使页面至少有屏幕的高度 */
}

.bottom-line {
    margin-top: auto; /* 将底部内容推到最底部 */
    width: 100%;
    height: 80px;
    background-color: #1100ff;
    display: flex;
    justify-content: center;
    align-items: center;
}

.bottom-line span {
    position: relative;
    left: -30%;
    font-size: 18px;
    color: #dedede;
    font-family: Noto Sans;
}
</style>
