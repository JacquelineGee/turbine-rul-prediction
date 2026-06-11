<script lang="ts" setup>
import { ref, onMounted, computed,reactive } from 'vue';
import axios from '@/utils/axios'
import NavMenu from '../component/NavMenu.vue';
import BottomLine from '../component/BottomLine.vue';
import { ElMessageBox, ElMessage, ComponentSize } from 'element-plus';
import { useRouter } from 'vue-router';
interface Prediction {
    predictId: number;
    userId: string;
    predictTime: string;
    predictModel: string;
}

// 定义预测数据列表
const predictions = ref<Prediction[]>([]);

import { useUser } from '@/store/user';
const userS = useUser();
const user_info = reactive({ ...userS.user_info });
const update_info = reactive({ ...userS.user_info });


// 页面加载时获取数据
const fetchPredictions = async () => {
    try {
        const response = await axios.post('http://127.0.0.1:8080/predictionByUserId/'+user_info.userId);
        predictions.value = response.data.data;
    } catch (error) {
        console.error('获取预测数据失败:', error);
    }
};
const formatUpdateTime = (row: Prediction, column: any, cellValue: string) => {
    return cellValue.replace('T', ' ').split('+')[0];
};
const handleSizeChange = (val: number) => {
    console.log(`${val} items per page`)
}
const handleCurrentChange = (val: number) => {
    console.log(`current page: ${val}`)
}
const handleDelete = async (index: number, row: Prediction) => {
    try {
        const confirmResult = await ElMessageBox.confirm('确定要删除该预测记录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        if (confirmResult === 'confirm') {
            const response = await axios.delete(`http://127.0.0.1:8080/deleteById2/${row.predictId}`);
            if (response.data.code === 1) {
                ElMessage.success('删除成功');
                // 从 tableData 中移除已删除的数据
                const newTableData = predictions.value.filter(data => data.predictId !== row.predictId);
                predictions.value = newTableData;
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
const search = ref('');
const filterTableData = computed(() =>
  predictions.value.filter(
    (data) =>
      !search.value ||
      // 将 data.predictId 转换为字符串类型，然后调用 toLowerCase 方法
      String(data.predictId).toLowerCase().includes(search.value.toLowerCase())
  )
);
const router = useRouter();
const currentPage = ref(1)
const pageSize = ref(10)
const size = ref<ComponentSize>('default')
const background = ref(false)
const disabled = ref(false)
const handleCheck = (predictId: number) => {
    router.push({ name: 'PredictionDetail', params: { id: predictId } });
};
// 在组件挂载时调用
onMounted(fetchPredictions);
</script>

<template>
    <div>
        <NavMenu />
        <el-container>
            <el-main>
                <el-table :data="filterTableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)
                    " style="width: 100%">
                    <el-table-column prop="predictId" label="预测编号" width="200" />
                    <el-table-column prop="userName" label="预测用户" width="200" />
                    <el-table-column prop="modelName" label="使用模型" width="200" />
                    <el-table-column prop="datasetName" label="使用数据集" width="200" />
                    <el-table-column prop="predictTime" label="预测时间" width="200" :formatter="formatUpdateTime" />
                    <el-table-column align="right">
                        <template #header>
                            <el-input v-model="search" size="small" placeholder="Type to search" />
                        </template>
                        <template #default="scope">
                            <el-button size="small" type="primary" @click="handleCheck(scope.row.predictId)">
                                查看详情
                            </el-button>
                            <el-button size="small" type="danger" 
                                @click="handleDelete(scope.$index, scope.row)">
                                删除
                        </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="demo-pagination-block">
                    <div class="demonstration"></div>
                    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                        :page-sizes="[1, 5, 10, 20]" :size="size" :disabled="disabled" :background="background"
                        layout="total, sizes, prev, pager, next, jumper" :total="filterTableData.length"
                        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                </div>
            </el-main>
        </el-container>
        <BottomLine/>
    </div>
</template>