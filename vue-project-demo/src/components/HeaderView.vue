<template>
    <div class="Nav-menu">
        <div class="Nav-style">
            <router-link to="/home" style="text-decoration: none; margin-right: 10px;" class="fixed-color-link">
                <img src="https://vue-project-image.oss-cn-shanghai.aliyuncs.com/nav-title3.png" alt="">
                </router-link>
            <div style="display: flex; align-items: center; justify-content: flex-end; margin-left: auto;">
                <span class="welcomeword" style="margin-right: 10px; color: #fefefe;">欢迎{{ username }}！</span>
                <el-avatar :src="userimage" style="width: 50px; height: 50px; margin-right: 10px; "></el-avatar>
                <el-button type="info" @click="logout" style="margin-right: 5vw;">退出</el-button>
            </div>
        </div>
    </div>
</template>
<!-- #3c2efb -->
<script>
export default {
    name: 'HeaderView',
    data() {
        return {
            username: '',
            userimage: '',
        }
    },
    methods: {
        logout() {
            this.$confirm('即将注销，请确认', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '已注销!'
                }
                );
                window.sessionStorage.clear();
                //this.$message.success('已注销');
                this.$router.push("/")
            }).catch(() => {

            });

        },

    },
    mounted() {
        this.username = sessionStorage.getItem('name');
        this.userimage = sessionStorage.getItem('image');
        console.log(this.userimage);
        console.log("组件挂载完成");

    }
}

</script>


<style>
.fixed-color-link {
    text-decoration: none;
    color: #000000;
    /* 替换为你想要的颜色 */
}

/* 覆盖访问过的链接颜色 */
.fixed-color-link:visited {
    color: #000000;
    /* 保持与未访问时相同的颜色 */
}

.welcomeword {
    margin-right: 15px;
    font-size: 15px;
}

.Nav-menu {
    font-size: 25px;
    background-color: #3c2efb;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.Nav-style {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
}
</style>