import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', {
    state: () => ({
        user_info: JSON.parse(localStorage.getItem("user_information") || '{}')
    }),
    actions: {
        setUserInfo(newUserInfo: any) {
            this.user_info = newUserInfo;
            localStorage.setItem("user_information", JSON.stringify(newUserInfo));
        },
        logout() {
            this.user_info = {}; // 清空用户信息
            localStorage.removeItem("user_information");
            localStorage.removeItem("isLoggedIn");
            window.location.href = '/'; // 刷新页面
        }
    }
});
