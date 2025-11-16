<template class="page-full">
  <div class="login-container">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div>
        <input v-model="email" type="email" placeholder="Email" required />
      </div>
      <div>
        <input
          v-model="password"
          type="password"
          placeholder="Senha"
          required
        />
      </div>
      <button type="submit">Entrar</button>

      <div class="register-redirect">
        <a @click="goToRegister">Não têm uma conta?</a>
      </div>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import api from "../services/axios.js";
import "../styles/LoginRegister/LoginRegister.css";

export default {
  name: "Login",
  data() {
    return { email: "", password: "", errorMessage: "" };
  },
  methods: {
    async login() {
      try {
        const response = await api.post("/auth/login", {
          email: this.email,
          password: this.password,
        });
        const user = response.data;
        const token = response.data.token;

        api.defaults.headers.Authorization = `Bearer ${token}`;
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);
        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro login:", error);
        this.errorMessage = "Email ou senha inválidos!";
      }
    },
    goToRegister() {
      this.$router.push("/register");
    },
  },
};
</script>
