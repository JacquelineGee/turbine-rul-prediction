<template>
    <div>
        <HeaderView></HeaderView>
        <div style="display: flex;">
            <el-container style="border: 1px solid #eee">
                <el-container>
                    <AsideView></AsideView>

                    <el-main>
                        <!-- 面包屑 -->
                        <el-breadcrumb separator-class="el-icon-arrow-right">
                            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                            <el-breadcrumb-item>角色管理</el-breadcrumb-item>
                        </el-breadcrumb>
                        <br>
                        <!-- 查询表单 -->
                        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                            <el-form-item label="角色编号">
                                <el-input v-model="searchForm.roleId" placeholder="角色编号"></el-input>
                            </el-form-item>
                            <el-form-item label="角色名">
                                <el-input v-model="searchForm.roleName" placeholder="角色名"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="onSubmit" :disabled="roleSearchBind()"
                                    plain>查询</el-button>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="addRole" :disabled="roleAddBind()"
                                    plain>添加角色</el-button>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="addPower" :disabled="powerAddBind()"
                                    plain>添加权限</el-button>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="danger" @click="onDeletes" :disabled="roleDeleteBind()"
                                    plain>批量删除</el-button>
                            </el-form-item>
                        </el-form>

                        <!-- 角色表格 -->
                        <el-table :data="tableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
                            @selection-change="handleSelectionChange" border :row-style="rowStyle">
                            <el-table-column type="selection" width="55"></el-table-column>
                            <el-table-column prop="roleId" label="角色编号" width="100"></el-table-column>
                            <el-table-column prop="roleName" label="角色名称" width="180"></el-table-column>
                            <el-table-column prop="powers" label="已分配权限" width="180">
                                <template slot-scope="scope">
                                    {{ scope.row.powers ? scope.row.powers : '该角色暂无权限' }}
                                </template>
                            </el-table-column>
                            <el-table-column prop="updtime" label="更新时间" width="180">
                                <template slot-scope="scope">
                                    <!-- 通过substring方法截取合适的时间部分 -->
                                    {{ scope.row.updtime.substring(0, 19).replace("T", " ") }}
                                </template>
                            </el-table-column>
                            <el-table-column label="操作">
                                <template slot-scope="scope">
                                    <el-button type="primary" size="mini" @click="handleEdit(scope.row)"
                                        :disabled="roleEditBind()">编辑</el-button>
                                    <el-button type="danger" size="mini" @click="deleteRole(scope.row)"
                                        :disabled="roleDeleteBind()">删除</el-button>
                                    <el-button type="success" size="mini" @click="openRoleAuthDialog(scope.row)"
                                        :disabled="rolePowerEditBind()">角色权限修改</el-button>
                                </template>
                            </el-table-column>
                        </el-table>

                        <!-- 权限修改对话框 -->
                        <br>
                        <el-dialog :visible.sync="roleAuthDialogVisible" title="角色权限修改" width="30%" @close="resetTree">
                            <div>
                                <h1>{{ this.curRoleName }}</h1>
                            </div>

                            <el-tree :data="permissionTreeData" show-checkbox node-key="powerId"
                                :default-expanded-keys="defaultExpandedKeys" :default-checked-keys="defaultCheckedKeys"
                                :props="treeProps" ref="tree">
                            </el-tree>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="roleAuthDialogVisible = false">取消</el-button>
                                <el-button type="primary" @click="updateRolePermissions">更新</el-button>
                            </span>
                        </el-dialog>
                        <el-dialog title="添加权限" :visible.sync="showAddPowerForm" width="30%"
                            :before-close="handleCloseDialog">

                            <el-form :model="addPowerForm" label-width="80px">

                                <el-form-item label="权限号">
                                    <el-input v-model="addPowerForm.powerId"></el-input>
                                </el-form-item>
                                <el-form-item label="权限名">
                                    <el-input v-model="addPowerForm.powerName"></el-input>
                                </el-form-item>
                                <el-form-item label="父权限">
                                    <el-select v-model="addPowerForm.faPowerId" placeholder="请选择父权限">
                                        <el-option v-for="permission in permissionTreeData" :key="permission.powerId"
                                            :label="permission.powerName" :value="permission.powerId">
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCancel">取消</el-button>
                                <el-button type="primary" @click="handleSubmit">提交</el-button>
                            </span>
                        </el-dialog>
                        <el-dialog title="添加角色" :visible.sync="showAddRoleForm" width="30%"
                            :before-close="handleCloseDialog1">

                            <el-form :model="addPowerForm" label-width="80px">

                                <el-form-item label="角色编号">
                                    <el-input v-model="addRoleForm.roleId"></el-input>
                                </el-form-item>
                                <el-form-item label="角色名">
                                    <el-input v-model="addRoleForm.roleName"></el-input>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCancel1">取消</el-button>
                                <el-button type="primary" @click="handleSubmit1">提交</el-button>
                            </span>
                        </el-dialog>
                        <el-dialog title="编辑角色信息" :visible.sync="editDialogVisible" width="30%"
                            :before-close="handleCloseEditDialog">

                            <el-form :model="editRoleForm" label-width="80px">

                                <el-form-item label="角色名">
                                    <el-input v-model="editRoleForm.roleName"></el-input>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCloseEditDialog">取消</el-button>
                                <el-button type="primary" @click="handleEditSubmit">提交</el-button>
                            </span>
                        </el-dialog>

                        <!-- 分页 -->
                        <div>
                            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                                :current-page="currentPage" :page-sizes="[1, 5, 10, 20]" :page-size="pageSize"
                                layout="total, sizes, prev, pager, next, jumper" :total="tableData.length">
                            </el-pagination>
                        </div>
                    </el-main>
                </el-container>
            </el-container>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import qs from 'qs';

export default {
    data() {
        return {
            editRoleForm: {},
            roleAuthDialogVisible: false,
            permissionTreeData: [],
            defaultExpandedKeys: [],
            defaultCheckedKeys: [],
            treeProps: {
                label: 'powerName',
                children: 'children',
                key: 'powerId'
            },
            curRoleId: '', // 当前角色ID
            curRoleName: "",
            tableData: [],
            currentPage: 1, // 当前页
            pageSize: 10, // 每页条数
            total: 0, // 数据总条数
            searchForm: {
                roleName: "",
                roleId: ""
            },
            addPowerForm: {
                powerId: "",
                powerName: "",
                faPowerId: ""
            },
            addRoleForm: {
                roleName: "",
                roleId: ""
            },
            showAddRoleForm: false,
            sum: '',
            username: '',
            userimage: '',
            userId: '',
            showAddPowerForm: false,
            multipleSelection: [],
            editDialogVisible: false,
            operationList: [],
            verrify: ''
        }
    },
    created() {
        //this.getUserList()
        this.userId = sessionStorage.getItem('id');
        this.getOperationList()
    },
    methods: {
        rowStyle({ rowIndex }) {
            return {
                backgroundColor: rowIndex % 2 === 0 ? "#ffffff" : "#f8fbf7cc", // 自定义斑马线颜色
            };
        },
        async verrifyAuth(operationid) {
            let id = this.userId;
            // 使用qs库将参数对象转换为查询字符串格式
            let queryString = qs.stringify({
                id: id,
                op: operationid
            });
            await axios({
                method: 'post',
                url: `/verify?${queryString}`,
                // 因为参数通过URL传递了，这里data可以不传或者根据实际情况看是否有其他需要在请求体中发送的数据
            }).then((resp) => {
                if (resp.data.code === 1) {
                    this.verrify = true;
                } else {
                    this.$message.error("没有权限进行此操作！");
                    this.verrify = false;
                }
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
        FeatureManageBind() {
            let str = "功能菜单管理"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false
            })
            if (id === '') id = true
            return id
        },
        roleSearchBind() {
            let str = "角色查询"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
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
        powerAddBind() {
            let str = "添加权限"
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
        /*  //按钮启用状态检查
         activeCheck(id) {
             let flag = true
             this.operationList.forEach(value => {
                 if (value.id == id) {
                     if (value.activeState === 1)
                         flag = false
                 }
             })
             return flag
         }, */
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
        handleEditSubmit() {

            axios
                .post("/updRole", this.editRoleForm)
                .then((response) => {
                    if (response.data.code) {
                        // 请求成功后的处理，比如提示更新成功，关闭弹框，重新获取用户列表等
                        this.$message.success("角色信息更新成功");
                        this.editDialogVisible = false;
                        // 这里可以利用response.data.updatedUser 做一些其他事情，比如更新本地缓存的当前用户信息等
                        this.currentUser = response.data.updatedUser;
                        axios.get("/roles").then((result) => {
                            this.tableData = result.data.data;
                            // this.sum = this.tableData.length
                        }); // 假设存在获取用户列表的方法，重新获取最新列表
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
        handleCloseModifyRoleDialog(done) {
            // 弹框关闭前的钩子函数，隐藏弹框，也可做其他清理操作
            this.editDialogVisible = false;
            this.$forceUpdate();
            done();
        },
        async handleEdit(row) {
            // 根据row.userId发送axios请求获取更详细的用户信息（这里假设接口返回的格式和userList中的数据格式类似）
            await this.verrifyAuth(1234575);
            if (this.verrify === true) {
                axios
                    .post(`roleById/${row.roleId}`)
                    .then((response) => {
                        this.editRoleForm = response.data.data;
                        this.editDialogVisible = true;
                        //alert(response.data.userName);
                    })
                    .catch((error) => {
                        console.error("获取用户详细信息失败：", error);
                        // 可以在这里添加一些提示信息给用户，比如弹出提示框告知获取信息失败等
                    });
            }

        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        async deleteRole(row) {
            await this.verrifyAuth(1234577);
            if (this.verrify === true) {
                this.$confirm('即将删除，请确认', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let data = [row.roleId];
                    axios.post('/roleDeleteByIds', data)
                        .then((response) => {
                            if (response.data.code) {
                                this.$message.success("删除成功");
                                axios.get("/roles").then((result) => {
                                    this.tableData = result.data.data;
                                    this.sum = this.tableData.length;
                                });
                                //this.tableData = response.data.data;
                            } else {
                                this.$message.error("删除失败，业务逻辑层面验证未通过");
                            }
                        })
                        .catch((error) => {
                            this.$message.error("删除失败：请勾选要删除的对象，" + error.message);
                        });

                    //alert(JSON.stringify(idsToDelete));
                    //alert('已批量删除！');
                    /* setTimeout(function () {
                        location.reload();
                    }, 500); */
                    //location.reload();
                }).catch(() => {
                });
            }
        },
        async onDeletes() {
            await this.verrifyAuth(1234577);
            if (this.verrify === true) {
                this.$confirm('即将批量删除，请确认', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const idsToDelete = this.multipleSelection.map(row => row.roleId);
                    axios.post('/roleDeleteByIds', idsToDelete)
                        .then((response) => {
                            if (response.data.code) {
                                this.$message.success("删除成功");
                                axios.get("/roles").then((result) => {
                                    this.tableData = result.data.data;
                                    this.sum = this.tableData.length;
                                });
                                //this.tableData = response.data.data;
                            } else {
                                this.$message.error("删除失败，业务逻辑层面验证未通过");
                            }
                        })
                        .catch((error) => {
                            this.$message.error("删除失败：请勾选要删除的对象，" + error.message);
                        });

                    //alert(JSON.stringify(idsToDelete));
                    //alert('已批量删除！');
                    /* setTimeout(function () {
                        location.reload();
                    }, 500); */
                    //location.reload();
                }).catch(() => {

                });
            }

        },
        async addRole() {
            //await this.fetchAllPermissions();
            await this.verrifyAuth(1234578)
            if (this.verrify === true) {

                this.showAddRoleForm = true;
            }
        },
        async addPower() {
            await this.verrifyAuth(1234582)
            if (this.verrify === true) {
                await this.fetchAllPermissions();
                this.showAddPowerForm = true;
            }

        },
        async handleSubmit() {
            try {
                // 发起查询请求
                const response = await axios.post('/addPower', this.addPowerForm);
                if (response.data.code === 1) {
                    this.$message.success('添加成功');
                    this.handleCancel();
                    //location.reload();
                } else {
                    this.$message.error('添加失败');
                }
            } catch (error) {
                console.error('查询失败:', error);
                this.$message.error('请求失败，请重试');
            } finally {
                // 请求完成后，关闭加载提示
            }

        },
        async handleSubmit1() {
            try {
                // 发起查询请求
                const response = await axios.post('/addRole', this.addRoleForm);
                if (response.data.code === 1) {
                    this.$message.success('添加成功');
                    this.handleCancel1();
                    axios.get("http://localhost:8080/roles").then((result) => {
                        this.tableData = result.data.data;
                        this.sum = this.tableData.length;
                    });
                    //location.reload();
                } else {
                    this.$message.error('添加失败');
                }
            } catch (error) {
                console.error('查询失败:', error);
                this.$message.error('请求失败，请重试');
            } finally {
                // 请求完成后，关闭加载提示
            }

        },
        handleCancel1() {
            this.addRoleForm.roleId = '';
            this.addRoleForm.roleName = '';
            this.showAddRoleForm = false;
        },
        handleCloseDialog1(done) {
            // 弹框关闭前的钩子函数，这里直接隐藏弹框，也可以做其他清理操作
            this.handleCancel1();
            done();
        },
        handleCancel() {
            this.addPowerForm.faPowerId = '';
            this.addPowerForm.powerId = '';
            this.addPowerForm.powerName = '';
            this.showAddPowerForm = false;
            this.resetTree();
        },
        handleCloseDialog(done) {
            // 弹框关闭前的钩子函数，这里直接隐藏弹框，也可以做其他清理操作
            this.handleCancel();
            done();
        },
        async onSubmit() {
            // 显示加载提示
            await this.verrifyAuth(1234574)
            if (this.verrify === true) {
                try {
                    // 发起查询请求
                    const response = await axios.post('/queryRoles', this.searchForm);
                    if (response.data.code === 1) {
                        this.tableData = response.data.data; // 将查询结果绑定到表格
                        this.$message.success('查询成功');
                    } else {
                        this.$message.error('查询失败');
                    }
                } catch (error) {
                    console.error('查询失败:', error);
                    this.$message.error('请输入正确的角色名和角色id');
                } finally {
                    // 请求完成后，关闭加载提示
                    if (this.loadingInstance) {
                        this.loadingInstance.close();
                    }
                }
            }
            //alert(this.verrify)
        },
        async openRoleAuthDialog(row) {
            await this.verrifyAuth(1234576)
            if (this.verrify === true) {
                this.curRoleId = row.roleId;
                this.curRoleName = row.roleName;
                this.roleAuthDialogVisible = true;

                try {
                    await this.fetchAllPermissions();
                    await this.fetchRolePermissions(this.curRoleId);
                } catch (error) {
                    console.error('加载权限数据失败:', error);
                }
            }
        },

        async fetchAllPermissions() {
            try {
                const response = await axios.get('/allPower');
                const allPermissions = response.data.data;
                this.permissionTreeData = this.buildPermissionTree(allPermissions);
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
                        this.$message.success('角色权限更新成功');
                        this.roleAuthDialogVisible = false;
                        axios.get("http://localhost:8080/roles").then((result) => {
                            this.tableData = result.data.data;
                            this.sum = this.tableData.length;
                        });
                    }
                })
                .catch(error => {
                    console.error('更新角色权限失败:', error);
                    this.$message.error('角色权限更新失败');
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
                this.$router.push("/");
            }).catch(() => { });
        },

        handleSizeChange(val) {
            this.currentPage = 1;
            this.pageSize = val;
        },

        handleCurrentChange(val) {
            this.currentPage = val;
        },

        resetTree() {
            // 重置树组件
            this.defaultCheckedKeys = [];
            this.permissionTreeData = [];
        }
    },

    mounted() {
        this.username = sessionStorage.getItem('name');
        this.userId = sessionStorage.getItem('id');
        this.userimage = sessionStorage.getItem('image');
        axios.get("http://localhost:8080/roles").then((result) => {
            this.tableData = result.data.data;
            this.sum = this.tableData.length;
        });
    }
}
</script>

<style>
.el-menu-item[disabled] {
    pointer-events: none;
    opacity: 0.6;
    /* 或其他使其看起来禁用的样式 */
}

.fixed-color-link {
    text-decoration: none;
    color: #000000;
}


.welcomeword {
    margin-right: 15px;
    font-size: 15px;
}
</style>
