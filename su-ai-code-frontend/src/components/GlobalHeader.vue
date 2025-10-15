<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">『织梦星枢』</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="goUserSetting">
                    <SettingOutlined />
                    个人设置
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, SettingOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/chatManage',
    label: '对话管理',
    title: '对话管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/Yukimiyi/su-ai-code', target: '_blank' }, '项目仓库'),
    title: '项目仓库',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

// 前往个人设置
const goUserSetting = () => {
  router.push('/user/setting')
}
</script>

<style scoped>
.header {
  background: linear-gradient(180deg, rgba(75, 70, 150, 0.4) 0%, rgba(120, 80, 120, 0.3) 100%);
  backdrop-filter: blur(25px);
  border-bottom: 1px solid rgba(75, 70, 150, 0.6);
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #ffffff;
  text-shadow:
    0 2px 20px rgba(255, 255, 255, 0.3),
    0 1px 8px rgba(151, 149, 240, 0.6);
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent !important;
}

.ant-menu-horizontal :deep(.ant-menu-item) {
  color: rgba(255, 255, 255, 0.8) !important;
  border-bottom: 2px solid transparent !important;
}

.ant-menu-horizontal :deep(.ant-menu-item:hover) {
  color: #ffffff !important;
  background: rgba(75, 70, 150, 0.3) !important;
  border-bottom-color: rgba(151, 149, 240, 0.8) !important;
}

.ant-menu-horizontal :deep(.ant-menu-item-selected) {
  color: #ffffff !important;
  background: rgba(75, 70, 150, 0.5) !important;
  border-bottom-color: rgba(255, 255, 255, 0.8) !important;
}

.user-login-status {
  color: #ffffff;
}

.user-login-status :deep(.ant-btn-primary) {
  background: rgba(75, 70, 150, 0.6);
  border-color: rgba(151, 149, 240, 0.8);
  color: #ffffff;
  backdrop-filter: blur(10px);
}

.user-login-status :deep(.ant-btn-primary:hover) {
  background: rgba(120, 80, 120, 0.7);
  border-color: rgba(255, 255, 255, 0.6);
}
</style>
