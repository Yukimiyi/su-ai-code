<template>
  <div class="app-card" :class="{ 'app-card--featured': featured }">
    <div class="app-preview">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
      <div v-else class="app-placeholder">
        <span>🤖</span>
      </div>
      <div class="app-overlay">
        <a-space>
          <a-button type="primary" @click="handleViewChat">查看对话</a-button>
          <a-button v-if="app.deployKey" type="default" @click="handleViewWork">查看作品</a-button>
        </a-space>
      </div>
    </div>
    <div class="app-info">
      <div class="app-info-left">
        <a-avatar :src="app.user?.userAvatar" :size="40">
          {{ app.user?.userName?.charAt(0) || 'U' }}
        </a-avatar>
      </div>
      <div class="app-info-right">
        <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
        <p class="app-author">
          {{ app.user?.userName || (featured ? '官方' : '未知用户') }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  app: API.AppVO
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void
  (e: 'view-work', app: API.AppVO): void
}

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const emit = defineEmits<Emits>()

const handleViewChat = () => {
  emit('view-chat', props.app.id)
}

const handleViewWork = () => {
  emit('view-work', props.app)
}
</script>

<style scoped>
.app-card {
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(151, 149, 240, 0.05) 50%,
    rgba(251, 200, 212, 0.05) 100%
  );
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(151, 149, 240, 0.1);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition:
    transform 0.3s,
    box-shadow 0.3s;
  cursor: pointer;
}

.app-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 50px rgba(151, 149, 240, 0.2);
}

.app-preview {
  height: 180px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.app-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.app-placeholder {
  font-size: 48px;
  color: #d9d9d9;
}

.app-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(151, 149, 240, 0.3);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.app-card:hover .app-overlay {
  opacity: 1;
}

.app-info {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-info-left {
  flex-shrink: 0;
}

.app-info-right {
  flex: 1;
  min-width: 0;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px;
  color: #ffffff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 1px 3px rgba(151, 149, 240, 0.3);
}

.app-author {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.app-overlay :deep(.ant-btn-primary) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  backdrop-filter: blur(10px);
}

.app-overlay :deep(.ant-btn-default) {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
  color: #ffffff;
  backdrop-filter: blur(10px);
}
</style>
