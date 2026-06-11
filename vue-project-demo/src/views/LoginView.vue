<template>
  <div id="app" :style="{ backgroundImage: `url(${backgroundImage})` }">
    <div class="login-container">
      <h2 style="text-align: center;">登录</h2>
      <el-form :model="form" :rules="rules" ref="loginForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">登录</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      operationList: [],
      loginsuccess: "",
      // 设置固定背景图片的 URL
      backgroundImage: require('../..//img/bg1.jpg') // 替换为你的背景图片 URL
    };
  },
  methods: {
    userBind() {
      let str = "用户查询"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = 1;
      })
      if (id === '') id = 0
      return id
    },
    roleBind() {
      let str = "角色查询"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) {
          id = 1;
        }
      })
      if (id === '') id = 0
      return id
    },
    opBind() {
      let str = "操作管理"
      let id = ''
      this.operationList.forEach(value => {
        if (value.powerName === str) id = 1;
      })
      if (id === '') id = 0
      return id
    },
    getOperationList(id) {
      axios.post(`/userPower/${id}`).then((result) => {
        this.operationList = result.data.data;
        //console.log(this.operationList);
        this.operationList.forEach(value => {
          console.log(value.powerName); // 输出每个权限的名称
          sessionStorage.setItem("op", this.opBind());
          sessionStorage.setItem("role", this.roleBind());
          sessionStorage.setItem("user", this.userBind());
          
        });
      });


    },
    submitForm() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          axios.post('/log', {
            userId: this.form.username,
            userPassword: this.form.password
          })
            .then(response => {
              this.loginsuccess = response.data.msg;
              if (this.loginsuccess == "success") {

                this.$message.success('登录成功！');

                localStorage.setItem("jwt", response.data.data.jwt);
                sessionStorage.setItem("name", response.data.data.userName);
                // alert(sessionStorage.getItem('name'));
                alert("欢迎您，" + sessionStorage.getItem('name') + " !");
                sessionStorage.setItem("id", response.data.data.userId);
                sessionStorage.setItem("image", response.data.data.image);
                this.getOperationList(response.data.data.userId);

                this.$router.push('/home');
              }
              //const token = response.data.token; // 假设服务器返回了 token
              //localStorage.setItem('token', token); // 保存 token 到 localStorage
              else
                this.$message.error('用户名或密码错误');
            })
        } else {
          this.$message.error('请检查输入');
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.loginForm.resetFields();
    }
  }
};
</script>

<style scoped>
#app {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  /* 背景固定 */
}

.login-container {
  width: 400px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.router-link-active {
  color: #000000;
}
</style>
