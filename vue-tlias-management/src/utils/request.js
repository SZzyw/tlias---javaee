import axios from "axios";
import { ElMessage } from "element-plus";
import { clearAuth } from "@/utils/auth";

const request = axios.create({
  baseURL: "/api",
  timeout: 600000,
});

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.token = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.msg;
    if (status === 401) {
      clearAuth();
      if (window.location.pathname !== "/login") {
        ElMessage.error("登录状态已失效，请重新登录");
        window.location.href = "/login";
      }
    } else if (status === 403) {
      ElMessage.error(message || "无权限访问");
    } else if (status === 429) {
      ElMessage.warning(message || "请求过于频繁");
    }
    return Promise.reject(error);
  }
);

export default request;
