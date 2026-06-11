<template>
    <div>
        <HeaderView></HeaderView>
        <el-container>
            <el-container>
                <AsideView></AsideView>
                <el-main>
                    <!-- 搜索表单 -->
                    <el-form :inline="true" :model="formInline" class="demo-form-inline">
                        <el-form-item label="预测编号">
                            <el-input v-model="formInline.predictId" placeholder="预测编号" style="width:150px" clearable />
                        </el-form-item>
                        <el-form-item label="上传用户id">
                            <el-input v-model="formInline.userId" placeholder="上传用户id" style="width:150px" clearable />
                        </el-form-item>
                        <el-form-item label="使用模型">
                            <el-input v-model="formInline.model" placeholder="使用模型" style="width:150px" clearable />
                        </el-form-item>
                        <el-form-item label="预测日期">
                            <el-date-picker v-model="formInline.date" type="daterange" unlink-panels range-separator="到"
                                start-placeholder="开始日期" end-placeholder="结束日期" clearable />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="onSubmit">查询</el-button>
                        </el-form-item>
                    </el-form>
                    <!-- 数据表格 -->
                    <el-table :data="filterTableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
                        style="width: 100%">
                        <el-table-column label="预测编号" prop="predictId" />
                        <el-table-column label="预测用户" prop="userName" />
                        <el-table-column label="使用模型" prop="modelName" />
                        <el-table-column label="预测时间" prop="predictTime" :formatter="formatUpdateTime" />
                        <el-table-column align="right">
                            <template #header>
                                <el-input v-model="search" size="small" placeholder="Type to search" />
                            </template>
                            <template #default="scope">
                                <el-button size="small" type="danger"
                                    @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>

                    <!-- 分页 -->
                    <div class="demo-pagination-block">
                        <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[1, 5, 10, 20]"
                            :size="size" :disabled="disabled" :background="background"
                            layout="total, sizes, prev, pager, next, jumper" :total="filterTableData.length"
                            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                    </div>
                </el-main>
            </el-container>
            <!-- 新增数据集对话框 -->
        </el-container>
    </div>
</template>

<script>
//import HeaderView from '@/components/HeaderView.vue';
import axios from 'axios';
//import  AsideView  from '../components/AsideView.vue';

export default {
    data() {
        return {
            username: '',
            formInline: {
                predictId: '',
                userId: '',
                date: '',
                datestart: '',
                dateend: '',
                model: ''
            },
            ruleForm: {
                datasetName: '',
                datasetUrl: '',
                userId: ''
            },
            rules: {
                datasetName: [{ required: true, message: '请输入数据集名称', trigger: 'blur' }],
                datasetUrl: [{ required: true, message: '请上传数据集文件', trigger: 'blur' }]
            },
            tableData: [],
            search: '',
            dialogVisible: false,
            formSize: 'default',
            currentPage: 1,
            pageSize: 5,
            size: 'default',
            userimage: '',
            operationList: [],
            background: false,
            disabled: false,
            userId: sessionStorage.getItem('id'),
            //user_info: JSON.parse(localStorage.getItem("user_information")),
            uploadRef: null
        };
    },
    computed: {
        filterTableData() {
            return this.tableData.filter(data => !this.search || String(data.predictId).toLowerCase().includes(this.search.toLowerCase()));
        }
    },
    methods: {
        getOperationList() {
            if (this.userId == '8888')
                axios.post("/power/13").then((result) => {
                    this.operationList = result.data.data;
                });
            else
                axios.post("/power/112").then((result) => {
                    this.operationList = result.data.data;
                });
        },
        async submitForm(formEl) {
            if (!formEl) return;

            try {
                console.log('表单提交成功:', this.ruleForm);
                const dataset = {
                    datasetName: this.ruleForm.datasetName,
                    datasetUrl: this.ruleForm.datasetUrl,
                    uploadUserId: this.ruleForm.userId
                };

                // 发起 POST 请求
                const response = await axios.post('http://127.0.0.1:8080/addDataset', dataset);

                if (response.data.code === 1) {
                    this.$message.success('上传成功！')
                    this.dialogVisible = false;

                    // 获取新的数据
                    const dataResponse = await axios.get('http://127.0.0.1:8080/dataSet');
                    this.tableData = dataResponse.data.data;

                    // 重置表单
                    this.resetForm(formEl);
                } else {
                    this.$message.error('提交失败：' + response.data.message)
                    //ElMessage.error('提交失败：' + response.data.message);
                }
            } catch (error) {
                console.error('提交失败:', error);
                this.$message({
                    type: 'error',
                    message: '提交失败，请重试！'
                });
            }
        },
        async handleEdit(index, row) {
            console.log('编辑行:', index, row);
        },
        handleDelete(index, row) {
            this.$confirm('确定要删除该预测 记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async (confirmResult) => {
                if (confirmResult === 'confirm') {
                    try {
                        const response = await axios.delete(`http://127.0.0.1:8080/deleteById2/${row.predictId}`);
                        if (response.data.code === 1) {
                            this.$message({
                                type: 'success',
                                message: '删除成功'
                            });

                            // 更新表格数据
                            const newTableData = this.tableData.filter(data => data.predictId !== row.predictId);
                            this.tableData = newTableData;
                        } else {
                            this.$message({
                                type: 'error',
                                message: '删除失败' + response.data.message
                            });

                        }
                    } catch (error) {
                        console.error('删除失败:', error);
                        this.$message({
                            type: 'error',
                            message: '删除失败' + error
                        });
                    }
                }
            }).catch(() => {
                // 用户取消时的处理
                console.log('删除操作被取消');
            });
        },
        async onSubmit() {
            if (this.formInline.date !== '') {
                let dates = this.formInline.date[0];
                let datee = this.formInline.date[1];
                let date = new Date(dates);
                let formattedDate = date.getFullYear() + '-' +
                    ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
                    ('0' + date.getDate()).slice(-2);
                this.formInline.datestart = formattedDate;
                date = new Date(datee);
                formattedDate = date.getFullYear() + '-' +
                    ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
                    ('0' + date.getDate()).slice(-2);
                this.formInline.dateend = formattedDate;
            }
            console.log(this.formInline.datestart, ' ', this.formInline.dateend);
            try {
                const response = await axios.post('http://127.0.0.1:8080/IfSelectPrediction', {
                    predictId: this.formInline.predictId,
                    userId: this.formInline.userId,
                    predictModel: this.formInline.model,
                    updateTimeStart: this.formInline.datestart,
                    updateTimeEnd: this.formInline.dateend
                });
                if (response.data.msg === "success") {
                    this.$message({
                        type: 'success',
                        message: '查询成功'
                    });
                    //ElMessage.success('查询成功');
                    this.tableData = response.data.data;
                }
                else {
                    this.$message({
                        type: 'error',
                        message: '查询失败'
                    });
                    // ElMessage.error('查询失败');
                }
            }
            catch (error) {
                console.error('查询失败:', error);
                this.$message({
                    type: 'error',
                    message: '查询失败'
                });
                //ElMessage.error('查询失败');
            }
        },
        formatUpdateTime(row, column, cellValue) {
            return cellValue.replace('T', ' ').split('+')[0];
        },
        handleSizeChange(val) {
            this.currentPage = 1;
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },

    },
    mounted() {
        this.username = sessionStorage.getItem('name');
        this.ruleForm.userId = sessionStorage.getItem('id');
        this.userId = sessionStorage.getItem('id');
        this.userimage = sessionStorage.getItem('image');
        axios.get('http://127.0.0.1:8080/predictions')
            .then((response) => {
                if (response.data.code === 1) {
                    this.tableData = response.data.data;
                    this.$message.success("数据集加载成功!")
                    //ElMessage.success('数据集加载成功');
                }
                else {
                    //ElMessage.error('数据集加载失败');
                }
            })
            .catch((error) => {
                console.error('数据集加载失败:', error);
                this.$message.error("数据集加载失败，请重试")
                //ElMessage.error('数据集加载失败，请重试');
            });
    },
};
</script>

<style>
.demo-form-inline .el-input {
    --el-input-width: 150px;
}

.demo-form-inline .el-select {
    --el-select-width: 220px;
}

.demo-pagination-block+.demo-pagination-block {
    margin-top: 10px;
}

.demo-pagination-block .demonstration {
    margin-bottom: 16px;
}
</style>