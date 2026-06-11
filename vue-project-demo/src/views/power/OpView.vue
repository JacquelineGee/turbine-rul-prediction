<template>
    <div>
        <HeaderView></HeaderView>
        <div style="display: flex;">
            <el-container style="border: 1px solid #eee">

                <el-container>
                    <AsideView></AsideView>

                    <el-main>
                        <el-dialog title="编辑角色信息" :visible.sync="editDialogVisible" width="30%"
                            :before-close="handleCloseEditDialog">

                            <el-form :model="editPowerForm" label-width="80px">

                                <el-form-item label="权限名称">
                                    <el-input v-model="editPowerForm.powerName"></el-input>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCloseEditDialog">取消</el-button>
                                <el-button type="primary" @click="handleEditSubmit">提交</el-button>
                            </span>
                        </el-dialog>
                        <el-row :gutter="20">
                            <el-col :span="6">
                                <!--面包屑  -->
                                <el-breadcrumb separator-class="el-icon-arrow-right">
                                    <el-breadcrumb-item @click="clearNavState"
                                        :to="{ path: '/home' }">首页</el-breadcrumb-item>
                                    <el-breadcrumb-item>操作管理</el-breadcrumb-item>
                                </el-breadcrumb>
                                <br>
                                <template>
                                    <el-tree :data="permissionTreeData" show-checkbox node-key="powerId"
                                        :default-expanded-keys="defaultExpandedKeys"
                                        :default-checked-keys="defaultCheckedKeys" :props="treeProps" ref="tree"
                                        @check-change="handleCheckChange">
                                    </el-tree>
                                    <br>
                                    <el-button type="primary" @click="updateRolePermissions" plain>更新</el-button>
                                </template>
                            </el-col>
                            <el-col :span="18"><template>
                                    <el-breadcrumb separator-class="el-icon-arrow-right">

                                        <el-breadcrumb-item>权限管理</el-breadcrumb-item>
                                    </el-breadcrumb>
                                    <br>
                                    <template>
                                        <div>
                                            <!-- 表格 -->
                                            <el-table :data="allPermissions" stripe border :cell-style="cellStyle"
                                                :row-style="rowStyle">
                                                <!-- 权限编号 -->
                                                <el-table-column prop="powerId" label="权限编号"
                                                    width="100"></el-table-column>
                                                <!-- 权限名称 -->
                                                <el-table-column prop="powerName" label="权限名称"
                                                    width="120"></el-table-column>
                                                <!-- 父权限编号 -->
                                                <el-table-column prop="faPowerId" label="父权限编号" width="100">
                                                    <template slot-scope="scope">
                                                        {{ scope.row.faPowerId ? scope.row.faPowerId : '无父权限' }}
                                                    </template>
                                                </el-table-column>
                                                <!-- 更新时间 -->
                                                <el-table-column prop="updtime" label="更新时间" width="180">
                                                    <template slot-scope="scope">
                                                        {{ scope.row.updtime.substring(0, 19).replace("T", " ") }}
                                                    </template>
                                                </el-table-column>
                                                <!-- 操作 -->
                                                <el-table-column label="操作">
                                                    <template slot-scope="scope">
                                                        <el-button type="primary" size="mini"
                                                            @click="handleEdit(scope.row)"
                                                            :disabled="roleEditBind()">编辑</el-button>
                                                        <el-button type="danger" size="mini"
                                                            @click="deletePower(scope.row)"
                                                            :disabled="roleDeleteBind()">删除</el-button>
                                                    </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>
                                    </template>

                                </template></el-col>
                        </el-row>

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
            userimage: '',
            userId: '',
            /*  defaultExpandedKeys: [], */
            operationList: [],
            searchId: 0,
            roleAuthDialogVisible: true,
            defaultCheckedKeys: [],
            permissionTreeData: [],
            treeProps: {
                label: 'powerName',
                children: 'children',
                key: 'powerId'
            },
            allPermissions: [],
            multipleSelection: [],
            editDialogVisible: false,
            editPowerForm: []
        }
    },
    created() {
        //this.getUserList()
        this.userId = sessionStorage.getItem('id');
        this.getOperationList()
    },
    methods: {
        handleEditSubmit() {
            axios
                .post("/updPower", this.editPowerForm)
                .then((response) => {
                    if (response.data.code) {
                        // 请求成功后的处理，比如提示更新成功，关闭弹框，重新获取用户列表等
                        this.$message.success("权限信息更新成功");
                        this.editDialogVisible = false;
                        this.defaultCheckedKeys = [];
                        this.permissionTreeData = [];
                        // 这里可以利用response.data.updatedUser 做一些其他事情，比如更新本地缓存的当前用户信息等
                        this.openRoleAuthDialog()
                    } else {
                        this.$message.error("用户信息更新失败，业务逻辑层面验证未通过");
                    }
                })
                .catch((error) => {
                    // 请求失败后的处理，提示错误信息
                    this.$message.error("用户信息更新失败：" + error.message);
                });
        },
        handleCloseEditDialog() {
            this.editDialogVisible = false;
        },
        handleEdit(row) {
            axios
                .post(`powerById/${row.powerId}`)
                .then((response) => {
                    this.editPowerForm = response.data.data;
                    this.editDialogVisible = true;
                    //alert(response.data.userName);
                })
                .catch((error) => {
                    console.error("获取用户详细信息失败：", error);
                    // 可以在这里添加一些提示信息给用户，比如弹出提示框告知获取信息失败等
                });


        },
        deletePower(row) {
            this.$confirm('即将删除，请确认', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.post(`/deletePower/${row.powerId}`)
                    .then((response) => {
                        if (response.data.code) {
                            this.$message.success("删除成功");
                            this.defaultCheckedKeys = [];
                            this.permissionTreeData = [];
                            this.openRoleAuthDialog();
                            //this.tableData = response.data.data;
                        } else {
                            this.$message.error("删除失败，业务逻辑层面验证未通过");
                        }
                    })
                    .catch((error) => {
                        this.$message.error("删除失败：请勾选要删除的对象，" + error.message);
                    });
            })
            //alert(JSON.stringify(idsToDelete));
            //alert('已批量删除！');
            /* setTimeout(function () {
                location.reload();
            }, 500); */
            //location.reload();
        },
        rowStyle({ rowIndex }) {
            return {
                backgroundColor: rowIndex % 2 === 0 ? "#eff5f7" : "rgb(142, 175, 233)", // 自定义斑马线颜色
            };
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        resetTree() {
            // 重置树组件
            this.defaultCheckedKeys = [];
            this.permissionTreeData = [];
        },
        async openRoleAuthDialog() {
            this.curRoleId = 112;
            this.curRoleName = '操作管理员';
            this.resetTree;
            this.roleAuthDialogVisible = true;

            try {
                await this.fetchAllPermissions();
                await this.fetchRolePermissions(this.curRoleId);
            } catch (error) {
                console.error('加载权限数据失败:', error);
            }
        },

        async fetchAllPermissions() {
            try {
                const response = await axios.get('/allPower');
                this.allPermissions = response.data.data;
                this.permissionTreeData = this.buildPermissionTree(this.allPermissions);
                this.defaultExpandedKeys = this.permissionTreeData.map(item => item.powerId).slice(0, 2);
            } catch (error) {
                console.error('获取所有权限失败:', error);
            }
        },

        async fetchRolePermissions(roleId) {
            try {
                const response = await axios.post(`/power/${roleId}`);
                const rolePermissions = response.data.data;
                const rolePermissionIds = rolePermissions.map(permission => permission.powerId);
                this.defaultCheckedKeys = rolePermissionIds;
            } catch (error) {
                console.error('获取当前角色权限失败:', error);
            }
        },

        buildPermissionTree(permissions) {
            const map = new Map();
            permissions.forEach(permission => {
                map.set(permission.powerId, { ...permission, children: [] });
            });
            permissions.forEach(permission => {
                const parent = map.get(permission.faPowerId);
                if (parent) {
                    parent.children.push(map.get(permission.powerId));
                } else {
                    this.permissionTreeData.push(map.get(permission.powerId));
                }
            });
            return this.permissionTreeData;
        },

        updateRolePermissions() {
            let that = this
            let authList = [];
            if (that.$refs.tree.getCheckedNodes().length === 0) {
                authList.push(0)
            }
            else {
                that.$refs.tree.getCheckedNodes().forEach(value => {
                    authList.push(value.powerId)
                })
            }
            //alert(authList);
            axios.post(`/updateRolePower/${this.curRoleId}`, {
                powers: authList
            })
                .then(response => {
                    if (response.data.code) {
                        this.$message.success('操作更新成功');
                        this.roleAuthDialogVisible = false;
                        axios.get("http://localhost:8080/roles").then((result) => {
                            this.tableData = result.data.data;
                            this.sum = this.tableData.length;
                        });
                        this.getOperationList();
                    }
                })
                .catch(error => {
                    console.error('更新操作失败:', error);
                    this.$message.error('操作更新失败');
                });
        },
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
        roleManageBind() {
            let str = "角色管理"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        userManageBind() {
            let str = "用户管理"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        OpManageBind() {
            let str = "操作管理"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        roleSearchBind() {
            let str = "角色查询"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) {
                    id = false;
                    this.searchId = 1;
                }
            })
            if (id === '') id = true
            return id
        },
        roleEditBind() {
            let str = "角色修改"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
                //alert(value.powerName)}
            })
            if (id === '') id = true
            return id
        },
        rolePowerEditBind() {
            let str = "角色权限修改"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true;
            return id
        },
        roleDeleteBind() {
            let str = "角色删除"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true;
            return id
        },
        roleAddBind() {
            let str = "添加角色"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true;
            return id
        },
        userSearchBind() {
            let str = "用户查询"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        useraddBind() {
            let str = "用户新增"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        userEditBind() {
            let str = "用户修改"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        userDeleteBind() {
            let str = "用户删除"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        userRoleEditBind() {
            let str = "用户角色修改"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
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
        this.userId = sessionStorage.getItem('id');
        this.openRoleAuthDialog();
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