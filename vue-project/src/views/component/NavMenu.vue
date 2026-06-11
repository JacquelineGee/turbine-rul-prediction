<script lang="ts" setup>
import { ref } from 'vue'
import { useUserStore } from '@/store/userStore'
import { useUser } from '@/store/user'

const activeIndex = ref('1')

// 使用 Pinia 的用户信息
const userStore = useUserStore();
const userS = useUser();
console.log(userStore);

const handleSelect = (key: string, keyPath: string[]) => {
    console.log(key, keyPath);
}

import { ElMessage } from 'element-plus'
const logout = () => {
    userStore.logout();
    ElMessage({
        showClose: true,
        message: '已退出登录',
        type: 'success',
    })
}
</script>

<template>
    <div id="header">
        <!-- 容器使用flex布局，让菜单始终水平居中 -->
        <div class="nav_menu">
            <div class="logo-container">
                <router-link to="/">
                    <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/title8.png" alt="Logo">
                </router-link>
            </div>
            <div class="menu_container">
                <el-menu :default-active="activeIndex" class="el-menu-demo_nav" :ellipsis="false" mode="horizontal"
                    @select="handleSelect" background-color="#1100ff" text-color="#fff" active-text-color="#E0E0E0" router>
                    <div style="width:410px;"></div>
                    <el-menu-item style="width:90px;" index="/turbine">
                        轮机介绍
                    </el-menu-item>
                    <el-menu-item style="width:90px;" index="/structure">
                        轮机结构
                    </el-menu-item>
                    <el-menu-item style="width:90px;" index="/methods">
                        预测方法
                    </el-menu-item>
                    <el-menu-item style="width:90px;" index="/data-collecting">
                        数据采集
                    </el-menu-item>
                    <el-menu-item style="width:90px;" index="/model">
                        模型构建
                    </el-menu-item>
                    <el-menu-item style="width:110px;" index="/data-show">
                        数据可视化
                    </el-menu-item>
                    <el-menu-item style="width:80px;" index="/AI-Chat">
                       AI对话
                    </el-menu-item>
                    <!-- <div style="width:40px"></div> -->
                    <el-sub-menu popper-class="custom-submenu" index="1">
                        <template #title>
                            <img v-if="userS.user_info.image" :src="userS.user_info.image"
                                style="width: min(60px, 100%);height: min(60px, 100%);clip-path:circle(30px at center)" />
                            <span v-else class="words">未登录</span>
                            <!-- <div style="width: 10px;"></div> -->
                            <span v-if="userS.user_info.userName">&nbsp;&nbsp;{{ userS.user_info.userName }}</span>
                        </template>
                        <!-- 未登录时只显示登录按钮 -->
                        <el-menu-item v-if="!userS.user_info.userName" index="/login">登录</el-menu-item>
                        <!-- 登录后显示账户信息和退出登录按钮 -->
                        <el-menu-item v-if="userS.user_info.userName" index="/user-info">账户信息</el-menu-item>
                        <el-menu-item v-if="userS.user_info.userName" @click="logout">退出登录</el-menu-item>
                    </el-sub-menu>
                </el-menu>
            </div>
        </div>
    </div>
</template>

<style scoped>
#header {
    background: #1100ff no-repeat center top;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.nav_menu {
    display: flex;
    justify-content: center;
    background-color: #1100ff;
    height: 90px;
    /* 导航栏高度 */
    align-items: center;
}

.logo-container {
    position: absolute;
    left: 20px;
    z-index: 1000;
}

.el-menu--horizontal {
    --el-menu-horizontal-height: 90px;
    border-bottom: none !important;
    /* 移除底部边框 */
}

.el-menu-demo_nav .el-menu-item {
    width: 110px;
    text-align: center;
    font-size: 17px;
    font-family: Noto Sans;
}

.el-menu-demo_nav .el-menu-item:hover {
    font-weight: bold;
}

:deep(.custom-submenu) {
    width: 100px !important; /* 调整下拉框的宽度 */
    min-width: auto !important; /* 防止 Element Plus 默认最小宽度 */
}

:deep(.custom-submenu .narrow-item) {
    width: 100% !important; /* 调整宽度 */
    min-width: auto !important; /* 防止 Element Plus 默认最小宽度 */
    padding: 0 10px !important; /* 调整内边距 */
    margin: 0 auto !important; /* 水平居中 */
}

.custom-submenu {
    background-color: #ffffff;
    /* 自定义背景颜色 */
}

.custom-submenu .el-menu-item {
    width: 100%;
    color: #ffffff;
    /* 自定义菜单项文字颜色 */
    text-align: center;
}

.words {
    font-size: 17px;
    font-family: Noto Sans;
}

.words:hover {
    font-weight: bold;
}

.iconstyle:hover {
    font-weight: bold;
}
</style>