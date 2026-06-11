<template>
    <el-aside width="230px" style="border: 1px solid #eff5f7">
        <el-menu :default-openeds="['1', '2']">
            <el-submenu index="1">
                <template slot="title"><i class="el-icon-message"></i>系统信息管理</template>
                <el-menu-item index="1-1" @click="op1">
                    用户管理</el-menu-item>
                <el-menu-item index="1-2" @click="op2">
                    角色管理</el-menu-item>
                <el-menu-item index="1-3" @click="op3">
                    操作管理</el-menu-item>
            </el-submenu>
            <el-submenu index="2">
                <template slot="title"><i class="el-icon-message"></i>业务管理</template>
                <el-menu-item index="2-1" @click="op4">
                    数据集管理</el-menu-item>
                <el-menu-item index="2-2" @click="op5">
                    历史预测数据管理</el-menu-item>
                <el-menu-item index="2-2" @click="op6">
                    日志管理</el-menu-item>
            </el-submenu>

        </el-menu>
    </el-aside>
</template>
<script>
export default {
    name: 'AsideView',
    methods: {
        op1() {
            this.$router.push('/User');
        },
        op2() {
            this.$router.push('/Role');
        },
        op3() {
            this.$router.push('/op');
        },
        op4() {
            this.$router.push('/Dataset');
        },
        op5() {
            this.$router.push('/PredictData');
        },
        op6() {
            this.$router.push('/sys-log');
        },

    },
    mounted() {
        console.log("Aside组件挂载完成");
    }
}


</script>