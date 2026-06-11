<template>
  <div>
    <NavMenu />
    <div class="user-title">
      <span>账户信息</span>
    </div>
    <div v-if="modify === true" style="margin-top:20px;">
      <el-descriptions :column="4" direction="vertical" align="center" border
        style="margin-top: 20px;width: 800px; margin: 0 auto;">
        <el-descriptions-item :rowspan="2" :width="140" label="头像" align="center">
          <el-image style="width: 140px; " :src="update_info.image" />
        </el-descriptions-item>
        <el-descriptions-item label="用户名" :width="140">{{ userS.user_info.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称" :width="140">{{ userS.user_info.userName }}</el-descriptions-item>
        <el-descriptions-item label="用户性别" :width="140">{{ userS.user_info.gender }}</el-descriptions-item>
        <el-descriptions-item label="电话号码" :width="140">{{ userS.user_info.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userS.user_info.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user_roles }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div v-if="modify === false" style="margin-top:20px;">
      <el-descriptions :column="4" direction="vertical" align="center" border
        style="margin-top: 20px;width: 800px; margin: 0 auto;">
        <el-descriptions-item :rowspan="2" :width="140" label="头像" align="center">
          <AvatarCropper :initial-image="update_info.image" @upload-success="handleAvatarSuccess" />
        </el-descriptions-item>
        <el-descriptions-item label="用户名" :width="140">
          <el-input :placeholder="userS.user_info.userId" disabled /></el-descriptions-item>
        <el-descriptions-item label="用户昵称" :width="140">
          <el-input v-model="update_info.userName" :placeholder="update_info.userName" /></el-descriptions-item>
        <el-descriptions-item label="用户性别" :width="140">
          <el-select v-model="update_info.gender" clearable :placeholder="update_info.gender" style="width: 140px">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-descriptions-item>
        <el-descriptions-item label="电话号码" :width="140"><el-input v-model="update_info.phone"
            :placeholder="update_info.phone" /></el-descriptions-item>
        <el-descriptions-item label="邮箱"><el-input v-model="update_info.email"
            :placeholder="update_info.email" /></el-descriptions-item>
        <el-descriptions-item label="密码"><el-input v-model="update_info.userPassword"
            :placeholder="update_info.userPassword" show-password type="password" /></el-descriptions-item>
      </el-descriptions>
    </div>
    <div style="height:10px"></div>
    <div class="table-pos">
      <el-button type="primary" v-if="modify === true" @click="modify_info">修改信息</el-button>
      <el-button type="primary" v-if="modify === false" @click="modify_submit">提交</el-button>
      <el-button type="primary" v-if="modify === false" @click="cancel_modify">取消</el-button>
      <el-button type="danger" @click="dialogVisible = true">注销</el-button>
      <el-dialog v-model="dialogVisible" title="Tips" width="500" :before-close="handleClose">
        <span>是否注销该账户？</span>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="warning" @click="delete_user">确认</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
    <BottomLine />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import NavMenu from '../component/NavMenu.vue';
import BottomLine from '../component/BottomLine.vue';
import AvatarCropper from '../component/UserAvator.vue'; // 引入头像裁剪组件

const dialogVisible = ref(false);

const handleClose = (done: () => void) => {
  ElMessageBox.confirm('Are you sure to close this dialog?')
    .then(() => {
      done();
    })
    .catch(() => { });
};

const options = [
  {
    value: '男',
    label: '男',
  },
  {
    value: '女',
    label: '女',
  },
];

import axios from '@/utils/axios';
import { useUser } from '@/store/user';
const userS = useUser();
const user_info = reactive({ ...userS.user_info });
const update_info = reactive({ ...userS.user_info });

watch(() => userS.user_info, (newInfo) => {
  Object.assign(update_info, newInfo);
}, { deep: true });

const user_roles = localStorage.getItem('user_roles');

const delete_user = async () => {
  dialogVisible.value = false;
  console.log(user_info.userId);
  try {
    const response = await axios.post('http://localhost:8080/deleteById?Id=' + user_info.userId, {
      Id: user_info.userId, 
    });
    if (response.data.msg === "success") {
      console.log('注销成功:', response.data);
      ElMessage({
        message: '注销成功',
        type: 'success',
      });
      localStorage.removeItem('user_information');
      localStorage.removeItem('isLoggedIn');
      window.location.href = '/';
    } else {
      ElMessage({
        message: '注销失败',
        type: 'error',
      });
    }
  } catch (error) {
    ElMessage({
      message: '注销失败',
      type: 'error',
    });
  }
};

const modify = ref(true);

const modify_info = () => {
  modify.value = false;
};

const cancel_modify = () => {
  modify.value = true;
};

const modify_submit = async () => {
  try {
    const response = await axios.post('http://localhost:8080/upu', {
      userId: update_info.userId,
      userName: update_info.userName,
      gender: update_info.gender,
      userPassword: update_info.userPassword,
      email: update_info.email,
      phone: update_info.phone,
      image: update_info.image,
    });

    if (response.data.msg === "success") {
      userS.updateUserInfo(update_info);
      ElMessage({
        message: '修改成功',
        type: 'success',
      });
      modify.value = true;
    } else {
      console.error('修改失败');
      ElMessage({
        message: '修改失败',
        type: 'error',
      });
    }
  } catch (error) {
    console.error('修改失败:', error);
    ElMessage({
      message: '修改失败',
      type: 'error',
    });
  }
};

const handleAvatarSuccess = (newImageUrl: string) => {
  update_info.image = newImageUrl; // 更新头像 URL
};
</script>
<style scoped>
.user-title {
  font-family: Noto Sans;
  display: flex;
  justify-content: center;
  margin-left: 0px;
  margin-top: 20px;
  font-size: 2.5em;
  font-weight: bold;
}

.table-pos {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.el-table {
  width: 60%;
  max-width: 100%;
}

.avatar-uploader .avatar {
  width: 140px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  text-align: center;
}
</style>