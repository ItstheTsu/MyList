import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import api from "./services/api";

const token = localStorage.getItem("token");
if (token) {
  api.defaults.headers.Authorization = `Bearer ${token}`;
}

createApp(App).use(router).mount("#app");