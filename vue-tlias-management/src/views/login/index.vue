<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { setAuth } from "@/utils/auth";
import { getFirstAccessiblePath } from "@/utils/menu";

const router = useRouter();
const loginForm = ref({
  username: "",
  password: "",
  captchaId: "",
  captchaCode: "",
});
const captchaImage = ref("");
const loginBackgroundStyle = {
  backgroundImage:
    "linear-gradient(120deg, rgba(46, 26, 14, 0.72), rgba(67, 43, 26, 0.42)), url('/head/v2-f3ea5ca8859b092b89da66d0e1a06a0f_1440w.jpg')",
};

const loadCaptcha = async () => {
  const result = await request.get("/captcha");
  if (result.code) {
    loginForm.value.captchaId = result.data.captchaId;
    loginForm.value.captchaCode = "";
    captchaImage.value = result.data.imageBase64;
  }
};

onMounted(() => {
  loadCaptcha();
});

const login = async () => {
  if (
    !loginForm.value.username ||
    !loginForm.value.password ||
    !loginForm.value.captchaCode
  ) {
    ElMessage.warning("请输入用户名、密码和验证码");
    return;
  }
  const result = await request.post("/login", loginForm.value);
  if (result.code) {
    setAuth(result.data);
    ElMessage.success("登录成功");
    router.push(getFirstAccessiblePath(result.data.permissions));
  } else {
    ElMessage.error(result.msg || "用户名或密码错误");
    loadCaptcha();
  }
};

const reset = () => {
  loginForm.value = {
    username: "",
    password: "",
    captchaId: loginForm.value.captchaId,
    captchaCode: "",
  };
};
</script>

<template>
  <div class="login-shell" :style="loginBackgroundStyle">
    <div class="login-overlay"></div>
    <div class="login-card">
      <div class="login-copy">
        <p class="login-eyebrow">TLIAS</p>
        <h1 class="login-title">教育管理系统</h1>
        <p class="login-subtitle">
          以更清晰的视图管理班级、学员、员工、角色与统计数据，
          让教学协同与后台运营放在同一块控制台上完成。
        </p>
        <div class="login-tags">
          <span class="login-tag">班级与学员协同</span>
          <span class="login-tag">角色权限控制</span>
          <span class="login-tag">数据统计总览</span>
        </div>
      </div>

      <div class="login-form-panel">
        <div class="form-heading">
          <p class="form-eyebrow">Welcome Back</p>
          <h2>登录后台</h2>
          <p>请输入用户名、密码与验证码进入系统。</p>
        </div>

        <el-form label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              type="password"
              v-model="loginForm.password"
              placeholder="请输入密码"
              show-password
              size="large"
            />
          </el-form-item>
          <el-form-item label="验证码" prop="captchaCode">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="请输入验证码"
                size="large"
              />
              <img
                class="captcha-image"
                :src="captchaImage"
                alt="captcha"
                @click="loadCaptcha"
              />
            </div>
          </el-form-item>
          <div class="login-actions">
            <el-button class="action-button" type="primary" @click="login"
              >登 录</el-button
            >
            <el-button class="action-button secondary" @click="reset"
              >重 置</el-button
            >
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-shell {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 28px;
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
}

.login-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(
      circle at 15% 20%,
      rgba(255, 255, 255, 0.18),
      transparent 28%
    ),
    radial-gradient(
      circle at 85% 18%,
      rgba(255, 215, 179, 0.24),
      transparent 20%
    );
}

.login-card {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1.15fr 0.95fr;
  width: min(1120px, 100%);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 32px;
  background: rgba(255, 250, 244, 0.18);
  box-shadow: 0 30px 90px rgba(20, 10, 4, 0.35);
  backdrop-filter: blur(20px);
}

.login-copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 18px;
  padding: 54px 48px;
  color: #fff7f0;
  background: linear-gradient(
    160deg,
    rgba(54, 32, 18, 0.52),
    rgba(54, 32, 18, 0.1)
  );
}

.login-eyebrow,
.form-eyebrow {
  margin: 0;
  font-size: 13px;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.login-title {
  margin: 0;
  font-size: clamp(34px, 4vw, 52px);
  line-height: 1.08;
  font-family: "STZhongsong", "KaiTi", serif;
}

.login-subtitle {
  margin: 0;
  max-width: 480px;
  font-size: 15px;
  line-height: 1.9;
  color: rgba(255, 247, 238, 0.84);
}

.login-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.login-tag {
  padding: 10px 14px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 999px;
  font-size: 13px;
  color: rgba(255, 248, 240, 0.92);
  background: rgba(255, 255, 255, 0.1);
}

.login-form-panel {
  padding: 42px 38px;
  background: rgba(255, 252, 247, 0.93);
}

.form-heading {
  margin-bottom: 26px;
}

.form-heading h2 {
  margin: 8px 0 10px;
  font-size: 28px;
  color: #2f241d;
}

.form-heading p:last-child {
  margin: 0;
  color: #7a695c;
}

.login-actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 10px;
}

.action-button {
  height: 48px;
  margin: 0;
  border-radius: 14px;
  font-weight: 700;
}

.action-button.secondary {
  border: 1px solid rgba(124, 85, 53, 0.14);
  color: #5b4536;
  background: rgba(124, 85, 53, 0.06);
}

.captcha-row {
  display: flex;
  width: 100%;
  gap: 12px;
  align-items: center;
}

.captcha-image {
  width: 120px;
  height: 48px;
  border: 1px solid rgba(124, 85, 53, 0.16);
  border-radius: 14px;
  cursor: pointer;
  object-fit: cover;
}

@media (max-width: 960px) {
  .login-card {
    grid-template-columns: 1fr;
  }

  .login-copy {
    padding: 42px 32px 28px;
  }

  .login-form-panel {
    padding: 32px;
  }
}

@media (max-width: 640px) {
  .login-shell {
    padding: 18px;
  }

  .login-copy,
  .login-form-panel {
    padding: 26px 22px;
  }

  .login-actions {
    grid-template-columns: 1fr;
  }

  .captcha-row {
    flex-direction: column;
    align-items: stretch;
  }

  .captcha-image {
    width: 100%;
  }
}
</style>
