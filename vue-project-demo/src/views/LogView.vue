<template>
    <div>
      <HeaderView></HeaderView>
      <el-container>
        <el-container>
          <AsideView></AsideView>
          <el-main>
            <!-- 搜索表单 -->
            <el-form :inline="true" :model="formInline" class="demo-form-inline">
              <el-form-item label="日志ID">
                <el-input v-model="formInline.id" placeholder="日志ID" style="width:150px" clearable />
              </el-form-item>
              <el-form-item label="用户ID">
                <el-input v-model="formInline.userId" placeholder="用户ID" style="width:150px" clearable />
              </el-form-item>
              <el-form-item label="用户名">
                <el-input v-model="formInline.username" placeholder="用户名" style="width:150px" clearable />
              </el-form-item>
              <el-form-item label="操作类型">
                <el-input v-model="formInline.operation" placeholder="操作类型" style="width:150px" clearable />
              </el-form-item>
              <el-form-item label="操作状态">
                <el-select v-model="formInline.status" placeholder="操作状态" clearable>
                  <el-option label="成功" value="1"></el-option>
                  <el-option label="失败" value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="操作模块">
                <el-input v-model="formInline.module" placeholder="操作模块" style="width:150px" clearable />
              </el-form-item>
              <el-form-item label="操作日期">
                <el-date-picker
                  v-model="formInline.date"
                  type="daterange"
                  unlink-panels
                  range-separator="到"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  clearable>
                </el-date-picker>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="onSubmit">查询</el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
            
            <!-- 数据表格 -->
            <el-table
              :data="filterTableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
              style="width: 100%"
              border
              stripe>
              <el-table-column label="日志ID" prop="id" width="80" align="center"></el-table-column>
              <el-table-column label="用户ID" prop="userId" width="100" align="center"></el-table-column>
              <el-table-column label="用户名" prop="username" width="120" align="center"></el-table-column>
              <el-table-column label="操作类型" prop="operation" width="120" align="center"></el-table-column>
              <el-table-column label="操作模块" prop="module" width="120" align="center"></el-table-column>
              <el-table-column label="请求方法" prop="method" width="180" align="center" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作状态" width="100" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                    {{ scope.row.status === 1 ? '成功' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="执行耗时" width="100" align="center">
                <template slot-scope="scope">
                  {{ scope.row.executionTime }} ms
                </template>
              </el-table-column>
              <el-table-column label="  IP地址" prop="ip" width="150" align="center"></el-table-column>
              <el-table-column label="操作时间" prop="operationTime" width="180" align="center" :formatter="formatDateTime"></el-table-column>
              <el-table-column label="操作" width="180" align="center" fixed="right">
                <template slot-scope="scope">
                  <el-button size="small" type="primary" @click="showDetail(scope.row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
  
            <!-- 分页 -->
            <div class="demo-pagination-block">
              <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="filterTableData.length"
                background>
              </el-pagination>
            </div>
            
            <!-- 日志详情对话框 -->
            <el-dialog
              title="日志详情"
              :visible.sync="detailDialogVisible"
              width="50%">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
                <el-descriptions-item label="用户ID">{{ currentLog.userId }}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
                <el-descriptions-item label="操作类型">{{ currentLog.operation }}</el-descriptions-item>
                <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
                <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
                <el-descriptions-item label="请求参数">
                  <pre style="max-height: 200px; overflow: auto;">{{ formatParams(currentLog.params) }}</pre>
                </el-descriptions-item>
                <el-descriptions-item label="操作状态">
                  <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'">
                    {{ currentLog.status === 1 ? '成功' : '失败' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="错误信息" v-if="currentLog.status === 0">
                  {{ currentLog.errorMsg }}
                </el-descriptions-item>
                <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
                <el-descriptions-item label="操作时间">{{ formatDateTimeFull(currentLog.operationTime) }}</el-descriptions-item>
                <el-descriptions-item label="执行耗时">{{ currentLog.executionTime }} ms</el-descriptions-item>
              </el-descriptions>
              <span slot="footer" class="dialog-footer">
                <el-button @click="detailDialogVisible = false">关闭</el-button>
              </span>
            </el-dialog>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </template>
  
  <script>
  import HeaderView from '@/components/HeaderView.vue';
  import AsideView from '@/components/AsideView.vue';
  import axios from 'axios';
  
  export default {
    components: {
      HeaderView,
      AsideView
    },
    data() {
      return {
        formInline: {
          id: '',
          userId: '',
          username: '',
          operation: '',
          status: '',
          module: '',
          date: [],
          startTime: '',
          endTime: ''
        },
        tableData: [],
        search: '',
        currentPage: 1,
        pageSize: 10,
        detailDialogVisible: false,
        currentLog: {}
      };
    },
    computed: {
      filterTableData() {
        return this.tableData.filter(data => 
          (!this.search || String(data.id).includes(this.search)) &&
          (!this.formInline.id || String(data.id).includes(this.formInline.id)) &&
          (!this.formInline.userId || String(data.userId).includes(this.formInline.userId)) &&
          (!this.formInline.username || (data.username && data.username.includes(this.formInline.username))) &&
          (!this.formInline.operation || (data.operation && data.operation.includes(this.formInline.operation))) &&
          (!this.formInline.status || String(data.status) === this.formInline.status) &&
          (!this.formInline.module || (data.module && data.module.includes(this.formInline.module)))
        );
      }
    },
    methods: {
      // 加载日志数据
      loadLogData() {
        const params = {
          id: this.formInline.id,
          userId: this.formInline.userId,
          username: this.formInline.username,
          operation: this.formInline.operation,
          status: this.formInline.status,
          module: this.formInline.module,
          startTime: this.formInline.startTime,
          endTime: this.formInline.endTime
        };
        const payload =JSON.stringify(params)
        axios.post('/api/system/log/list', { payload })
          .then(response => {
            if (response.data.code === 1 ) {
              this.tableData = response.data.data;
            } else {
              this.$message.error(response.data.msg || '获取日志数据失败');
            }
          })
          .catch(error => {
            console.error('获取日志数据失败:', error);
            this.$message.error('获取日志数据失败');
          });
      },
      
      // 查询提交
      onSubmit() {
        if (this.formInline.date && this.formInline.date.length === 2) {
          this.formInline.startTime = this.formatDate(this.formInline.date[0]);
          this.formInline.endTime = this.formatDate(this.formInline.date[1]);
        } else {
          this.formInline.startTime = '';
          this.formInline.endTime = '';
        }
        
        this.currentPage = 1;
        this.loadLogData();
      },
      
      // 重置表单
      resetForm() {
        this.formInline = {
          id: '',
          userId: '',
          username: '',
          operation: '',
          status: '',
          module: '',
          date: [],
          startTime: '',
          endTime: ''
        };
        this.loadLogData();
      },
      
      // 显示日志详情
      showDetail(row) {
        this.currentLog = row;
        this.detailDialogVisible = true;
      },
      
      // 删除日志
      handleDelete(row) {
        this.$confirm(`确定要删除日志ID为 ${row.id} 的记录吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          axios.delete(`/api/sys/log/${row.id}`)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('删除成功');
                this.loadLogData();
              } else {
                this.$message.error(response.data.msg || '删除失败');
              }
            })
            .catch(error => {
              console.error('删除日志失败:', error);
              this.$message.error('删除失败');
            });
        }).catch(() => {
          // 用户取消
        });
      },
      
      // 格式化日期时间显示
      formatDateTime(row, column, cellValue) {
        if (!cellValue) return '';
        return cellValue.replace('T', ' ').substring(0, 19);
      },
      
      // 格式化完整日期时间
      formatDateTimeFull(dateTime) {
        if (!dateTime) return '';
        return dateTime.replace('T', ' ').substring(0, 19);
      },
      
      // 格式化日期为字符串
      formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        return d.getFullYear() + '-' + 
               (d.getMonth() + 1).toString().padStart(2, '0') + '-' + 
               d.getDate().toString().padStart(2, '0');
      },
      
      // 格式化请求参数
      formatParams(params) {
        try {
          if (typeof params === 'string') {
            return JSON.stringify(JSON.parse(params), null, 2);
          }
          return JSON.stringify(params, null, 2);
        } catch (e) {
          return params;
        }
      },
      
      // 分页大小改变
      handleSizeChange(val) {
        this.pageSize = val;
      },
      
      // 当前页改变
      handleCurrentChange(val) {
        this.currentPage = val;
      }
    },
    mounted() {
      this.loadLogData();
    }
  };
  </script>
  
  <style scoped>
  .demo-form-inline .el-input {
    --el-input-width: 150px;
  }
  
  .demo-form-inline .el-select {
    --el-select-width: 150px;
  }
  
  .demo-pagination-block {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  
  .el-table {
    margin-top: 20px;
  }
  
  .el-descriptions {
    margin-top: 20px;
  }
  
  pre {
    margin: 0;
    white-space: pre-wrap;
    word-wrap: break-word;
  }
  </style>