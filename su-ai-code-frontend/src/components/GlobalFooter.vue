<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          href="https://github.com/Yukimiyi"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          个人全栈项目 by yukina
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: linear-gradient(180deg, rgba(75, 70, 150, 0.4) 0%, rgba(120, 80, 120, 0.3) 100%);
  backdrop-filter: blur(30px);
  text-align: center;
  padding: 20px;
  border-top: 1px solid rgba(75, 70, 150, 0.6);
  box-shadow: 0 -4px 20px rgba(75, 70, 150, 0.3);
}

.copyright {
  margin: 0;
  color: #ffffff;
  font-size: 14px;
  text-shadow:
    0 2px 15px rgba(255, 255, 255, 0.3),
    0 1px 8px rgba(151, 149, 240, 0.6);
}

.author-link {
  color: #ffffff;
  text-decoration: none;
  transition: all 0.3s ease;
}

.author-link:hover {
  color: rgba(255, 255, 255, 1);
  text-shadow:
    0 3px 20px rgba(255, 255, 255, 0.5),
    0 1px 8px rgba(151, 149, 240, 0.8);
}
</style>
