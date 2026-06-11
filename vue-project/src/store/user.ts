import { defineStore } from 'pinia'

export const useUser = defineStore('user', {
    state: () => ({
        user_info: JSON.parse(localStorage.getItem('user_information') || '{}')
    }),
    actions: {
        updateUserInfo(newInfo: object) {
            this.user_info = newInfo;
            localStorage.setItem('user_information', JSON.stringify(newInfo));
        },
        clearUserInfo() {
            this.user_info = {};
            localStorage.removeItem('user_information');
        }
    }
});
