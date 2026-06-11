<template>
    <div>
        <HeaderView></HeaderView>
        <div style="display: flex;">
            <el-container style="height: 700px; border: 1px solid #eee">
                <el-container>
                    <AsideView></AsideView>

                    <el-main>
                        <!--面包屑  -->
                        <el-breadcrumb separator-class="el-icon-arrow-right">
                            <el-breadcrumb-item @click="clearNavState" :to="{ path: '/' }">首页</el-breadcrumb-item>
                            <el-breadcrumb-item>功能菜单管理</el-breadcrumb-item>
                        </el-breadcrumb>
                        <br>


                    </el-main>
                </el-container>
            </el-container>
        </div>
    </div>
</template>
<script>
import axios from 'axios';

export default {
    data() {
        return {
            tableData: [],
            currentPage: 1, // 当前页
            pageSize: 10, // 每页条数
            total: 0, // 数据总条数
            searchForm: {
                name: "",
                gender: "",
                entrydate: []
            },
            sum: '',
            username: '',
            userimage: ''
        }
    },
    methods: {
        onSubmit: function () {
            alert("查询数据");
        },
        /*  handleSizeChange: function (val) {
             alert("每页记录数变化" + val)
         },
         handleCurrentChange: function (val) {
             alert("页码发生变化" + val)
         }, */
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
        handleSizeChange(val) {
            this.currentPage = 1;
            this.pageSize = val;
        },
        //当前页改变时触发 跳转其他页
        handleCurrentChange(val) {
            this.currentPage = val;
        },
    },
    mounted() {
        //发送异步请求,获取数据
        this.username = sessionStorage.getItem('name');
        this.userimage = sessionStorage.getItem('image');
        axios.get("http://localhost:8080/cas").then((result) => {
            this.tableData = result.data.data;
            this.sum = this.tableData.length
        });
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
</style>