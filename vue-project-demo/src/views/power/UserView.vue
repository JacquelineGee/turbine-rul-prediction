<template>
    <div>
        <HeaderView></HeaderView>
        <div style="display: flex;">
            <el-container style="border: 1px solid #eee">
                <el-container>
                    <AsideView></AsideView>

                    <el-main>
                        <!--面包屑  -->
                        <el-breadcrumb separator-class="el-icon-arrow-right">
                            <el-breadcrumb-item @click="clearNavState" :to="{ path: '/home' }">首页</el-breadcrumb-item>
                            <el-breadcrumb-item>用户管理</el-breadcrumb-item>
                        </el-breadcrumb>
                        <br>
                        <!-- 表单 -->
                        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                            <el-form-item label="账号">
                                <el-input v-model="searchForm.userId" placeholder="账号"></el-input>
                            </el-form-item>
                            <el-form-item label="用户名">
                                <el-input v-model="searchForm.userName" placeholder="用户名"></el-input>
                            </el-form-item>

                            <el-form-item label="性别">
                                <el-select v-model="searchForm.gender" placeholder="性别">
                                    <el-option label="男" value="男"></el-option>
                                    <el-option label="女" value="女"></el-option>
                                    <el-option label="请选择性别" value=""></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="onSubmit" :disabled="userSearchBind()"
                                    plain>查询</el-button>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="onAdd" :disabled="useraddBind()"
                                    plain>添加用户</el-button>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="danger" @click="onDeletes" :disabled="userDeleteBind()"
                                    plain>批量删除</el-button>
                            </el-form-item>
                        </el-form>
                        <!-- 添加用户的表单，初始为隐藏状态 -->
                        <el-dialog title="添加用户" :visible.sync="showAddUserForm" width="30%"
                            :before-close="handleCloseAddUserDialog">
                            <el-form ref="addUserForm" :model="addUserForm" :rules="rules" label-width="80px">
                                <el-form-item label="用户名" prop="userName">
                                    <el-input v-model="addUserForm.userName"></el-input>
                                </el-form-item>
                                <el-form-item label="账号" prop="userId">
                                    <el-input v-model="addUserForm.userId"></el-input>
                                </el-form-item>
                                <el-form-item label="密码" prop="userPassword">
                                    <el-input v-model="addUserForm.userPassword" type="password"></el-input>
                                </el-form-item>
                                <el-form-item label="性别">
                                    <el-select v-model="addUserForm.gender" placeholder="请选择性别">
                                        <el-option label="男" value="男"></el-option>
                                        <el-option label="女" value="女"></el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="头像" prop="image">
                                    <el-upload class="avatar-uploader" action="http://127.0.0.1:8080/upload"
                                        :show-file-list="false" :on-success="handleAvatarSuccess1"
                                        :before-upload="beforeAvatarUpload" :file-list="fileList" name="image">
                                        <img v-if="addUserForm.image" :src="addUserForm.image" alt="myimage"
                                            class="avatar" />
                                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                                    </el-upload>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="onSubmitAddUser">提交</el-button>
                                    <el-button @click="resetAddUserForm">重置</el-button>
                                    <el-button @click="showAddUserForm = false">取消</el-button>
                                </el-form-item>
                            </el-form>
                        </el-dialog>
                        <!-- 表格 -->
                        <el-table :data="tableData.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
                            @selection-change="handleSelectionChange" border :row-style="rowStyle">
                            <el-table-column type="selection" width="55">
                            </el-table-column>
                            <el-table-column prop="userId" label="账号" width="180"></el-table-column>
                            <el-table-column prop="userName" label="用户名" width="180"></el-table-column>
                            <el-table-column label="头像" width="150">
                                <template slot-scope="scope">
                                    <img :src="scope.row.image" width="120px" height="100px">
                                </template>
                            </el-table-column>
                            <el-table-column label="性别" width="50">
                                <template slot-scope="scope">
                                    {{ scope.row.gender == '男' ? '男' : '女' }}
                                </template>
                            </el-table-column>
                            <el-table-column prop="roles" label="用户类别" width="150">
                                <template slot-scope="scope">
                                    <!-- 判断 roles 是否为空，为空则显示默认值，这里假设默认值为 '普通用户' -->
                                    {{ scope.row.roles ? scope.row.roles : '该用户暂未分配角色' }}
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
                                        :disabled="userEditBind()">编辑</el-button>
                                    <el-button type="danger" size="mini" @click="onDelete(scope.row)"
                                        :disabled="userDeleteBind()">删除</el-button>
                                    <el-button type="success" size="mini" @click="handleModifyRole(scope.row)"
                                        :disabled="userRoleEditBind()">修改用户角色</el-button>
                                </template>

                            </el-table-column>
                        </el-table>
                        <el-dialog title="编辑用户信息" :visible.sync="editDialogVisible" width="30%"
                            :before-close="handleCloseDialog">

                            <el-form :model="editUserForm" label-width="80px">

                                <el-form-item label="用户名">
                                    <el-input v-model="editUserForm.userName"></el-input>
                                </el-form-item>
                                <el-form-item label="账   号">
                                    <el-input v-model="editUserForm.userId" disabled></el-input>
                                </el-form-item>
                                <el-form-item label="密   码">
                                    <el-input v-model="editUserForm.userPassword" type="password"></el-input>
                                </el-form-item>
                                <el-form-item label="性   别">
                                    <el-select v-model="editUserForm.gender" placeholder="请选择性别">
                                        <el-option label="男" value="男"></el-option>
                                        <el-option label="女" value="女"></el-option>
                                    </el-select>
                                </el-form-item>
                                <el-form-item label="头像" prop="editUserForm.image">
                                    <el-upload class="avatar-uploader" action="http://127.0.0.1:8080/upload"
                                        :show-file-list="false" :on-success="handleAvatarSuccess"
                                        :before-upload="beforeAvatarUpload" :file-list="fileList" name="image">
                                        <img v-if="editUserForm.image" :src="editUserForm.image" alt="myimage"
                                            class="avatar" />
                                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                                    </el-upload>
                                </el-form-item>
                            </el-form>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCancel">取消</el-button>
                                <el-button type="primary" @click="handleSubmit">提交</el-button>
                            </span>
                        </el-dialog>
                        <el-dialog title="修改用户角色" :visible.sync="modifyRoleDialogVisible" width="50%"
                            :before-close="handleCloseModifyRoleDialog">
                            <div>
                                <h1>用户: {{ this.curUserName }}</h1>
                            </div>
                            <el-transfer v-model="transferValue" :data="transferData" :titles="['未分配角色', '已分配角色']"
                                :button-texts="['撤销', '分配']"></el-transfer>
                            <span slot="footer" class="dialog-footer">
                                <el-button @click="handleCancelModifyRole">取消</el-button>
                                <el-button type="primary" @click="handleSubmitModifyRole">确定</el-button>
                            </span>
                        </el-dialog>

                        <br>

                        <!-- 分页条 -->
                        <!-- Pagination 分页 -->
                        <div>
                            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                                :current-page="currentPage" :page-sizes="[1, 2, 5, 10, 20]" :page-size="pageSize"
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
            tableData: [],
            transferValue: [], // 穿梭框绑定的值，存放已选的角色id
            transferData: [], // 穿梭框的数据源
            editDialogVisible: false, // 控制编辑弹框是否显示
            editUserForm: {}, // 用于绑定编辑弹框内表单数据
            currentPage: 1, // 当前页
            pageSize: 10, // 每页条数
            total: 0, // 数据总条数
            searchForm: {
                userName: "",
                gender: "",
                userId: ""
            },
            multipleSelection: [],
            sum: '',
            user: '',
            username: '',
            userimage: '',
            userId: '',
            version: '',
            nowuser: '',
            curUserName: "",
            operationList: [],
            showAddUserForm: false,
            // 添加用户表单的数据模型
            addUserForm: {
                userName: '',
                userPassword: '',
                gender: '',
                image: '',
                userId: '',

            },
            rules: {
                userName: [
                    { required: true, message: "请输入用户名", trigger: "blur" }
                ],
                userId: [
                    { required: true, message: "请输入账号", trigger: "blur" }
                ],
                userPassword: [
                    { required: true, message: "请输入密码", trigger: "blur" }
                ]
            }
        }

    },
    created() {
        //this.getUserList()
        this.userId = sessionStorage.getItem('id');
        //alert(this.userId+"created");
        this.getOperationList();
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
        getOperationList() {
            //alert(this.userId)
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
            if (id === '') id = true
            return id
        },
        roleDeleteBind() {
            let str = "角色删除"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },
        roleAddBind() {
            let str = "添加角色"
            let id = ''
            this.operationList.forEach(value => {
                if (value.powerName === str) id = false;
            })
            if (id === '') id = true
            return id
        },

        beforeAvatarUpload(file) {
            const isJPG = file.type === "image/jpeg";
            const isPNG = file.type === "image/png";
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isJPG && !isPNG) {
                this.$message.error("上传头像图片只能是 JPG 或 PNG 格式!");
            }
            if (!isLt2M) {
                this.$message.error("上传头像图片大小不能超过 2MB!");
            }
            return (isJPG || isPNG) && isLt2M;
        },
        handleAvatarSuccess(res) {
            this.editUserForm.image = res.data;
            this.$nextTick(() => {
            });
        },
        handleAvatarSuccess1(res) {
            this.addUserForm.image = res.data;
            this.$nextTick(() => {
            });
        },
        // 提交添加用户信息的方法
        onSubmitAddUser() {
            // 这里可以添加提交数据到后端的逻辑，例如发送axios请求
            //alert('提交用户信息：'+this.addUserForm.image);
            let data = this.addUserForm;
            axios.post('/addUser', data)
                .then((response) => {
                    if (response.data.code) {
                        this.$message.success("用户添加成功");
                        this.resetAddUserForm();
                        this.showAddUserForm = false;
                        // 重新获取用户列表等数据，刷新页面显示（假设存在获取用户列表方法，类似之前的axios.get("/users")等操作）
                        axios.get("/users").then((result) => {
                            this.tableData = result.data.data;
                            // this.sum = this.tableData.length
                        });
                    } else {
                        this.$message.error("用户添加失败失败，业务逻辑层面验证未通过");
                    }
                })
                .catch(() => {
                    this.$message.error("该用户已存在");

                });
            // 提交成功后可以选择隐藏表单并重置数据


        },
        // 重置添加用户表单数据的方法
        resetAddUserForm() {
            //alert('重置表单')
            this.addUserForm.image = '';  // 清空图片路径
            this.addUserForm.userId = '';
            this.addUserForm.userName = '';
            this.addUserForm.userPassword = '';
            this.addUserForm.gender = '';
            // this.$refs.addUserForm.resetFields();  // 调用resetFields重置表单数据
        },
        // 关闭添加用户对话框时的钩子函数，可用于重置数据等操作
        handleCloseAddUserDialog() {
            try {
                this.resetAddUserForm();
            } catch (error) {
                // alert("重置表单数据时出错:", error);
            }
            this.showAddUserForm = false; // 确保隐藏对话框
        },
        async handleModifyRole(row) {
            // 点击修改用户类别按钮时，先根据用户id获取当前用户已有的角色信息，发送axios请求（这里假设接口返回的已选角色id列表）
            await this.verrifyAuth(1234573);
            if (this.verrify === true) {
                this.nowuser = row.userId;
                this.curUserName = row.userName;
                axios.post(`/role/${row.userId}`)
                    .then((response) => {
                        // 从响应体中直接获取已选角色信息数组，赋值给transferValue
                        this.transferValue = response.data.data.map(item => item.roleId);
                        // 同时获取所有可用的角色数据作为穿梭框数据源，发送另一个axios请求（假设接口返回角色列表数据）
                        axios.get('/roles')
                            .then((roleResponse) => {
                                this.transferData = roleResponse.data.data.map(role => ({
                                    key: role.roleId,
                                    label: role.roleName,
                                    disabled: false // 这里假设默认都不禁用，可根据实际业务调整
                                }));
                                this.modifyRoleDialogVisible = true;
                            })
                            .catch((roleError) => {
                                console.error("获取角色列表数据失败：", roleError);
                            });
                    })
                    .catch((error) => {
                        console.error("获取用户已有角色信息失败：", error);
                    });
            }
        },
        handleCancelModifyRole() {
            // 点击取消按钮，隐藏修改用户类别弹框
            //alert('点击取消');
            this.modifyRoleDialogVisible = false;
            this.$forceUpdate();
        },
        handleCloseModifyRoleDialog(done) {
            // 弹框关闭前的钩子函数，隐藏弹框，也可做其他清理操作
            this.modifyRoleDialogVisible = false;
            this.$forceUpdate();
            done();
        },
        handleSubmitModifyRole() {
            const selectedRoleIds = this.transferValue;
            const userId = this.nowuser; // 假设你有记录当前正在编辑用户的id变量，需根据实际添加逻辑
            // 发送axios请求，将新选择的角色信息更新到后端（这里假设接口接收用户id和角色id列表来更新用户角色关联）
            //alert(userId);
            axios.post('/user/updateRoles', {
                userId,
                roleIds: selectedRoleIds
            })
                .then((response) => {
                    if (response.data.code) {
                        this.$message.success("用户角色更新成功");
                        this.modifyRoleDialogVisible = false;
                        // 重新获取用户列表等数据，刷新页面显示（假设存在获取用户列表方法，类似之前的axios.get("/users")等操作）
                        axios.get("/users").then((result) => {
                            this.tableData = result.data.data;
                            // this.sum = this.tableData.length
                        });
                    } else {
                        this.$message.error("用户角色更新失败，业务逻辑层面验证未通过");
                    }
                })
                .catch((error) => {
                    this.$message.error("用户角色更新失败：" + error.message);
                });
        },
        async onSubmit() {
            await this.verrifyAuth(1234570);
            if (this.verrify === true) {
                //alert("查询数据");
                let data = this.searchForm;
                axios.post('/ifUser', data)
                    .then((response) => {
                        if (response.data.code) {
                            this.$message.success("查询成功");
                            this.tableData = response.data.data;
                        } else {
                            this.$message.error("用户角色更新失败，业务逻辑层面验证未通过");
                        }
                    })
                    .catch((error) => {
                        this.$message.error("查询失败，请输入正确查询信息：" + error.message);
                    });
            }
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        async onDeletes() {
            await this.verrifyAuth(1234572);
            if (this.verrify === true) {
                this.$confirm('即将批量删除，请确认', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    const idsToDelete = this.multipleSelection.map(row => row.userId);
                    axios.post('/deleteByIds', idsToDelete)
                        .then((response) => {
                            if (response.data.code) {
                                this.$message.success("删除成功");
                                axios.get("/users").then((result) => {
                                    this.tableData = result.data.data;
                                    // this.sum = this.tableData.length
                                });
                                //this.tableData = response.data.data;
                            } else {
                                this.$message.error("删除失败，业务逻辑层面验证未通过");
                            }
                        })
                        .catch(() => {
                            this.$message.error("删除失败，请重新勾选需要删除的用户");
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
        async handleEdit(row) {
            // 根据row.userId发送axios请求获取更详细的用户信息（这里假设接口返回的格式和userList中的数据格式类似）
            await this.verrifyAuth(1234571);
            this.version = row.version;
            if (this.verrify === true) {
                axios
                    .post(`user/${row.userId}`)
                    .then((response) => {
                        this.editUserForm = response.data.data;
                        this.editDialogVisible = true;
                        //alert(response.data.userName);
                    })
                    .catch((error) => {
                        console.error("获取用户详细信息失败：", error);
                        // 可以在这里添加一些提示信息给用户，比如弹出提示框告知获取信息失败等
                    });
            }
        },
        handleCancel() {
            // 点击取消按钮，隐藏弹框
            this.editDialogVisible = false;
        },
        handleCloseDialog(done) {
            // 弹框关闭前的钩子函数，这里直接隐藏弹框，也可以做其他清理操作
            this.editDialogVisible = false;
            done();
        },
        handleSubmit() {
            axios
                .post("/upu", this.editUserForm, this.version)
                .then((response) => {
                    if (response.data.code) {
                        // 请求成功后的处理，比如提示更新成功，关闭弹框，重新获取用户列表等
                        this.$message.success("用户信息更新成功");
                        this.editDialogVisible = false;
                        // 这里可以利用response.data.updatedUser 做一些其他事情，比如更新本地缓存的当前用户信息等
                        this.currentUser = response.data.updatedUser;
                        axios.get("/users").then((result) => {
                            this.tableData = result.data.data;
                            // this.sum = this.tableData.length
                        }); // 假设存在获取用户列表的方法，重新获取最新列表
                    } else {
                        this.$message.error("用户信息更新失败，请刷新后再次尝试");
                    }
                })
                .catch((error) => {
                    // 请求失败后的处理，提示错误信息
                    this.$message.error("用户信息更新失败：" + error.message);
                });
        },
        async onDelete(row) {
            await this.verrifyAuth(1234572);
            if (this.verrify === true) {
                this.$confirm('即将删除，请确认', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let data = [row.userId.toString()];
                    //alert(data);
                    axios.post('/deleteByIds', data)
                        .then((response) => {
                            if (response.data.code) {
                                // this.$message.success("删除成功");
                                // location.reload();
                                //this.tableData = response.data.data;
                                axios.get("/users").then((result) => {
                                    this.tableData = result.data.data;
                                    // this.sum = this.tableData.length
                                });
                            } else {
                                this.$message.error("删除失败，业务逻辑层面验证未通过");
                            }
                        })
                        .catch((error) => {
                            this.$message.error("删除失败：" + error.message);
                        });
                    this.$message({
                        type: 'success',
                        message: '已删除!'
                    }
                    );
                    //alert('已批量删除！');
                    //location.reload();
                }).catch(() => {

                });
            }
        },
        /*  handleSizeChange: function (val) {
             alert("每页记录数变化" + val)
         },
         handleCurrentChange: function (val) {
             alert("页码发生变化" + val)
         }, */
        async onAdd() {
            await this.verrifyAuth(1234581);
            if (this.verrify === true) {

                this.showAddUserForm = true
            }
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

        //alert(this.userimage);
        //alert(this.username);
        axios.get("/users").then((result) => {
            this.tableData = result.data.data;
            // this.sum = this.tableData.length
        });
    }
}
</script>

<style scoped>
::v-deep.el-table__body tr.el-table__row--striped td {
    background-color: #f0f0f0 !important;
}

/* 修改表格偶数行背景色 */
::v-deep.el-table__body tr.el-table__row--striped--even td {
    background-color: #7f0f0f !important;
}

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

.compact-form.el-form-item {
    margin-bottom: 10px;
    /* 调整表单行之间的垂直间距 */
}

.compact-form.el-form-item__label {
    width: 80px;
    /* 调整标签的宽度，使其更紧凑 */
}

.compact-form.el-input {
    width: calc(100% - 80px);
    /* 根据标签宽度计算输入框的宽度，使其自适应剩余空间 */
}

.avatar {
    /* width: 178px; */
    height: 120px;
    display: block;
}
</style>