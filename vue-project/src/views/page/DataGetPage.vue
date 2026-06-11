<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue';
import type { ComponentSize, FormInstance, FormRules, UploadInstance, UploadFile } from 'element-plus';
import { ElMessageBox, ElMessage } from 'element-plus';
import axios from '@/utils/axios';
import NavMenu from '../component/NavMenu.vue';
import BottomLine from '../component/BottomLine.vue';

// 表单数据类型
interface RuleForm {
    datasetName: string;
    datasetUrl: string;
    userId: string;
    updName?: string;
}

// 表格数据类型
interface DataSet {
    updateTime: string;
    datasetName: string;
    datasetId: string;
    datasetUrl: string;
    uploadUserId: string;
    userName?: string;
}

// 表单大小
const formSize = ref<ComponentSize>('default');

// 表单引用
const ruleFormRef = ref<FormInstance>();
const editFormRef = ref<FormInstance>(); // 编辑表单引用

// 表单数据
const ruleForm = reactive<RuleForm>({
    datasetName: '',
    datasetUrl: '',
    userId: ''
});

// 表单验证规则
const rules = reactive<FormRules<RuleForm>>({
    datasetName: [
        { required: true, message: '请输入数据集名称', trigger: 'blur' },
    ],
    datasetUrl: [
        { required: true, message: '请上传数据集文件', trigger: 'blur' },
    ],
    updName: [
        { required: true, message: '请输入新的数据集名称', trigger: 'blur' },
    ],
});

// 提交表单
const submitForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    try {
        await formEl.validate((valid) => {
            if (valid) {
                console.log('表单提交成功:', ruleForm);
                const dataset = {
                    datasetName: ruleForm.datasetName,
                    datasetUrl: ruleForm.datasetUrl,
                    uploadUserId: ruleForm.userId
                };
                axios.post('http://127.0.0.1:8080/addDataset', dataset)
                    .then(async (response) => {
                        if (response.data.code === 1) {
                            ElMessage.success('提交成功！');
                            dialogVisible.value = false;
                            const response = await axios.get('http://127.0.0.1:8080/dataSet');
                            tableData.value = response.data.data;
                            resetForm(ruleFormRef.value);
                        } else {
                            ElMessage.error('提交失败：' + response.data.message);
                        }
                    })
                    .catch((error) => {
                        console.error('提交失败:', error);
                        ElMessage.error('提交失败，请重试！');
                    });
            } else {
                console.log('表单验证失败');
                ElMessage.error('请填写完整信息！');
            }
        });
    } catch (error) {
        console.error('提交失败:', error);
        ElMessage.error('提交失败，请重试！');
    }
};

// 重置表单
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    if (uploadRef.value) {
        uploadRef.value.clearFiles();
    }
    formEl.resetFields();
};

// 文件上传逻辑
const uploadRef = ref<UploadInstance>();
const handleFileChange = async (file: UploadFile) => {
    console.log('文件已选择:', file);
    try {
        const formData = new FormData();
        formData.append('dataset', file.raw!);
        const response = await axios.post('http://127.0.0.1:8080/upload1', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        ruleForm.datasetUrl = response.data.data;
        ElMessage.success('文件上传成功');
    } catch (error) {
        console.error('文件上传失败:', error);
        ElMessage.error('文件上传失败，请重试');
    }
};

const beforeUpload = (file: UploadFile) => {
    return true;
};

// 表格数据
const tableData = ref<DataSet[]>([]);
const stored_user = localStorage.getItem("user_information");
const user_info = JSON.parse(stored_user || '{}');

// 页面加载时获取数据集数据
onMounted(async () => {
    ruleForm.userId = user_info.userId;
    try {
        const response = await axios.get('http://127.0.0.1:8080/dataSet');
        if (response.data.code === 1) {
            tableData.value = response.data.data;
            ElMessage.success('数据集加载成功');
        } else {
            ElMessage.error('数据集加载失败');
        }
    } catch (error) {
        console.error('数据集加载失败:', error);
        ElMessage.error('数据集加载失败，请重试');
    }
});

// 表格搜索功能
const search = ref('');
const filterTableData = computed(() =>
    tableData.value.filter(
        (data) =>
            !search.value ||
            data.datasetName.toLowerCase().includes(search.value.toLowerCase())
    )
);

const dialogEditVisible = ref(false);
const updName = ref('');
const updId = ref('');

// 编辑操作
const handleEdit = async (index: number, row: DataSet) => {
    console.log('编辑行:', index, row);
    updName.value = row.datasetName;
    updId.value = row.datasetId;
    dialogEditVisible.value = true;
};

// 提交编辑表单
const submitEditForm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return;
    try {
        await formEl.validate((valid) => {
            if (valid) {
                if (!updName.value.trim()) {
                    ElMessage.error('数据集名称不能为空');
                    return;
                }
                
                axios.post(`http://127.0.0.1:8080/updDataset`, {
                    datasetId: updId.value,
                    datasetName: updName.value,
                }).then(response => {
                    if (response.data.code === 1) {
                        ElMessage.success('更新成功');
                        dialogEditVisible.value = false;
                        // 更新本地数据
                        const index = tableData.value.findIndex(item => item.datasetId === updId.value);
                        if (index !== -1) {
                            tableData.value[index].datasetName = updName.value;
                        }
                    } else {
                        ElMessage.error('更新失败：' + response.data.message);
                    }
                }).catch(error => {
                    console.error('更新失败:', error);
                    ElMessage.error('更新失败，请重试');
                });
            } else {
                ElMessage.error('请填写完整信息');
            }
        });
    } catch (error) {
        if (error instanceof Error && error.message !== 'cancel') {
            console.error('更新失败:', error);
            ElMessage.error('更新失败，请重试');
        }
    }
}

const handleDelete = async (index: number, row: DataSet) => {
    try {
        const confirmResult = await ElMessageBox.confirm('确定要删除该数据集吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        if (confirmResult === 'confirm') {
            const response = await axios.delete(`http://127.0.0.1:8080/deleteById1/${row.datasetId}`);
            if (response.data.code === 1) {
                ElMessage.success('删除成功');
                tableData.value = tableData.value.filter(data => data.datasetId !== row.datasetId);
            } else {
                ElMessage.error('删除失败：' + response.data.message);
            }
        }
    } catch (error) {
        if (error instanceof Error && error.message !== 'cancel') {
            console.error('删除失败:', error);
            ElMessage.error('删除失败，请重试');
        }
    }
};

const onSubmit = async () => {
    if (formInline.date !== '') {
        let dates = formInline.date[0];
        let datee = formInline.date[1];
        let date = new Date(dates);
        let formattedDate = date.getFullYear() + '-' +
            ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
            ('0' + date.getDate()).slice(-2);
        formInline.datestart = formattedDate;
        date = new Date(datee);
        formattedDate = date.getFullYear() + '-' +
            ('0' + (date.getMonth() + 1)).slice(-2) + '-' +
            ('0' + date.getDate()).slice(-2);
        formInline.dateend = formattedDate;
    }

    try {
        const response = await axios.post('http://127.0.0.1:8080/selectDataset', {
            datasetName: formInline.dataset,
            uploadUserId: formInline.user,
            updateTimeStart: formInline.datestart,
            updateTimeEnd: formInline.dateend
        });

        if (response.data.msg === "success") {
            ElMessage.success('查询成功');
            tableData.value = response.data.data;
        }
        else {
            ElMessage.error('查询失败');
        }
    } catch (error) {
        console.error('查询失败:', error);
        ElMessage.error('查询失败');
    }
};

// 对话框逻辑
const dialogVisible = ref(false);
const handleClose = (done: () => void) => {
    ElMessageBox.confirm('确定关闭对话框吗？')
        .then(() => {
            resetForm(ruleFormRef.value);
            done();
        })
        .catch(() => {
            // 取消关闭
        });
};

const formInline = reactive({
    dataset: '',
    user: '',
    date: '',
    datestart: '',
    dateend: ''
});

const formatUpdateTime = (row: DataSet, column: any, cellValue: string) => {
    return cellValue.replace('T', ' ').split('+')[0];
};

// 分页
const currentPage = ref(1)
const pageSize = ref(5)
const size = ref<ComponentSize>('default')
const background = ref(false)
const disabled = ref(false)

const handleSizeChange = (val: number) => {
    console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
    console.log(`current page: ${val}`)
}
</script>

<template>
    <div>
        <NavMenu />
        <el-container>
            <el-main>
                <!-- 搜索表单 -->
                <el-form :inline="true" :model="formInline" class="demo-form-inline">
                    <el-form-item label="数据集名">
                        <el-input v-model="formInline.dataset" placeholder="数据集名" style="width:170px" clearable />
                    </el-form-item>
                    <el-form-item label="上传用户">
                        <el-input v-model="formInline.user" placeholder="上传用户id" style="width:170px" clearable />
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
                            <el-link :href="scope.row.datasetUrl" target="_blank" :underline="false">
                                {{ scope.row.datasetUrl }}
                            </el-link>
                        </template>
                    </el-table-column>
                    <el-table-column align="right">
                        <template #header>
                            <el-input v-model="search" size="small" placeholder="Type to search" />
                        </template>
                        <template #default="scope">
                            <el-button size="small" v-if="scope.row.uploadUserId === user_info.userId"
                                @click="handleEdit(scope.$index, scope.row)">
                                编辑
                            </el-button>
                            <el-button size="small" v-else disabled>
                                编辑
                            </el-button>
                            <el-button size="small" type="danger" v-if="scope.row.uploadUserId === user_info.userId"
                                @click="handleDelete(scope.$index, scope.row)">
                                删除
                            </el-button>
                            <el-button size="small" type="danger" v-else disabled>
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页 -->
                <div class="demo-pagination-block">
                    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                        :page-sizes="[1, 5, 10, 20]" :size="size" :disabled="disabled" :background="background"
                        layout="total, sizes, prev, pager, next, jumper" :total="filterTableData.length"
                        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </el-main>

            <!-- 编辑对话框 -->
            <el-dialog v-model="dialogEditVisible" title="修改数据集名称" width="500" :before-close="handleClose">
                <el-form ref="editFormRef" :model="{ updName }" :rules="rules" label-width="auto" class="demo-ruleForm"
                    :size="formSize" status-icon>
                    <el-form-item label="新数据集名称" prop="updName">
                        <el-input v-model="updName" placeholder="请输入数据集名称" clearable />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="dialogEditVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitEditForm(editFormRef)">提交</el-button>
                    </div>
                </template>
            </el-dialog>

            <!-- 新增数据集对话框 -->
            <el-dialog v-model="dialogVisible" title="新增数据集" width="500" :before-close="handleClose">
                <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="auto" class="demo-ruleForm"
                    :size="formSize" status-icon>
                    <el-form-item label="数据集名称" prop="datasetName">
                        <el-input v-model="ruleForm.datasetName" placeholder="请输入数据集名称" />
                    </el-form-item>
                    <el-form-item label="数据集 URL" prop="datasetUrl">
                        <el-input v-model="ruleForm.datasetUrl" autosize type="textarea" placeholder="请输入数据集 URL" />
                        <br>
                        <el-upload ref="uploadRef" class="upload-demo" action="http://127.0.0.1:8080/upload1"
                            :auto-upload="false" :on-change="handleFileChange" :before-upload="beforeUpload">
                            <template #trigger>
                                <el-button type="primary" style="margin-top:5px">选择文件</el-button>
                            </template>
                        </el-upload>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitForm(ruleFormRef)">提交</el-button>
                    </div>
                </template>
            </el-dialog>
        </el-container>
        <BottomLine />
    </div>
</template>

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