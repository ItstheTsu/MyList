import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import Dashboard from "../views/Dashboard.vue";
import Register from "../views/Register.vue";
import Config from "../views/Config.vue";
import BackupPassword from "../views/RecoverPassword.vue";

const routes = [
  {
    path: "/Register",
    name: "Register",
    component: Register,
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard,
  },
  {
    path: "/config",
    name: "Configurações",
    component: Config,
  },
  {
    path: "/recover-password",
    name: "RecoverPassword",
    component: BackupPassword,
  },
  {
    path: "/",
    redirect: "/Register",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;