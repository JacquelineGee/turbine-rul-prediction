<template>
    <div>
        <HeaderView></HeaderView>
        <el-container>
            <el-container>
                <AsideView></AsideView>
                <el-main>
                    <!-- 搜索表单 -->
                    <el-form :inline="true" :model="formInline" class="demo-form-inline">
                        <el-form-item label="数据集名">
                            <el-input v-model="formInline.dataset" placeholder="数据集名" style="width:170px" clearable />
                        </el-form-item>
                        <el-form-item label="上传用户">
                            <el-input v-model="formInline.user" placeholder="上传用户" style="width:170px" clearable />
                        </el-form-item>
                        <el-form-item label="上传日期">
                            <el-date-picker v-model="formInline.date" type="daterange" unlink-panels range-separator="到"
                                start-placeholder="开始日期" end-placeholder="结束日期" clearable />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="onSubmit">查询</el-button>
                            <el-button type="primary" plain @click="dialogVisible = true">新增</el-button>
                        </el-form-item>
                    </el-form>

                    <!-- 数据表格 -->
                    <el-table :data="filterTableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
                        style="width: 100%">
                        <el-table-column label="数据集名" prop="datasetName" />
                        <el-table-column label="上传用户" prop="userName" />
                        <el-table-column label="上传日期" prop="updateTime" :formatter="formatUpdateTime" />
                        <el-table-column label="数据集url">
                            <template #default="scope">
                                <el-link :href="scope.row.datasetUrl" target="_blank" :underline="false">{{
                                    scope.row.datasetUrl }}</el-link>
                            </template>
                        </el-table-column>
                        <el-table-column align="right">
                            <template #header>
                                <el-input v-model="search" size="small" placeholder="Type to search" />
                            </template>
                            <template #default="scope">
                                <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>

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
            <el-dialog :visible.sync="dialogEditVisible" title="修改数据集名称" width="500" :before-close="handleClose">
                <el-form ref="editForm" :model="{ updName }" :rules="rules" label-width="auto" class="demo-ruleForm"
                    :size="formSize" status-icon>
                    <el-form-item label="新数据集名称" prop="updName">
                        <el-input v-model="updName" placeholder="请输入数据集名称" clearable />
                    </el-form-item>
                </el-form>
                <template slot="footer">
                    <div class="dialog-footer">
                        <el-button @click="dialogEditVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitEditForm">提交</el-button>
                    </div>
                </template>
            </el-dialog>
            <!-- 新增数据集对话框 -->
            <el-dialog :visible.sync="dialogVisible" title="新增数据集" width="500" :before-close="handleClose">
                <el-form :model="ruleForm" :rules="rules" label-width="auto" class="demo-ruleForm" :size="formSize"
                    status-icon>
                    <!-- 数据集名称 -->
                    <el-form-item label="数据集名称" prop="datasetName">
                        <el-input v-model="ruleForm.datasetName" placeholder="请输入数据集名称" />
                    </el-form-item>
                    <!-- 数据集 URL 和文件选择按钮 -->
                    <el-form-item label="数据集 URL" prop="datasetUrl">
                        <el-input v-model="ruleForm.datasetUrl" autosize type="textarea" placeholder="请输入数据集 URL" />
                        <br />
                        <el-upload ref="uploadRef" class="upload-demo" action="http://127.0.0.1:8080/upload1"
                            :auto-upload="false" :on-change="handleFileChange" :before-upload="beforeUpload">
                            <el-button slot="trigger" type="primary" style="margin-top:5px">选择文件</el-button>
                        </el-upload>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="resetForm(ruleForm)">重置</el-button>
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitForm(ruleForm)">提交</el-button>
                    </div>
                </template>
            </el-dialog>

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
                dataset: '',
                user: '',
                date: '',
                datestart: '',
                dateend: ''
            },
            ruleForm: {
                datasetName: '',
                datasetUrl: '',
                userId: ''
            },
            rules: {
                datasetName: [{ required: true, message: '请输入数据集名称', trigger: 'blur' }],
                datasetUrl: [{ required: true, message: '请上传数据集文件', trigger: 'blur' }],
                updName: [
                    { required: true, message: '请输入新的数据集名称', trigger: 'blur' },
                ],
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
            uploadRef: null,
            dialogEditVisible: false,
            updId: '',
            updName: '',
        };
    },
    computed: {
        filterTableData() {
            return this.tableData.filter(data => !this.search || data.datasetName.toLowerCase().includes(this.search.toLowerCase()));
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
        logout() {
            this.$confirm('即将注销，请确认', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$message({
                    type: 'success',
                    message: '已注销!'
                });
                window.sessionStorage.clear();
                //this.$message.success('已注销');
                this.$router.push("/");
            }).catch(() => {
            });
        },
        roleManageBind() {
            let str = "角色管理";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        userManageBind() {
            let str = "用户管理";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        OpManageBind() {
            let str = "操作管理";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        userSearchBind() {
            let str = "用户查询";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        useraddBind() {
            let str = "用户新增";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        userEditBind() {
            let str = "用户修改";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        userDeleteBind() {
            let str = "用户删除";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        userRoleEditBind() {
            let str = "用户角色修改";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        roleSearchBind() {
            let str = "角色查询";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        roleEditBind() {
            let str = "角色修改";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        rolePowerEditBind() {
            let str = "角色权限修改";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        roleDeleteBind() {
            let str = "角色删除";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
        },
        roleAddBind() {
            let str = "添加角色";
            let id = '';
            this.operationList.forEach(value => {
                if (value.powerName === str)
                    id = false;
            });
            if (id === '')
                id = true;
            return id;
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

        /* resetForm(formEl) {
            if (!formEl)
                return;
            if (this.uploadRef) {
                this.uploadRef.clearFiles();
            }
            formEl.resetFields();
        }, */
        async handleFileChange(file) {
            console.log('文件已选择:', file);
            try {
                const formData = new FormData();
                formData.append('dataset', file.raw);
                const response = await axios.post('http://127.0.0.1:8080/upload1', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });
                this.ruleForm.datasetUrl = response.data.data;
                this.$message({
                    type: 'success',
                    message: '文件上传成功'
                });
                //ElMessage.success('文件上传成功');
            }
            catch (error) {
                console.error('文件上传失败:', error);
                this.$message({
                    type: 'error',
                    message: '文件上传失败，请重试'
                });
                //ElMessage.error('文件上传失败，请重试');
            }
        },
        beforeUpload() {
            return true;
        },
        async handleEdit(index, row) {
            console.log('编辑行:', index, row);
            this.dialogEditVisible = true;
            this.updName = row.datasetName;
            this.updId = row.datasetId;
        },
        submitEditForm() {
            this.$refs.editForm.validate(async (valid) => {
                if (!valid) return;

                if (!this.updName.trim()) {
                    this.$message.error('数据集名称不能为空');
                    return;
                }

                try {
                    const response = await axios.post('http://127.0.0.1:8080/updDataset', {
                        datasetId: this.updId,
                        datasetName: this.updName,
                    });

                    if (response.data.code === 1) {
                        this.$message.success('更新成功');
                        this.dialogEditVisible = false;

                        // 更新本地数据
                        const index = this.tableData.findIndex(item => item.datasetId === this.updId);
                        if (index !== -1) {
                            this.tableData[index].datasetName = this.updName;
                            // 触发视图更新
                            this.$set(this.tableData, index, this.tableData[index]);
                        }
                    } else {
                        this.$message.error('更新失败');
                    }
                } catch (error) {
                    console.error('更新数据集时出错:', error);
                    this.$message.error('更新失败');
                }
            });
        },
        handleDelete(index, row) {
            this.$confirm('确定要删除该数据集吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(async (confirmResult) => {
                if (confirmResult === 'confirm') {
                    try {
                        const response = await axios.delete(`http://127.0.0.1:8080/deleteById1/${row.datasetId}`);
                        if (response.data.code === 1) {
                            this.$message({
                                type: 'success',
                                message: '删除成功'
                            });

                            // 更新表格数据
                            const newTableData = this.tableData.filter(data => data.datasetId !== row.datasetId);
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
                const response = await axios.post('http://127.0.0.1:8080/selectDataset', {
                    datasetName: this.formInline.dataset,
                    uploadUserId: this.formInline.user,
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
        handleClose(done) {
            // 调用 MessageBox.confirm 方法弹出确认框
            this.$confirm('确定关闭对话框吗？')
                .then(() => {
                    // 调用 resetForm 方法重置表单
                    this.resetForm(this.ruleForm);
                    // 调用 done 函数
                    done();
                })
                .catch(() => {
                    // 捕获取消操作，这里不做处理
                });
        },
        resetForm(formRef) {
            // 这里实现重置表单的逻辑
            if (formRef) {
                this.ruleForm.datasetName = '';
                this.ruleForm.datasetUrl = '';
                //formRef.resetFields();
            }
        },
        /* handleClose(done) {
            ElMessageBox.confirm('确定关闭对话框吗？')
                .then(() => {
                    this.resetForm(this.ruleFormRef);
                    done();
                })
                .catch(() => { });
        } */
    },
    mounted() {
        this.username = sessionStorage.getItem('name');
        this.ruleForm.userId = sessionStorage.getItem('id');
        this.userId = sessionStorage.getItem('id');
        this.userimage = sessionStorage.getItem('image');
        axios.get('http://127.0.0.1:8080/dataSet')
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
