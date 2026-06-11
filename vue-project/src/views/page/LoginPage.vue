<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue';
import type { FormRules, FormInstance } from 'element-plus';
import { ElMessage } from 'element-plus';
import type { TabsPaneContext } from 'element-plus';
import axios from '@/utils/axios';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/userStore'
import { useUser } from '@/store/user'

const router = useRouter();
const userS = useUserStore();
// 表单相关逻辑
const ruleFormRef = ref<FormInstance>();
const ruleFormRefNew = ref<FormInstance>();

const formLabelAlign = reactive({
  id: '',
  password: '',
});

const formLabelAlignnew = reactive({
  id: '',
  name: '',
  password: '',
  pass2: '',
  tel: '',
  email: '',
});

// 表单验证函数
// const checkId = (rule: any, value: any, callback: any) => {
//   if (!value) {
//     return callback(new Error('请输入用户名'));
//   } else callback();
// };
const checkId = (rule: any, value: string, callback: any) => {
  if (!value) {
    return callback(new Error('请输入用户名'));
  }
  
  // Regular expression to match only letters and numbers
  const alphanumericRegex = /^[a-zA-Z0-9]+$/;
  
  if (!alphanumericRegex.test(value)) {
    return callback(new Error('只能为数字或英文字母'));
  }
  
  return callback();
};

const checkName = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入昵称'));
  } else callback();
};

const checkPass = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入密码'));
  } else callback();
};

const checkTel = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入电话号码'));
  } else callback();
};

const checkEmail = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请输入邮箱地址'));
  } else callback();
};

const confirmPass = (rule: any, value: any, callback: any) => {
  if (!value) {
    return callback(new Error('请确认密码'));
  } else if (value !== formLabelAlignnew.password) {
    callback(new Error('两次密码不一致'));
  } else callback();
};

const rules = reactive<FormRules<typeof formLabelAlign>>({
  id: [{ validator: checkName, trigger: 'blur' }],
  password: [{ validator: checkPass, trigger: 'blur' }],
});

const rulesnew = reactive<FormRules<typeof formLabelAlignnew>>({
  id: [{ validator: checkId, trigger: 'blur' }],
  name: [{ validator: checkName, trigger: 'blur' }],
  password: [{ validator: checkPass, trigger: 'blur' }],
  pass2: [{ validator: confirmPass, trigger: 'blur' }],
  tel: [
    { validator: checkTel, trigger: 'blur' },
    { type: 'number', message: '请输入合法的手机号码' },
  ],
  email: [{ validator: checkEmail, trigger: 'blur' }],
});

// 注册逻辑
const register_submit = async () => {
  try {
    await ruleFormRefNew.value?.validate();
    const response = await axios.post('/addUser', {
      userId: formLabelAlignnew.id,
      userName: formLabelAlignnew.name,
      userPassword: formLabelAlignnew.password,
      phone: formLabelAlignnew.tel,
      email: formLabelAlignnew.email,
    });

    if (response.data.msg === 'success') {
      ElMessage({
        message: '注册成功，请登录',
        type: 'success',
      });

      // 清空注册表单
      Object.assign(formLabelAlignnew, {
        id: '',
        name: '',
        password: '',
        pass2: '',
        tel: '',
        email: '',
      });

      // 切换到登录表单
      activeName.value = 'first';
    } else {
      ElMessage({
        message: '注册失败',
        type: 'error',
      });
    }
  } catch (error) {
    ElMessage({
      message: '注册失败，该用户名已存在',
      type: 'error',
    });
  }
};

const Login_Button = async () => {
  try {
    await ruleFormRef.value?.validate();
    const response = await axios.post('/log', {
      userId: formLabelAlign.id,
      userPassword: formLabelAlign.password,
    });

    if (response.data.msg === 'success') {
      ElMessage({
        message: '登录成功！即将跳转',
        type: 'success',
      });
      localStorage.setItem('user_information', JSON.stringify(response.data.data));
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('jwt',response.data.data.jwt);
      userS.setUserInfo(response.data.data);

      // 获取用户角色
      const response1 = await axios.post('/ifUser', {
        userId: formLabelAlign.id,
      });
      if (response1.data.msg === 'success') {
        localStorage.setItem('user_roles', response1.data.data[0].roles || '该用户暂未分配角色');
      }

      // 获取用户权限
      await getOperationList(formLabelAlign.id);

      // 延迟1秒后跳转
      setTimeout(() => {
        window.location.href='/'; 
      }, 1000);
    } else {
      ElMessage({
        message: '登录失败，请检查用户名和密码',
        type: 'error',
      });
    }
  } catch (error) {
    ElMessage({
      message: '登录失败',
      type: 'error',
    });
  }
};

// 权限管理逻辑
const operationList = ref<any[]>([]);

const HPdBind = () => {
  const str = '历史预测数据查看';
  return operationList.value.some(value => value.powerName === str) ? 1 : 0;
};

const datasetBind = () => {
  const str = '查看数据集';
  return operationList.value.some(value => value.powerName === str) ? 1 : 0;
};

const model1Bind = () => {
  const str = '模型1';
  return operationList.value.some(value => value.powerName === str) ? 1 : 0;
};
const model2Bind = () => {
  const str = '模型2';
  return operationList.value.some(value => value.powerName === str) ? 1 : 0;
};
const model3Bind = () => {
  const str = '模型3';
  return operationList.value.some(value => value.powerName === str) ? 1 : 0;
};

const getOperationList = async (id: string) => {
  try {
    const response = await axios.post(`/userPower/${id}`);
    operationList.value = response.data.data;
    sessionStorage.setItem('model1', model1Bind().toString());
    sessionStorage.setItem('model2', model2Bind().toString());
    sessionStorage.setItem('model3', model3Bind().toString());
    sessionStorage.setItem('dataset', datasetBind().toString());
    sessionStorage.setItem('HPd', HPdBind().toString());
    
  } catch (error) {
    console.error('获取权限列表失败:', error);
  }
};

// Tab 切换逻辑
const activeName = ref('first');
const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event);
};
</script>

<template>
    <div class="background">
        <div id="loginInput">
            <el-tabs v-model="activeName" type="card" class="demo-tabs1" @tab-click="handleClick">
                <el-tab-pane name="first">
                    <template #label>
                        <span class="label-style">
                            登录
                        </span>
                    </template>
                    <el-form ref="ruleFormRef" label-width="auto" :model="formLabelAlign" :rules="rules"
                        style="max-width: 600px" status-icon>
                        <el-form-item label="用户名" label-position="left" prop="name">
                            <el-input v-model="formLabelAlign.id" clearable />
                        </el-form-item>
                        <el-form-item label="密码" label-position="left" prop="password">
                            <el-input type="password" v-model="formLabelAlign.password" clearable show-password @keyup.enter="Login_Button" />
                        </el-form-item>
                    </el-form>
                    <div class="buttons">
                        <div class="submitButton">                      
                                <el-button class="button-color" @click="Login_Button">
                                    登录
                                </el-button>
                        </div>
                        <!-- <div class="resetButton">
                            <el-button @click="reset" class="button-color">
                                重置
                            </el-button>
                        </div> -->
                    </div>
                </el-tab-pane>
                <el-tab-pane>
                    <template #label>
                        <span class="label-style">
                            注册
                        </span>
                    </template>
                    <el-form ref="ruleFormRefNew" label-width="auto" :model="formLabelAlignnew" :rules="rulesnew"
                        style="max-width: 600px" status-icon>
                        <el-form-item label="用户名" label-position="left" prop="id">
                            <el-input v-model="formLabelAlignnew.id" clearable />
                        </el-form-item>
                        <el-form-item label="昵称" label-position="left" prop="name">
                            <el-input v-model="formLabelAlignnew.name" clearable />
                        </el-form-item>
                        <el-form-item label="密码" label-position="left" prop="password">
                            <el-input type="password" v-model="formLabelAlignnew.password" clearable show-password />
                        </el-form-item>
                        <el-form-item label="确认密码" label-position="left" prop="pass2">
                            <el-input type="password" v-model="formLabelAlignnew.pass2" clearable show-password />
                        </el-form-item>
                        <el-form-item label="电话号码" label-position="left" prop="tel">
                            <el-input v-model.number="formLabelAlignnew.tel" clearable />
                        </el-form-item>
                        <el-form-item label="邮箱" label-position="left" prop="email">
                            <el-input v-model="formLabelAlignnew.email" clearable />
                        </el-form-item>
                    </el-form>
                    <div class="buttons">
                        <div class="submitButton">
                            <el-button class="button-color" @click="register_submit">
                                注册
                            </el-button>
                        </div>
                        <!-- <div class="resetButton">
                            <el-button @click="reset" class="button-color">
                                重置
                            </el-button>
                        </div> -->
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<style scoped>
.background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 120%;
    background-image: url('https://vue-project-image.oss-cn-shanghai.aliyuncs.com/background.jpeg');
    background-size: cover;
    /* Make sure the image covers the entire screen */
    background-position: center;
    /* Center the image */
    background-repeat: no-repeat;
    z-index: 0;
    /* Ensure the background is behind the login form */
}

#loginInput {
    margin: 0 auto;
    margin-top: 30vh;
    width: 300px;
    background-color: rgba(255, 255, 255, 0.9);
    padding: 80px 80px;
    border-radius: 10px;
    border: 3px solid rgb(250, 250, 250);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    z-index: 1;
    position: relative;
}

.buttons {
    display: flex;
    width: 500px;
    margin-top: 15px;
}

.submitButton {
    display: inherit;
    margin-left: 120px;
    /* padding:8px 50px; */
}

.resetButton {
    display: inherit;
    margin-left: 60px;
}

.demo-tabs1>.el-tabs__item {
    padding: 32px;
    color: #0084FF;
    font-size: 32px;
    font-weight: 600;
}

.label-style {
    vertical-align: middle;
    font-family: Noto Sans;
    font-weight: bold;
}

.demo-tabs1 .label-style:hover {
    color: #0084FF;
}

.demo-tabs1 .label-style:active {
    color: #0084FF;
}

.demo-tabs1 .label-style:is-active {
    color: black;
}

.button-color {
    background-color: #0084FF;
    color: white;
}

.button-color:hover {
    font-weight: bold;
}

.button-color:active {
    color: white;
}


/* 
.demo-tabs>.el-tabs__content {
    padding: 32px;
    color: #000000;
    font-size: 32px;
    font-weight: 600;
} */
</style>